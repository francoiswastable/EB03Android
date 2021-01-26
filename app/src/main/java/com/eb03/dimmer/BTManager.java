/**
 * @author François Wastable
 */
package com.eb03.dimmer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Classe qui contient le code spécifique au Bluetooth en implémentant les threads de connexion et
 * de transfert des données
 */
public class BTManager extends Transceiver {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mAdapter;

    private BluetoothSocket mSocket = null;

    private ConnectThread mConnectThread = null;
    private WritingThread mWritingThread = null;


    /**
     * Méthode de lancement de connexion au device
     * @param id id du device
     */
    @Override
    public void connect(String id) {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(id);
        disconnect();
        mConnectThread = new ConnectThread(device);
        setState(STATE_CONNECTING);
        mConnectThread.start();
    }

    /**
     * Méthode de lancement de déconnexion au device
     * */
    @Override
    public void disconnect() {

    }

    /**
     * Méthode d'envoi de données au device
     * @param b octets de données à envoyer au device
     */
    @Override
    public void send(byte[] b) {

    }

    /**
     * Classe du Thread de connexion au device
     */
    private class ConnectThread extends Thread{

        /**
         * Thread de connexion au socket avec le numéro UUID
         * @param device device sélectionné
         * @exception e si il y a une erreur
         */
        @SuppressWarnings("JavadocReference")
        public ConnectThread(@NotNull BluetoothDevice device) {


            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * Méthode de déroulement de la connexion
         * @exception e si il y a une erreur de connection
         */
        @Override
        public void run() {
            mAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (IOException e) {
                disconnect();
            }
            mConnectThread = null;
            startReadWriteThreads();

        }

    }

    /**
     * Méthode de lancement des threads d'écriture et de lecture
     * en réalité ici il n'y a que l'écriture qui est utilisé
     */
    private void startReadWriteThreads(){
        // instanciation d'un thread de lecture
        mWritingThread = new WritingThread(mSocket);
        Log.i("ConnectThread","Thread WritingThread lancé");
        mWritingThread.start();
        setState(STATE_CONNECTED);
    }

    /**
     * Classe du Thread d'écriture vers le device
     */
    private class WritingThread extends Thread{
        private OutputStream mOutStream;
        ByteRingBuffer mBuffer = new ByteRingBuffer(1024);

        /**
         * Thread d'écriture sur le socket
         * @param mSocket socket sur lequel écrire
         */
        public WritingThread(BluetoothSocket mSocket) {
            try {
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Méthode de lancement de déroulement de l'écriture sur le socket à partir des
         * données contenues dans le buffer circulaire
         * @exception e en cas d'erreur lors de la lecture du buffer ou de l'écriture sur le socket
         */
        @Override
        public void run() {
            int bytes;
            while(mSocket != null){
                    try {
                        bytes = mBuffer.get();
                        mOutStream.write(bytes);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        }

        /**
         *
         * @param bytes octets de données à envoyer vers le socket
         * @exception e en cas d'erreur lors de l'écriture sur le socket
         */
         public void write(byte[] bytes) {
            try {
                mOutStream.write(bytes);
            }  catch (Exception e) {
                e.printStackTrace();
            }

         }



    }




}
