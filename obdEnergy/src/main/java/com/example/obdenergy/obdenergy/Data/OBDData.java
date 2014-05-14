package com.example.obdenergy.obdenergy.Data;

/**
 * Created by sumayyah on 5/13/14.
 */
public class OBDData {

    public String initFuel = "";
    public String finalFuel = "";
    public String initDistance = "";
    public String finalDistance = "";
    public String initTimestamp = "";
    public String finalTimestamp = "";
    public Boolean city = false;
    public Boolean highway = false;

    public OBDData(){}

    public void setInitFuel(String val){ initFuel = val;}
    public void setFinalFuel(String val){ finalFuel = val;}
    public void setInitDistance(String val){ initDistance = val;}
    public void setFinalDistance(String val){ finalDistance = val;}
    public void setInitTimestamp(String val){ initTimestamp = val;}
    public void setFinalTimestamp(String val){ finalTimestamp = val;}

    public String getInitFuel(){ return initFuel; }
    public String getFinalFuel(){ return finalFuel; }
    public String getInitDistance(){ return initDistance; }
    public String getFinalDistance(){ return finalDistance; }
    public String getInitTimestamp(){ return initTimestamp; }
    public String getfinalTimestamp(){ return finalTimestamp; }
}
