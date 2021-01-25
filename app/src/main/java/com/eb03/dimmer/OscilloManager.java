package com.eb03.dimmer;

public class OscilloManager {

    private static final OscilloManager mOscilloManager = new OscilloManager();

    private float mAlpha ;
    private OscilloEventsListener mOscilloEventsListener;
    private Transceiver.TransceiverListener tranceiverListener ;
    private Transceiver tranceiver;
    private byte[] command;
    private OscilloManager(){


    }

    public  interface OscilloEventsListener{

    }

    private int getStatus(){

        return tranceiver.getState();
    }

    private void setCalibrationDutyCycle(float alpha){
        command[0]= 0x0A;
        command[1] = (byte) alpha;
    }

    public static final OscilloManager getInstance(){
        return mOscilloManager;
    }

}

