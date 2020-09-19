package com.example.monitoramento;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Set;

public class ListaDispositivos extends ListActivity {

    private BluetoothAdapter btAdabter = null;
    static String MEC_ADDRESS = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        btAdabter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosPareados = btAdabter.getBondedDevices();

        if(dispositivosPareados.size() > 0){
            for(BluetoothDevice disp : dispositivosPareados){
                String nomeBt = disp.getName();
                String macBt = disp.getAddress();
                ArrayBluetooth.add(nomeBt + "\n" + macBt);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String informacaoGeral = ((TextView)v).getText().toString();
        //Toast.makeText(getApplicationContext(), "Info: " + informacaoGeral, Toast.LENGTH_LONG).show();

        String mecAdd = informacaoGeral.substring(informacaoGeral.length() -17);

        Intent retornaMac = new Intent();
        retornaMac.putExtra(MEC_ADDRESS, mecAdd);
        setResult(RESULT_OK, retornaMac);
        finish();
    }
}
