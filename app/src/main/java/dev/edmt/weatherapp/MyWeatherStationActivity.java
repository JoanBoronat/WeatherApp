package dev.edmt.weatherapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import dev.edmt.weatherapp.Common.Common;
import dev.edmt.weatherapp.Model.ListOWM;
import dev.edmt.weatherapp.Model.MyWeatherStation;


public class MyWeatherStationActivity extends AppCompatActivity {

    String mac = null;
    BluetoothDevice device = null;
    final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //UUID for serial connection
    BluetoothSocket socket = null;
    MyWeatherStation station = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_weather);

        getSupportActionBar().setTitle("My weather station");

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mac = sharedPref.getString(getString(R.string.device_mac), null);

        if (mac == null || !mac.isEmpty()) {
            requestDeviceCreds();
        } else {
            connectBluetooth();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                readInput();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void connectBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // Device does not support Bluetooth
            finish(); //exit
        }

        if (!adapter.isEnabled()) {
            //make sure the device's bluetooth is enabled
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);
        }

        device = adapter.getRemoteDevice(mac); //get remote device by mac, we assume these two devices are already paired

        try {
            socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
            socket.connect();
        } catch (IOException e) {
        }

        readInput();
    }

    private void readInput() {
        // Get a BluetoothSocket to connect with the given BluetoothDevice

        InputStream in = null;

        try {
            in = socket.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            if ((line = r.readLine()) != null) {
                Gson gson = new Gson();
                Type mType = new TypeToken<MyWeatherStation>() {
                }.getType();
                station = gson.fromJson(line, mType);
                Log.e("MY", line);
                updateUI();
            }

        } catch (IOException e) {
        } catch (NullPointerException e) {
        }
    }

    private void updateUI() {
        TextView txtTemp, txtHumi, txtLux;

        txtTemp = (TextView) findViewById(R.id.temp);
        txtHumi = (TextView) findViewById(R.id.humidity);
        txtLux = (TextView) findViewById(R.id.luminosity);

        txtTemp.setText(String.format("Temperature: %.2f ºC", station.getTemperature()));
        txtHumi.setText(String.format("Humidity: %.2f", station.getHumidity()));
        txtLux.setText(String.format("Luminosity: %.2f lux", station.getLuminosity()));
    }

    private void requestDeviceCreds() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getString(R.string.device_credentials));
        final EditText inputMac = new EditText(this);
        inputMac.setInputType(InputType.TYPE_CLASS_TEXT);
        inputMac.setMaxLines(1);
        inputMac.setSingleLine(true);
        alert.setView(inputMac, 32, 0, 32, 0);

        alert.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mac = inputMac.getText().toString();
                if (!mac.isEmpty()) {
                    saveCredentials(mac);
                    connectBluetooth();
                }
            }
        });

        alert.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancelled
            }
        });
        alert.show();
    }

    private void saveCredentials(String mac) {

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.device_mac), mac);
        editor.commit();
    }

}
