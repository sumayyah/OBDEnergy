package com.example.obdenergy.obdenergy.Utilities;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

/**
 * Created by sumayyah on 5/7/14.
 */
public class DataLogger {
    public static void writeData(final String data) {


        // ++++ Fire off a thread to write info to file

        Thread w_thread = new Thread() {
            public void run() {
//                File myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/CarData/files");
                File myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/storage/emulated/0/Android/carData");
                myFilesDir.mkdirs();

                String dataline = (timestamp() + ", " + data + "\n");

                File myfile = new File(myFilesDir + "/" + "Term_Log" + sDate() + ".txt");
//                File myfile = new File("carData.txt");
                if(myfile.exists() == true)
                {
                    try {
                        FileWriter write = new FileWriter(myfile, true);
                        write.append(dataline);
                        //read_ct++;
                        write.close();
                    }catch (Exception e){

                    }

                }else{ //make a new file since we apparently need one
                    try {
                        FileWriter write = new FileWriter(myfile, true);
                        //	write.append(header);
                        write.append(dataline);
                        //read_ct++;
                        write.close();
                    }catch (Exception e){

                    }

                }
            }
        };
        w_thread.start();
    }

    public static long timestamp(){
        long timestamp = System.currentTimeMillis();
        return timestamp;
    }

    @SuppressLint("SimpleDateFormat")
    public static String sDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date= new java.util.Date();
        String sDate = sdf.format(date.getTime());
        return sDate;
    }

}
