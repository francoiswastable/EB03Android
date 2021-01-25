package com.eb03.dimmer;

import java.util.Arrays;

public class ByteRingBuffer {

    private int readIndex ;
    private int writeIndex ;
    private int size;
    private int maxSize;

    private byte[] buffer;

    public ByteRingBuffer(int maxSize){
        buffer= new byte[maxSize];
        readIndex = 0;
        writeIndex = -1;
        size=0;
    }

    public void put(byte data) throws Exception {
        int w = (writeIndex+1)% maxSize;
        if (size == maxSize){
            throw new Exception("Le buffer est plein");
        }
        size++;
        buffer[w]= data;
        writeIndex= (writeIndex+1) % maxSize;

    }

    public void put(byte[] data) throws Exception {
        for (int i = 0; i < data.length; i++) {
            int w = (writeIndex + 1) % maxSize;

            if (size == maxSize) {
                throw new Exception("Le buffer est plein");
            }
            size++;
            buffer[w] = data[i];
            writeIndex = (writeIndex + 1) % maxSize;
        }
    }

    public byte get() throws Exception {
        byte r ;
        if (size ==0){
            throw new Exception("Le buffer est vide");
        }
        r = buffer[readIndex];
        readIndex= (readIndex+1) % maxSize;
        size--;
        return r;
    }

    public byte[] bytesToRead() {
        return buffer;
    }

    @Override
    public String toString() {
        return "ByteRingBuffer{" +
                "readIndex=" + readIndex +
                ", writeIndex=" + writeIndex +
                ", size=" + size +
                ", maxSize=" + maxSize +
                ", buffer=" + Arrays.toString(buffer) +
                '}';
    }

}
