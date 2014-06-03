package com.example.obdenergy.obdenergy.Utilities;

import com.example.obdenergy.obdenergy.MainActivity;

import java.util.ArrayList;

/**
 * Created by Sumayyah on 5/26/2014.
 *
 *
 * This creates a Queue singleton to buffer all requests and messages to and from OBD
 */


public class Queue {

    ArrayList<String> messageQueue;
    private static MainActivity activity;
    private static String classID = "Queue ";

    private static Queue instance = null;

    protected Queue(MainActivity activity)
    {
        messageQueue = new ArrayList<String>();
        this.activity = activity;
    }

    public static Queue getInstance(MainActivity activity)
    {
        if(instance == null)
            instance = new Queue(activity);
        return instance;
    }

    public void add(String msg)
    {
        messageQueue.add(msg);
        Console.log(classID+"added "+msg+" Queue is: "+readQueue());
    }

    public void dequeue(){
        Console.log(classID+"called deuqueue");
        if(!messageQueue.isEmpty()){
            String message = messageQueue.remove(0);
//            activity.sendMessage(message);
            Console.log(classID+"not empty, sent message "+message);
        } else Console.log(classID+"queue empty");
    }

    public void clearQueue()
    {
        messageQueue.clear();
    }

    private String readQueue(){
        String ans = "";
        for(String msg: messageQueue)
            ans+=msg;

        return ans;
    }

}
