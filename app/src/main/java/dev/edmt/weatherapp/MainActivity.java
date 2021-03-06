package dev.edmt.weatherapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dev.edmt.weatherapp.Common.Common;
import dev.edmt.weatherapp.Common.RESTClient;
import dev.edmt.weatherapp.Model.ListOWM;
import dev.edmt.weatherapp.Model.OpenWeatherMap;


public class MainActivity extends AppCompatActivity {

    TextView txtLastUpdate, txtDescription, txtHumidity, txtSunrise, txtSunset, txtCelsius, txtMinTmp, txtMaxTmp;
    ImageView imageView;

    String last_city;

    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    ListOWM listOWM = new ListOWM();
    FusedLocationProviderClient mFusedLocationClient;

    int MY_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Control
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtSunrise = (TextView) findViewById(R.id.txtSunrise);
        txtSunset = (TextView) findViewById(R.id.txtSunset);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        txtMinTmp = (TextView) findViewById(R.id.txtMinTmp);
        txtMaxTmp = (TextView) findViewById(R.id.txtMaxTmp);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        last_city = sharedPref.getString(getString(R.string.last_city_pref), getString(R.string.default_city));

        getCurrentLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                new GetWeather().execute(Common.apiRequestByCityName(last_city));
                return true;

            case R.id.action_search_city:
                searchCities();
                return true;

            case R.id.action_get_current_location:
                getCurrentLocation();
                return true;

            case R.id.action_custom_sensors:
                Intent intent = new Intent(this, MyWeatherStationActivity.class);
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void searchCities() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getString(R.string.search_title));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setMaxLines(1);
        input.setSingleLine(true);
        alert.setView(input, 32, 0, 32, 0);
        alert.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                if (!result.isEmpty()) {
                    new GetWeather().execute(Common.apiRequestByCityName(result));
                    new GetWeather().execute(Common.apiRequestForecastByCityName(result));
                    saveLocation(result);
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

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.BLUETOOTH
            }, MY_PERMISSION);
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();

                            new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));
                        } else {
                            Log.e("TAG", "No Location");
                            Context context = getApplicationContext();
                            CharSequence text = "Location could not be retrieved";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            new GetWeather().execute(Common.apiRequestByCityName(last_city));
                        }
                    }
                });
    }


    private void saveLocation(String city) {

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (!Objects.equals(last_city, city)) {
            last_city = city;
            editor.putString(getString(R.string.last_city_pref), openWeatherMap.getName());
            editor.commit();
        }
    }




    private class GetWeather extends AsyncTask<String, Void, String> {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            RESTClient http = new RESTClient();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new Gson();

            if (s.contains("error")) {
                Log.e("onPostExecute", "city not found");
                pd.dismiss();
                return;
            }

            if (s.contains("list")) {

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomList customAdapter = new CustomList();

                Type mType = new TypeToken<ListOWM>() {
                }.getType();

                try {
                    listOWM = gson.fromJson(s, mType);
                } catch (JsonParseException e) {
                    Log.e("JSON", e.getMessage());
                }

                listView.setAdapter(customAdapter);

                pd.dismiss();

            } else {

                Type mType = new TypeToken<OpenWeatherMap>() {
                }.getType();

                openWeatherMap = gson.fromJson(s, mType);
                pd.dismiss();

                saveLocation(openWeatherMap.getName());

                getSupportActionBar().setTitle(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));

                txtLastUpdate.setText(String.format("Last update: %s", Common.getDateNow()));
                txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription().substring(0, 1).toUpperCase() + openWeatherMap.getWeather().get(0).getDescription().substring(1)));
                txtHumidity.setText(String.format("Humidity %d%%", openWeatherMap.getMain().getHumidity()));
                txtSunrise.setText(String.format("Sunrise %s", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise())));
                txtSunset.setText(String.format("Sunset %s", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
                txtCelsius.setText(String.format("%d °C", ((Double) openWeatherMap.getMain().getTemp()).intValue()));
                txtMinTmp.setText(String.format("Min %d °C", ((Double) openWeatherMap.getMain().getTemp_min()).intValue()));
                txtMaxTmp.setText(String.format("Max %d °C", ((Double) openWeatherMap.getMain().getTemp_max()).intValue()));
                Picasso.with(MainActivity.this)
                        .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                        .into(imageView);

                new GetWeather().execute(Common.apiRequestForecastByCityName(last_city));

            }
        }

    }

    class CustomList extends BaseAdapter {
        @Override
        public int getCount() {
            return listOWM.getCnt();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.list_elem, null);

            TextView txtDescription, txtCelsius, txtMinTmp, txtMaxTmp, txtDate;
            ImageView imageView;

            txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            txtCelsius = (TextView) convertView.findViewById(R.id.txtCelsius);
            txtMinTmp = (TextView) convertView.findViewById(R.id.txtMinTmp);
            txtMaxTmp = (TextView) convertView.findViewById(R.id.txtMaxTmp);
            imageView = (ImageView) convertView.findViewById(R.id.imageView);

            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
            String dateString = formatter.format(new Date(listOWM.getList().get(position).getDt() * 1000L));

            txtDate.setText(String.format("%s", dateString));
            txtDescription.setText(String.format("%s", listOWM.getList().get(position).getWeather().get(0).getDescription().substring(0, 1).toUpperCase() + listOWM.getList().get(position).getWeather().get(0).getDescription().substring(1)));
            txtCelsius.setText(String.format("%d °C", ((Double) listOWM.getList().get(position).getMain().getTemp()).intValue()));
            txtMinTmp.setText(String.format("Min %d °C", ((Double) listOWM.getList().get(position).getMain().getTemp_min()).intValue()));
            txtMaxTmp.setText(String.format("Max %d °C", ((Double) listOWM.getList().get(position).getMain().getTemp_max()).intValue()));
            Picasso.with(MainActivity.this)
                    .load(Common.getImage(listOWM.getList().get(position).getWeather().get(0).getIcon()))
                    .into(imageView);

            return convertView;
        }
    }
}
