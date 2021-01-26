/**
 * @author François Wastable
 */
package com.eb03.dimmer;

/**
 * Classe abstraite abstraite implémentant principalement une interface de
 * listener (TransceiverListener) sur les données de trames réceptionnées et le changement d’état
 * de connexion avec l'oscilloscope
 */
public abstract class Transceiver {

   // Définition des trois états de connexion possibles
   public static final int STATE_NOT_CONNECTED = 0; // non connecté
   public static final int STATE_CONNECTING = 1;    // connexion en cours
   public static final int STATE_CONNECTED = 2;     // connecté


   private int mState; // état de connexion
   private TransceiverListener mTransceiverListener; // listener du transceiver
   private FrameProcessor mFrameProcessor; // l'organe permettant l'encodage de données

    /**
     * Setter du listener du transceiver
     * @param transceiverListener listener du transceiver
     */
   public void setTransceiverListener(TransceiverListener transceiverListener){
       mTransceiverListener = transceiverListener;
   }

    /**
     * Méthode d'implémentation d'un FrameProcessor permettant l'encodage de données
     * @param frameProcessor organe permettant l'encodage de données
     */
   public void attachFrameProcessor(FrameProcessor frameProcessor){
       mFrameProcessor = frameProcessor;
   }

    /**
     * Méthode qui retire le FrameProcessor implémenté
     */
    public void detachFrameProcessor(){
        mFrameProcessor = null;
    }

    /**
     * Getter de l'état de connexion
     * @return L'état de connexion
     */
    public int getState() {
        return mState;
    }

    /**
     * Setter de l'état de connexion
     * @param state l'état de connexion à setter
     */
    public void setState(int state) {
        mState = state;
        if(mTransceiverListener != null){
            mTransceiverListener.onTransceiverStateChanged(state);
        }
    }

    /*********************************************************************************************
     *
     *                                         INTERFACES
     *
     ********************************************************************************************/
    /**
     * Interface de listener du transceiver
     */
    public interface TransceiverListener{
       void onTransceiverDataReceived();
       void onTransceiverStateChanged(int state);
       void onTransceiverConnectionLost();
       void onTransceiverUnableToConnect();
   }


    /*********************************************************************************************
     *
     *                                         METHODES ABSTRAITES
     *
     ********************************************************************************************/
    /**
     * Méthode abstraite de connexion
     * @param id id de connexion
     */
    public abstract void connect(String id);

    /**
     * Méthode abstraite de déconnexion
     */
    public abstract void disconnect();

    /**
     * Méthode abstraite d'envoi de données
     * @param b tableau de données à envoyer
     */
    public abstract void send(byte[] b);


}
