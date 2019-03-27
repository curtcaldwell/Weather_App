package com.curtcaldwell.whatstheweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    TextView cityTextView;
    TextView tempTextView;
    TextView highTextView;
    TextView lowTextView;
    Switch aSwitch;
    TextView descTextView;
    LocationManager locationManager;
    LocationListener locationListener;
    WeatherResponse weatherResponse;
    ImageView iconImageView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getWeather(location, "b6907d289e10d714a6e88b30761fae22");


                Log.i("location", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }


        cityTextView = findViewById(R.id.textView2);
        iconImageView = findViewById(R.id.iconImageView);
        tempTextView = findViewById(R.id.temp_text_view);
        lowTextView = findViewById(R.id.low_text_view);
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tempTextView.setText(String.valueOf(Math.round(toFehrenheit(weatherResponse.getMain().getTemp())) + "°"));

                } else {
                    tempTextView.setText(String.valueOf(Math.round(weatherResponse.getMain().getTemp())) + "°");
                }
            }
        });
        highTextView = findViewById(R.id.high_text_view);
        descTextView = findViewById(R.id.description_text_view);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setIcon(View view) {
            if (weatherResponse.getWeather().get(0).getDescription() == "clear sky") {
                iconImageView.setImageResource(R.drawable.clearsky);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "few clouds") {

                iconImageView.setImageResource(R.drawable.fewclouds);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "scattered clouds") {

                iconImageView.setImageResource(R.drawable.scatteredclouds);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "broken clouds") {

                iconImageView.setImageResource(R.drawable.brokenclouds);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "shower rain") {

                iconImageView.setImageResource(R.drawable.showerrain);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "rain") {

                iconImageView.setImageResource(R.drawable.rain);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "thunderstorm") {

                iconImageView.setImageResource(R.drawable.thunderstorm);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "snow") {

                iconImageView.setImageResource(R.drawable.snow);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "mist") {

                iconImageView.setImageResource(R.drawable.mist);

            } else if (weatherResponse.getWeather().get(0).getDescription() == "moderate rain") {

                iconImageView.setImageResource(R.drawable.moderaterain);
            }
        }


    private OpenWeatherService getService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OpenWeatherService service = retrofit.create(OpenWeatherService.class);
        return service;
    }

    public double toFehrenheit(double c) {

        return c * 9/5 + 32;
    }


    public void getWeather(final Location location, String appid) {

        Call call = getService().getWeatherResponse(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), appid);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                double c = response.body().getMain().getTemp();

                if (response != null) {

                    weatherResponse = response.body();

                    tempTextView.setText(String.valueOf(Math.round(toFehrenheit(response.body().getMain().getTemp())) + "°"));
                    cityTextView.setText(String.valueOf(response.body().getName()));
                    highTextView.setText(String.valueOf( Math.round(toFehrenheit(response.body().getMain().getTempMax())) + "°↑" ));
                    lowTextView.setText(String.valueOf(Math.round(toFehrenheit(response.body().getMain().getTempMin())) + "°↓️"));
                    descTextView.setText(("Description: " + response.body().getWeather().get(0).getDescription()));



                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public interface OpenWeatherService {
        @GET("data/2.5/weather")
        Call<WeatherResponse> getWeatherResponse(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String appid);

//        @GET("data/2.5/weather")
//        Call<WeatherResponse> getWea


    }


}
