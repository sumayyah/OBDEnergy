package com.example.obdenergy.obdenergy.Utilities;

/**
 * Created by Sumayyah on 5/26/2014.
 *
 *
 * This creates a Queue singleton to buffer all requests and messages to and from OBD
 */


public class Queue {

    private static Queue instance = null;
    protected Queue()
    {

    }

    public static Queue getInstance()
    {
        if(instance == null)
            instance = new Queue();
        return instance;
    }

}
