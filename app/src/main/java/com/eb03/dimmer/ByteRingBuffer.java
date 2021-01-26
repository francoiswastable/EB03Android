/**
 * @author François Wastable
 */
package com.eb03.dimmer;

import java.util.Arrays;

/**
 * Classe de création d’un buffer circulaire servant de tampon de stockage des données
 * à transmettre à l’oscilloscope
 */
public class ByteRingBuffer {

    private int readIndex ; // index de lecture
    private int writeIndex ; // index d'écriture
    private int size; // taille correspondant au nombre d'emplacement lisible
    private int maxSize; // taille maximale du buffer

    private byte[] buffer;

    /**
     * Constructeur du buffer circulaire
     * @param maxSize taille maximale du buffer
     */
    public ByteRingBuffer(int maxSize){
        buffer= new byte[maxSize];
        readIndex = 0;
        writeIndex = -1;
        size=0;
    }

    /**
     * Méthode d'écriture d'une donnée dans un emplacement du buffer
     * au niveau de l'index d'ériture
     * @param data octet de donnée à placé dans le buffer
     * @throws Exception en cas de buffer plein
     */
    public void put(byte data) throws Exception {
        int w = (writeIndex+1)% maxSize; // variable tampon de l'index d'écriture
        if (size == maxSize){
            throw new Exception("Le buffer est plein");
        }
        size++; // une donnée de plus à lire
        buffer[w]= data; // écriture de la donnée
        writeIndex= (writeIndex+1) % maxSize; // incrémentation de l'index d'écriture
    }

    /**
     * Méthode d'écriture de plusieurs données dans plusieurs emplacements du buffer
     * à partir de l'index d'ériture
     * @param data tableau d'octets de données à placées dans le buffer
     * @throws Exception en cas de buffer plein
     */
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

    /**
     * Méthode de lecture de la donnée au niveau de l'emplacement au niveau de l'index de lecture
     * @return la donnée de l'emplacement au niveau de l'index de lecture
     * @throws Exception en cas de buffer vide
     */
    public byte get() throws Exception {
        byte r ; // octet de donnée à lire
        if (size ==0){
            throw new Exception("Le buffer est vide");
        }
        r = buffer[readIndex]; // lecture de la donnée
        readIndex= (readIndex+1) % maxSize; // incrémentation de l'index de lecture
        size--; // une valeur en moins à lire
        return r;
    }

    /**
     *
     * @return la description du buffer circulaire
     */
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
