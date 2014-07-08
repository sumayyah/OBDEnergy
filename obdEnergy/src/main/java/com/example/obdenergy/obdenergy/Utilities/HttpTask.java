package com.example.obdenergy.obdenergy.Utilities;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by sumayyah on 7/7/14.
 */
public class HttpTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        POST(params[0], params[1]);
        return null;
    }

    private void POST(String url, String data) {
//        Console.log("Calling send JSON");
//        Console.log("Url is "+url);
//        Console.log("Data is "+data);

//        JSONObject json = new JSONObject();
//        String url = "http://10.0.2.2:3000/posttest";
        try{

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

//            String json = "[{\"initTimestamp\":\"1402414587670\", \"finalMAF\":655.35,\"treesKilled\":7, \"gallonsUsed\":3, \"carbonUsed\":61},{\"initTimestamp\":\"1401896187867\", \"finalMAF\":655.35,\"treesKilled\":1,\"carbonUsed\":5,\"initFuel\":0,\"gallonsUsed\":7,\"initMAF\":406.65,\"averageSpeed\":55.5,\"finalTimestamp\":\"1402365290\",\"finalFuel\":0}, {\"initTimestamp\":\"1402417236395\",\"carbonUsed\":9, \"initFuel\":0,\"initMAF\":406.65,\"finalFuel\":0,\"treesKilled\":3,\"finalMAF\":655.35,\"gallonsUsed\":6}]";

//            json.put("name", "SUmayyah");
//            json.put("email", "sahmed");
//            StringEntity entity = new StringEntity(json.toString());
            StringEntity entity = new StringEntity(data);

            httpPost.setEntity(entity);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpClient.execute(httpPost);
            Console.log("executed post");

        }
        catch(Exception e){
            e.printStackTrace();
            Console.log("Failed connection");
        }
    }

}
