/**
 * @author François Wastable
 */
package com.eb03.dimmer;

/**
 * Classe de code métier intégrant la méthode de changement de rapport cyclique et gérant
 * l’état de connexion. Elle forme un singleton
 */
public class OscilloManager {

    // Instance unique d'OscilloManager
    private static final OscilloManager mOscilloManager = new OscilloManager();

    private float mAlpha ; // rapport cyclique
    private OscilloEventsListener mOscilloEventsListener; // listener d'évenements de l'oscilloscope
    private Transceiver.TransceiverListener tranceiverListener ; // listener du transceiver
    private Transceiver tranceiver; //  instance de transceiver
    private byte[] command; // données de commande à envoyer à l'oscilloscope

    /**
     * Constructeur privé de OscilloManager
     */
    private OscilloManager(){
    }

    /**
     * Interface de listener d'évenements de l'oscilloscope
     */
    public  interface OscilloEventsListener{

    }

    /**
     * Getter de l'état de connexion
     * @return l'état de connexion
     */
    private int getStatus(){

        return tranceiver.getState();
    }

    /**
     * Méthode de modification du rapport cyclique du signal de calibration de l'oscilloscope
     * @param alpha rapport cyclique à transmettre
     */
    private void setCalibrationDutyCycle(float alpha){
        command[0]= 0x0A; // code de la commande "setCalibrationDutyCycle"
        command[1] = (byte) alpha;
    }

    /**
     * Point d'accès vers l'instance unique d'OscilloManager
     * @return l'instance unique de l'OscilloManager
     */
    public static final OscilloManager getInstance(){
        return mOscilloManager;
    }

}

