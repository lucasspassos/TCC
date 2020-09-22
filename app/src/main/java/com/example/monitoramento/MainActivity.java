package com.example.monitoramento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static int REQUEST_ENABLE_BT = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static final int MESSAGE_READ = 3;
    private boolean conexao =false;
    private static String MAC = null;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice device = null;
    BluetoothSocket socket = null;
    Button btnConexao;
    UUID uuid =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);



        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        btnConexao = (Button)findViewById(R.id.btnConexao);

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        btnConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(conexao){
                    //Desconectar
                    try{
                        socket.close();
                        conexao = false;
                        Log.e("REC", "Device Disconected");
                        Toast.makeText(getApplicationContext(), "Bluetooth Desconectado!" , Toast.LENGTH_LONG).show();
                        btnConexao.setText("Conectar");
                    }catch (IOException e){
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                }else{
                    //Conectar
                    Intent abrelista = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(abrelista, SOLICITA_CONEXAO);

                }
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.e("REC", "ON HANDLE MESSAGE");
                if(msg.what == MESSAGE_READ) {
                    String recebido = (String) msg.obj;

                    dadosBluetooth.append(recebido);

                    Log.e("REC","Dados: "+ dadosBluetooth.toString());

                    if(dadosBluetooth.length() > 0){
                        Log.e("REC","Maior que 0");
                    }
                    else{
                        Log.e("REC","Menor que 0");
                    }



                }
                dadosBluetooth.delete(0,dadosBluetooth.length());
            }
        };

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(-23.5553035, -46.6972816);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.MEC_ADDRESS);
                    //Toast.makeText(getApplicationContext(), "MAC: " + MAC, Toast.LENGTH_LONG).show();
                    device = bluetoothAdapter.getRemoteDevice(MAC);

                    try{

                        socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                        bluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        socket.connect();

                        conexao = true;

                        ConnectThread connectThread = new ConnectThread(socket);

                        try {

                            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                            new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());

                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), "Error on initialize Pire's thread", Toast.LENGTH_LONG).show();
                            Log.e("REC", "Error on initialize Pire's thread: " + e.toString());

                        }

                        try{
                            connectThread.start();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Erro ao iniciar a thread", Toast.LENGTH_LONG).show();
                            Log.e("REC", "Error to start thread: " + e.toString());
                        }


                        Toast.makeText(getApplicationContext(), "Voce Foi Conectado!", Toast.LENGTH_LONG).show();
                        Log.e("REC", "Device Connected");

                        btnConexao.setVisibility(View.GONE);
                        btnConexao.setText("Desconectar");

                        //Intent intent = new Intent(getApplicationContext(), viagem.class);
                        //intent.putExtra("RPM","1000 RPM");

                        //startActivity(intent);


                    }catch (IOException  erro){
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Erro ao conectar! : " + erro, Toast.LENGTH_LONG).show();
                        Log.e("REC", "Error on Connect: " + erro);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter o MAC ", Toast.LENGTH_LONG).show();
                    Log.e("REC", "Error on get MAC");
                }
        }

    }

    private class ConnectThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private String ver;
        Button rpm;
        Button velocidade;

        public ConnectThread(BluetoothSocket socket1) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.

            Log.e("REC", "CREATING THREAD");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmpIn = socket1.getInputStream();
                tmpOut = socket1.getOutputStream();

            } catch (IOException e) {
                Log.e("TAG", "Socket's create() method failed", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.e("REC", "RUN");
            byte[] buffer = new byte[1];
            int bytes;

            //Engine Rotation
            RPMCommand engineRpmCommand = new RPMCommand();
            //Vehicle Speed
            SpeedCommand speedCommand = new SpeedCommand();
            //Error codes pending
            /*PendingTroubleCodesCommand pendingCodes = new PendingTroubleCodesCommand();
            //Throttle Position
            ThrottlePositionCommand  throttlePosition = new ThrottlePositionCommand();
            //Consumpion Rate
            ConsumptionRateCommand consumptionRate = new ConsumptionRateCommand();
            //Fuel Level
            FuelLevelCommand fuelLevel  = new FuelLevelCommand();
            //Coolant Temperature
            EngineCoolantTemperatureCommand coolantTemperature = new EngineCoolantTemperatureCommand();
            */

            while (!Thread.currentThread().isInterrupted())
            {
                try {
                    engineRpmCommand.run(mmInStream, mmOutStream);
                    speedCommand.run(mmInStream, mmOutStream);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // TODO handle commands result
                Log.e("TAG", "RPM: " + engineRpmCommand.getFormattedResult());
                Log.e("TAG", "Speed: " + speedCommand.getFormattedResult());
                //Log.e("TAG", "pendingCodes: " + pendingCodes.getFormattedResult());
                /*Log.e("TAG", "throttlePosition: " + throttlePosition.getFormattedResult());
                Log.e("TAG", "consumptionRate: " + consumptionRate.getFormattedResult());
                Log.e("TAG", "fuelLevel: " + fuelLevel.getFormattedResult());
                Log.e("TAG", "coolantTemperature: " + coolantTemperature.getFormattedResult());*/

                showRpm(engineRpmCommand.getFormattedResult());
                showVel(speedCommand.getFormattedResult());
            }

            while (true) {
                Log.e("REC", "WHILE");
                try {
                    bytes = mmInStream.read(buffer);
                    Log.e("REC", "Bytes" + bytes);

                    String dadosbt = new String(buffer,0,bytes);
                    Log.e("REC", "DATA READ");
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosbt).sendToTarget();

                } catch (IOException e) {
                    Log.e("REC", "FAIL TO RECIEVE DATA");
                    break;
                }
            }
        }


        public void showVel(final String velAmostragem) {
            velocidade = (Button)findViewById(R.id.btn_Velocidade);
            velocidade.post(new Runnable(){
                @Override
                public void run() {
                    velocidade.setText(velAmostragem);
                }
            });
        }

        public void showRpm(final String rpmAmostragem) {
            rpm = (Button)findViewById(R.id.btn_Rotacao);
            rpm.post(new Runnable(){
                @Override
                public void run() {
                    rpm.setText(rpmAmostragem);
                }
            });
        }

    }

}

