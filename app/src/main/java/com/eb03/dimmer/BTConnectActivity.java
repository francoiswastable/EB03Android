/**
 * @author François Wastable
 */
package com.eb03.dimmer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Cette classe gère toutes les actions relevant de la recherche et l’affichage des périphériques
 * appairés, de la gestion du scanner  jusqu’à la mise à jour des périphériques et de leur adresse
 * par l’intermédiaire d’un adaptateur Bluetooth
 */
public class BTConnectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int BT_ACTIVATION_REQUEST_CODE = 0;
    private static final int RN42_COD = 0x1F00;
    private Toolbar mToolbar;
    private Button mScann;
    private ListView mPairedList;
    private ListView mDiscoveredList;
    private BroadcastReceiver mBroadcastReceiver;

    private ArrayAdapter<String> mPairedAdapter;
    private ArrayAdapter<String> mDiscoveredAdapter;
    private ProgressBar mProgressBar;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mBroadcastRegistered;

    private enum Action {START, STOP};

    /**
     * Méthode onCreate dans laquelle sont gérés les éléments du layout de connexion Bluetooth
     * @param savedInstanceState sauvegarde d'état
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_connect);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mProgressBar = findViewById(R.id.progress);

        mScann = findViewById(R.id.scan_button);

        mPairedList = findViewById(R.id.paired);
        mDiscoveredList = findViewById(R.id.discovered);

        mPairedList.setOnItemClickListener(this);
        mDiscoveredList.setOnItemClickListener(this);

        mPairedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDiscoveredAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        mPairedList.setAdapter(mPairedAdapter);
        mDiscoveredList.setAdapter(mDiscoveredAdapter);

        mScann.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice pairedDevice : pairedDevices) {
                mPairedAdapter.add(pairedDevice.getName() + "\n" + pairedDevice.getAddress());
            }
        } else {
            mPairedAdapter.add("pas de périphérique appairé");
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            /**
             * Méthode de réception d'une intention venant du broadcaster
             * et l'action en conséquence
             * @param context context dans lequel tourne le receveur
             * @param intent l'intention reçue du broadcaster
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (mDiscoveredAdapter.getCount() == 0) {
                            mDiscoveredAdapter.add("aucun périphérique trouvé");
                        }
                        mScann.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;

                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) { // &&(device.getBluetoothClass().getDeviceClass() == RN42_COD)
                            mDiscoveredAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                        break;
                }

            }
        };
    }

    /**
     * Méthode de mise en pause de l'adaptateur Bluetooth
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
    /*    if(mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }*/
    }

    /**
     * Méthode de récupération de l'adresse du device lorsqu'on clique sur son nom
     * @param adapterView
     * @param view
     * @param i position du view dans l'adaptateur
     * @param l id de l'item séletionné
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        mBluetoothAdapter.cancelDiscovery();
        String info = ((TextView)view).getText().toString();
        if(info.equals("aucun périphérique trouvé")||info.equals("pas de périphérique appairé")){
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // lecture de l'adresse du device : récupération des 17 derniers caractères
        if(info.length()>17) {
            info = info.substring(info.length()-17);
            intent.putExtra("device",info);
            setResult(RESULT_OK,intent);
            finish();
            return;
        }

        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Méthode d'initialisation du lancement de scanner de périphérique après un appui sur l'item de scan
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan_button:
                if(!mBluetoothAdapter.isEnabled()){
                    Intent BTActivation;
                    BTActivation = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(BTActivation,BT_ACTIVATION_REQUEST_CODE);
                    return;
                }
                toggleBtScan();
               break;


        }

    }

    /**
     * Méthode qui permet le lancement de la méthode du scanner
     * @param requestCode : code d'action Bluetooth demandé
     * @param resultCode : code résultant
     * @param data : intention de retour contenant l'adresse du device sélectionné
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_ACTIVATION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    toggleBtScan();
                } else {
                    Toast.makeText(this, "Le BT doit être activé", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Méthode de lancement et d'arrêt de scanner de périphériques et de la bar de recherche
     */
    private void toggleBtScan(){
        if(mScann.getText().equals("Scanner")){
            btScan(Action.START);
            mProgressBar.setVisibility(View.VISIBLE);
            mScann.setText("Annuler");
        }else{
            btScan(Action.STOP);
            mProgressBar.setVisibility(View.INVISIBLE);
            mScann.setText("Scanner");
        }

    }

    /**
     * Méthode de scanner bluetooth au niveau du broadcaster
     * @param startstop action à effectuer : start ou stop
     */
    private void btScan(Action startstop){
        if(startstop == Action.START){
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver,filter);
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver,filter);
            mBroadcastRegistered = true;
            mBluetoothAdapter.startDiscovery();
        }else{
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastRegistered = false;
            mBluetoothAdapter.cancelDiscovery();
        }
    }




}