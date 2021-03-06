package com.example.obdenergy.obdenergy.Utilities;

import android.os.AsyncTask;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sumayyah on 7/23/2014.
 */
public class DynamoDBTask extends AsyncTask<String, Void, Void> {


    static AmazonDynamoDBClient dbClient;
    private String accessID = "AKIAJC2UAC2RAHNQAQGA";
    private String secretAccessID = "XeWDdnNCs8i0KMKpe06XWBp7qeEbleo82fOmnGNS";
    private String tablename = "UserData";

    private String timestamp;
    private String username;
    private String jsondata;

    @Override
    protected Void doInBackground(String... strings) {

        timestamp = strings[0];
        username = strings[1];
        jsondata = strings[2];

        try {

            sendToDB();
        } catch (Exception e) {
            e.printStackTrace();
            Console.log("doInBackground failed sendToDB");
        }
        return null;
    }

    private void sendToDB() throws Exception {
        Console.log("Send to DB");

        init();
//        createTable();
        sendToTable();

    }

    private void init(){
         /*Set credentials*/
        AWSCredentials awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return accessID;
            }

            @Override
            public String getAWSSecretKey() {
                return secretAccessID;
            }
        };

        /*Initialize database client*/
        dbClient = new AmazonDynamoDBClient(awsCredentials);

        /*Set database in Northern California*/
        Region region = Region.getRegion(Regions.US_WEST_1);
        dbClient.setRegion(region);

        Console.log("Initialized with credentials");
    }

    private void createTable() {

        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
                .withReadCapacityUnits(5L)
                .withWriteCapacityUnits(5L);

        Console.log("Created provisioned throughput");

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tablename)
                .withProvisionedThroughput(provisionedThroughput);

        ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Name").withAttributeType("S"));
        request.setAttributeDefinitions(attributeDefinitions);

        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("Name").withKeyType(KeyType.HASH));
        request.setKeySchema(tableKeySchema);

        dbClient.createTable(request);

        Console.log("Created Table");

        waitForTableToActivate(tablename);
        getTableInformation();

    }

    private void sendToTable() throws Exception{

        try{

//            Map<String, AttributeValue> item = newItem("July23_3:34_PM", "3 gallons of gas", "Shalini");
            Map<String, AttributeValue> item = newItem(timestamp, jsondata, username);
            PutItemRequest putItemRequest = new PutItemRequest(tablename, item);
            PutItemResult putItemResult = dbClient.putItem(putItemRequest);
            Console.log("Result of put: "+putItemResult);

        }
        catch (AmazonServiceException ase) {
            Console.log("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            Console.log("Error Message:    " + ase.getMessage());
            Console.log("HTTP Status Code: " + ase.getStatusCode());
            Console.log("AWS Error Code:   " + ase.getErrorCode());
            Console.log("Error Type:       " + ase.getErrorType());
            Console.log("Request ID:       " + ase.getRequestId());
            ase.printStackTrace();
        } catch (AmazonClientException ace) {
            Console.log("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            Console.log("Error Message: " + ace.getMessage());
        }

    }

    private Map<String, AttributeValue> newItem(String timestamp, String data, String username){

        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

        item.put("Timestamp", new AttributeValue(timestamp));
        
        /*Not Data-index - this attribute will allow data of greater than 2KB*/
        item.put("DataValue", new AttributeValue(data));
        item.put("Username", new AttributeValue(username));

        Console.log("Created new items with "+timestamp+" "+username+" "+jsondata);
        return item;
    }

    private void waitForTableToActivate(String tablename){

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (1 * 60 * 1000);
        Console.log("Waiting for "+tablename+" to become ACTIVE till "+endTime);


        while (System.currentTimeMillis() < endTime) {
            Console.log("Time is "+System.currentTimeMillis());
            try {Thread.sleep(1000 * 2); Console.log("Sleeping");} catch (Exception e) {e.printStackTrace();}
            try {
                Console.log("Starting try");
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tablename);
                Console.log("request");
                TableDescription tableDescription = dbClient.describeTable(request).getTable();
                Console.log("Got description");
                String tableStatus = tableDescription.getTableStatus();
                Console.log("  - current state: " + tableStatus);
                if (tableStatus.equals(TableStatus.ACTIVE.toString())) return;
            } catch (AmazonServiceException ase) {
                Console.log("Caught!");
//                if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false) throw ase;
                Console.log("Error Message:    " + ase.getMessage());
                Console.log("HTTP Status Code: " + ase.getStatusCode());
                Console.log("AWS Error Code:   " + ase.getErrorCode());
                Console.log("Error Type:       " + ase.getErrorType());
                Console.log("Request ID:       " + ase.getRequestId());
            }
        }

        throw new RuntimeException("Table " + tablename + " never went active");
    }

    private void getTableInformation() {

        TableDescription tableDescription = dbClient.describeTable(
                new DescribeTableRequest().withTableName(tablename)).getTable();
        Console.log("Name: " + tableDescription.getTableName() + " \n" +
                "Status: " + tableDescription.getTableStatus() + " \n" +
                "Provisioned Throughput (read capacity units/sec): " + tableDescription.getProvisionedThroughput().getReadCapacityUnits() + " \n" +
                "Provisioned Throughput (write capacity units/sec): " + tableDescription.getProvisionedThroughput().getWriteCapacityUnits());

    }

}
