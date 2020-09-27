package com.example.monitoramento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.monitoramento.data.model.ResumoModelo;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.DistanceSinceCCCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.TemperatureCommand;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static com.example.monitoramento.R.drawable.btn_fundo_vermelho_alerta;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static int REQUEST_ENABLE_BT = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static final int MESSAGE_READ = 3;
    private boolean conexao = false;
    private static String MAC = null;
    public double nivel_inicial;
    public boolean nivel_inicial_coletado = false;
    public boolean distancia_inicial_coletada = false;
    public int distancia_inicial = 0;
    public double notaConducao = 10.0;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


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
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;

        try{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Marker in local position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
            Toast.makeText(this, "erro", Toast.LENGTH_LONG).show();
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Log.e("REC", "locationListener " + locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                            //new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());

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
        Button temperatura;
        Button consumoMed;
        Button distancia;
        Button nota;
        Button aceleracao;
        public ResumoModelo resumo = new ResumoModelo();

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

            //Throttle Position
            ThrottlePositionCommand  throttlePosition = new ThrottlePositionCommand();

            //Fuel Level
            FuelLevelCommand fuelLevel  = new FuelLevelCommand();

            //Coolant Temperature
            EngineCoolantTemperatureCommand coolantTemperature = new EngineCoolantTemperatureCommand();

            //Distance
            DistanceMILOnCommand milOnCommand = new DistanceMILOnCommand();

            //Distance since command
            DistanceSinceCCCommand distanceSinceCCCommand = new DistanceSinceCCCommand();


            while (!Thread.currentThread().isInterrupted())
            {
                try {
                    engineRpmCommand.run(mmInStream, mmOutStream);
                    speedCommand.run(mmInStream, mmOutStream);
                    coolantTemperature.run(mmInStream, mmOutStream);
                    throttlePosition.run(mmInStream, mmOutStream);
                    fuelLevel  = new FuelLevelCommand();
                    fuelLevel.run(mmInStream, mmOutStream);
                    milOnCommand.run(mmInStream, mmOutStream);
                    distanceSinceCCCommand.run(mmInStream, mmOutStream);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // TODO handle commands result
                Log.e("TAG", "RPM: " + engineRpmCommand.getFormattedResult());
                Log.e("TAG", "Speed: " + speedCommand.getFormattedResult());
                Log.e("TAG", "Coolant: " + coolantTemperature.getFormattedResult());
                Log.e("TAG", "throttlePosition: " + throttlePosition.getFormattedResult());
                Log.e("TAG", "distanceSinceCCCommand: " + distanceSinceCCCommand.getFormattedResult());
                Log.e("TAG", "fuelLevel: " + fuelLevel.getFormattedResult());

                showRpm(engineRpmCommand.getFormattedResult(), engineRpmCommand.getRPM());

                showVel(speedCommand.getFormattedResult());

                showTemp(coolantTemperature.getFormattedResult());

                showConsum(fuelLevel.getFuelLevel());

                showDist(distanceSinceCCCommand.getKm());

                verifyThrottlePosition(throttlePosition.getPercentage());
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

        public void verifyThrottlePosition(final float porcentagemPedal){

            try{
                aceleracao = (Button)findViewById(R.id.btn_aceleracao);

                aceleracao.post(new Runnable(){
                    @Override
                    public void run() {
                        aceleracao.setText(Double.toString(notaConducao));
                        if(porcentagemPedal > 70.0){
                            updateNota();
                            aceleracao.setBackgroundResource(btn_fundo_vermelho_alerta);
                        }
                        else
                            aceleracao.setBackgroundResource(R.drawable.btn_info_adicional);
                    }
                });

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erro ao verificar Aceleração" , Toast.LENGTH_LONG).show();
            }



        }
        public void showVel(final String velAmostragem) {

            try{
                velocidade = (Button)findViewById(R.id.btn_Velocidade);
                velocidade.post(new Runnable(){
                    @Override
                    public void run() {
                        velocidade.setText(velAmostragem);
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erro ao verificar Velocidade" , Toast.LENGTH_LONG).show();
            }

        }

        public void showRpm(final String rpmAmostragem, final int rpmPuro) {

            rpm = (Button)findViewById(R.id.btn_Rotacao);
            rpm.post(new Runnable(){
                @Override
                public void run() {
                    rpm.setText(rpmAmostragem);
                    if (rpmPuro >= 3000) {
                        rpm.setBackgroundResource(btn_fundo_vermelho_alerta);
                        updateNota();
                    }else {
                        rpm.setBackgroundResource(R.drawable.btn_info_adicional);
                    }

                }
            });

        }


        public void updateNota() {

            try{
                nota = (Button)findViewById(R.id.btn_nota);
                notaConducao -= 0.5;
                resumo.notaConducao = notaConducao;
                nota.post(new Runnable(){
                    @Override
                    public void run() {
                        nota.setText(Double.toString(notaConducao));
                        if(notaConducao <= 5)
                            nota.setBackgroundResource(btn_fundo_vermelho_alerta);
                        else
                            nota.setBackgroundResource(R.drawable.btn_info_adicional);
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erro ao atualizar nota da condução" , Toast.LENGTH_LONG).show();
            }

        }

        public void showTemp(final String temp) {
            try {
                temperatura = (Button)findViewById(R.id.btn_temperatura);
                temperatura.post(new Runnable(){
                    @Override
                    public void run() {
                        temperatura.setText(temp);
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erro ao verificar temperatura" , Toast.LENGTH_LONG).show();
            }

        }

        public void showDist(final int dist) {

            try{
                if(!distancia_inicial_coletada)
                {
                    distancia_inicial = dist;
                    distancia_inicial_coletada = true;
                }
                final int res = dist - distancia_inicial;
                resumo.distancia = res;
                distancia = (Button)findViewById(R.id.btn_distancia);
                distancia.post(new Runnable(){
                    @Override
                    public void run() {
                        distancia.setText(Integer.toString(res));
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erro ao verificar distancia" , Toast.LENGTH_LONG).show();
            }

        }

        public void showConsum(final Float nivel) {

            try{
                if(!nivel_inicial_coletado){
                    nivel_inicial = nivel;
                    nivel_inicial_coletado = true;
                }
                resumo.nivelTanque = nivel_inicial - nivel;
                consumoMed = (Button)findViewById(R.id.btn_Consumo_med);
                consumoMed.post(new Runnable(){
                    @Override
                    public void run() {
                        consumoMed.setText( String.format("%.1f", (nivel_inicial - nivel)) + '%' );
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Erro ao calcular consumo" , Toast.LENGTH_LONG).show();
            }

        }
    }
}

