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


        Thread thread = new Thread() {

            public void run() {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/carData");

                file.mkdirs();

                String dataString = (System.currentTimeMillis()+"\n"+ getDate()+ "\n" + data + "\n");

                File myfile = new File(file + "/" + "Log " + getDate() + ".txt");

                if(myfile.exists() == true) //if file already exists, append more text to it
                {
                    try {
                        FileWriter write = new FileWriter(myfile, true);
                        write.append(dataString);
                        //read_ct++;
                        write.close();
                    }catch (Exception e){

                    }

                }else{ //make a new file
                    try {
                        FileWriter write = new FileWriter(myfile, true);
                        //	write.append(header);
                        write.append(dataString);
                        //read_ct++;
                        write.close();
                    }catch (Exception e){

                    }

                }
            }
        };
        thread.start();
    }

    public static void writeConsoleData(final String data){
        Thread thread = new Thread() {

            public void run() {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/carConsoleData");
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/storage/emulated/0/Android/carConsoleData");
                file.mkdirs();

                String dataString = (System.currentTimeMillis()+"\n"+ data + "\n");

                File myfile = new File(file + "/" + "Console Log" + getDate() + ".txt");

                if(myfile.exists() == true) //if file already exists, append more text to it
                {
                    try {
                        FileWriter write = new FileWriter(myfile, true);
                        write.append(dataString);
                        write.close();
                    }catch (Exception e){

                    }

                }else{ //make a new file
                    try {
                        FileWriter write = new FileWriter(myfile, true);
                        write.append(dataString);
                        write.close();
                    }catch (Exception e){

                    }
                }
            }
        };
        thread.start();
    }
    
    @SuppressLint("SimpleDateFormat")
    public static String getDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date= new java.util.Date();
        String sDate = sdf.format(date.getTime());
        return sDate;
    }

}
