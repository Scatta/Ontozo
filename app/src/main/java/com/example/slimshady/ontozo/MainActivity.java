package com.example.slimshady.ontozo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.data.LineData;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private AsyncSocket socket = null;

    private EditText addressEdit;
    private EditText portEdit;
    private AsyncServer server = null;

    private TextView temperatureText;
    private TextView humidityText;
    private TextView lightText;

    private TextView statusText;
    private ImageView statusImg;

    public ArrayList<Double> humidityArray = new ArrayList<>();
    public ArrayList<Double> temperatureArray = new ArrayList<>();
    public ArrayList<Double> lightArray = new ArrayList<>();
    public ArrayList<String> timeArray = new ArrayList<>();

    public CommandCode motorActual = CommandCode.OFF;

    private Boolean enabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressEdit = (EditText) findViewById(R.id.address_edit);
        portEdit = (EditText) findViewById(R.id.port_edit);

        statusText = (TextView) findViewById(R.id.status_field);
        statusImg = (ImageView) findViewById(R.id.status_img);

        temperatureText = (TextView) findViewById(R.id.temperature_text);
        humidityText = (TextView) findViewById(R.id.humidity_text);
        lightText = (TextView) findViewById(R.id.light_text);

        humidityArray.add(10.2);
        humidityArray.add(10.0);
        humidityArray.add(10.0);
        humidityArray.add(9.9);
        humidityArray.add(9.8);
        humidityArray.add(9.8);
        humidityArray.add(9.6);
        humidityArray.add(9.5);

        temperatureArray.add(30.1);
        temperatureArray.add(30.2);
        temperatureArray.add(30.4);
        temperatureArray.add(30.5);
        temperatureArray.add(30.5);
        temperatureArray.add(30.6);
        temperatureArray.add(30.8);
        temperatureArray.add(41.0);

        lightArray.add(72.2);
        lightArray.add(72.1);
        lightArray.add(71.1);
        lightArray.add(72.1);
        lightArray.add(72.2);
        lightArray.add(72.3);
        lightArray.add(73.5);
        lightArray.add(73.4);

        timeArray.add("2016-04-25 10:00:00");
        timeArray.add("2016-04-25 10:10:00");
        timeArray.add("2016-04-25 10:20:00");
        timeArray.add("2016-04-25 10:30:00");
        timeArray.add("2016-04-25 10:40:00");
        timeArray.add("2016-04-25 10:50:00");
        timeArray.add("2016-04-25 11:00:00");
        timeArray.add("2016-04-25 11:10:00");

        Button connectButton = (Button) findViewById(R.id.connect_button);
        if (connectButton != null) {
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectSocket();
                }
            });
        }

        final Button motorButton = (Button) findViewById(R.id.motor_button);
        if (motorButton != null) {
            motorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(motorActual==CommandCode.OFF) {
                        MotorCommand startCommand = new MotorCommand(CommandCode.ON);
                        writeSocket(startCommand.toJsonString());
                        motorActual=CommandCode.ON;
                        motorButton.setText("Öntöző ki");
                    }
                    else if(motorActual==CommandCode.ON) {
                        MotorCommand stopCommand = new MotorCommand(CommandCode.OFF);
                        writeSocket(stopCommand.toJsonString());
                        motorActual=CommandCode.OFF;
                        motorButton.setText("Öntöző be");
                    }
                }
            });
        }

        Button weatherButton = (Button) findViewById(R.id.weather_button);
        if (weatherButton != null) {
            weatherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, WeatherActivity.class);
                    startActivity(i);
                }
            });
        }

        Button chartButton = (Button) findViewById(R.id.show_button);
        if (chartButton != null) {
            chartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, ChartActivity.class);
                    i.putExtra("temp", temperatureArray);
                    i.putExtra("hum", humidityArray);
                    i.putExtra("light", lightArray);
                    i.putExtra("time", timeArray);
                    startActivity(i);
                }
            });
        }
    }

    private void stateMachine(){
        double actTemp = temperatureArray.get(temperatureArray.size()-1);
        double actHum = humidityArray.get(humidityArray.size()-1);
        double actLight = lightArray.get(lightArray.size()-1);
        if ((0.03 < actHum) && (actHum < 0.4) && (10 < actTemp && actTemp < 35)){
            if (motorActual == CommandCode.ON) {
                MotorCommand stopCommand = new MotorCommand(CommandCode.OFF);
                writeSocket(stopCommand.toJsonString());
            }
            statusText.setText("A Park vidám");
            statusImg.setImageResource(R.drawable.happy);
//          statusText.setText("A Park szomorú");
//          statusImg.setImageResource(R.drawable.sad);
        }
        if ((actTemp <= 10) && (0.03 < actHum) && (actHum < 0.4)){
            if (motorActual == CommandCode.ON) {
                MotorCommand stopCommand = new MotorCommand(CommandCode.OFF);
                writeSocket(stopCommand.toJsonString());
            }
            statusText.setText("A Park fázik");
            statusImg.setImageResource(R.drawable.cold);
        }
        if ((actTemp >= 35) && (0.03 < actHum && actHum < 0.4)){
            if (motorActual == CommandCode.ON) {
                MotorCommand stopCommand = new MotorCommand(CommandCode.OFF);
                writeSocket(stopCommand.toJsonString());
            }
            statusText.setText("A Parknak melege van");
            statusImg.setImageResource(R.drawable.hot);

        }
        if ((actHum < 0.01) && (actLight < 0.3)){
            if (motorActual == CommandCode.ON) {
                MotorCommand stopCommand = new MotorCommand(CommandCode.OFF);
                writeSocket(stopCommand.toJsonString());
            }
            statusText.setText("Öntözés");
            statusImg.setImageResource(R.drawable.irrigation);
        }
        if ((actHum == 0) || (actHum > 0.6)){
            statusText.setText("Riszatás");
            statusImg.setImageResource(R.drawable.alarm);
        }
        if ((actHum < 0.01) && (actLight > 0.3)){
            statusText.setText("A Park szomjas");
            statusImg.setImageResource(R.drawable.thirsty);
        }
    }

    private void writeSocket(String json) {
        if (socket != null && socket.isOpen()) {
            ByteBufferList bbl = new ByteBufferList(ByteBuffer.wrap(json.getBytes()));
            socket.write(bbl);
        } else {
            Toast.makeText(MainActivity.this, "Socket is not opened.", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectSocket(){
        if (server == null) {
            server = new AsyncServer();
        }
        String address = addressEdit.getText().toString();
        int port = Integer.parseInt(portEdit.getText().toString());
        if (socket != null && socket.isOpen()) {
            Toast.makeText(MainActivity.this, "Already connected.", Toast.LENGTH_SHORT).show();
            return;
        }
        server.connectSocket(new InetSocketAddress(address, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, AsyncSocket socket) {
                if (ex != null) {
                    Log.e(TAG, "connection error", ex);
                    return;
                }
                Log.i(TAG, "connected to socket");
                MainActivity.this.socket = socket;
                socket.setDataCallback(new DataCallback() {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                        Log.d(TAG, "Data available! ");
                        try {
                            if (bb.size() > 0) {
                                byte[] data = bb.getAllByteArray();
                                String received = new String(data);
                                try {
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String formattedDate = df.format(c.getTime());
                                    ObjectMapper mapper = new ObjectMapper();
                                    final SensorData receivedData = mapper.readValue(received, SensorData.class);
                                    humidityArray = fillDoubleData(humidityArray, receivedData.humidity);
                                    temperatureArray = fillDoubleData(temperatureArray, receivedData.temperature);
                                    lightArray = fillDoubleData(lightArray, receivedData.light);
                                    timeArray = fillStringData(timeArray,formattedDate);
                                    stateMachine();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            temperatureText.setText(receivedData.temperature + "°C");
                                            humidityText.setText(receivedData.humidity + "%");
                                            lightText.setText(receivedData.light + " lux");
                                        }
                                    });

                                } catch (Exception e) {
                                    Log.e(TAG, "Deserialization error %s", e);
                                }
                            } else {
                                Log.d(TAG, "NoMore!");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "socket data receive error: " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private ArrayList<Double> fillDoubleData(ArrayList<Double> values, double newValue){
        ArrayList<Double> newValues = new ArrayList<>();
        if (values.size() >= 50) {
            for (int i = 1; i < values.size(); i++) {
                newValues.add(values.get(i));
            }
        } else {
            for (int i = 0; i < values.size(); i++) {
                newValues.add(values.get(i));
            }
        }
        newValues.add(newValue);
        return newValues;
    }

    private ArrayList<String> fillStringData(ArrayList<String> values, String newValue){
        ArrayList<String> newValues = new ArrayList<>();
        if (values.size() >= 50) {
            for (int i = 1; i < values.size(); i++) {
                newValues.add(values.get(i));
            }
        } else {
            for (int i = 0; i < values.size(); i++) {
                newValues.add(values.get(i));
            }
        }
        newValues.add(newValue);
        return newValues;
    }
}

