package com.eb03.dimmer;

public class FrameProcessor {
 private byte[] txframe;
 private byte[] rxFrame;



    private void toFrame(byte[] bytes){
        txframe[0]= 0x05;
        txframe[1]=0x00;
        txframe[2]= (byte) bytes.length;
        for (int i = 0; i < bytes.length-1; i++) {
            txframe[3+i]= bytes[i];
        }
        //CTRL à remplir
        byte ctrl = (byte) (bytes.length + 2);
        txframe[2+bytes.length]=ctrl;
        txframe[2+bytes.length+1]= 0x04;
        // Gestion des payloads contenant 0x04 ou 0x05 ou 0x06 à remplir
    }







}
