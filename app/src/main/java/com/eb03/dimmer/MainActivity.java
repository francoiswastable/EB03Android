package com.eb03.dimmer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.BreakIterator;
import java.text.StringCharacterIterator;

public class MainActivity extends AppCompatActivity {

    private final static int BT_CONNECT_CODE = 1;

    private TextView mState;
    private View mCircle_Slider;
    private Button mBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mState = findViewById(R.id.state);
        mCircle_Slider = findViewById(R.id.Circle_Slider);
        mCircle_Slider.setVisibility(View.INVISIBLE);
        mBtn = findViewById(R.id.Bouton_Alpha);
        mBtn.setVisibility(View.INVISIBLE);
        mBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // chargement de la valeur de rapport cyclique

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItem = item.getItemId();
        switch(menuItem){
            case R.id.connect:
                Intent BTConnect;
                BTConnect = new Intent(this,BTConnectActivity.class);
                startActivityForResult(BTConnect,BT_CONNECT_CODE);

        }
        return true;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_CONNECT_CODE:
                if (resultCode == RESULT_OK) {
                    // récupérer l'adresse du device
                    // se connecter
                    String adresse = data.getStringExtra("device");
                    mState.setText(adresse);
                    mCircle_Slider.setVisibility(View.VISIBLE);
                    mBtn.setVisibility(View.VISIBLE);
                }
                break;
            default:
        }


    }

}



