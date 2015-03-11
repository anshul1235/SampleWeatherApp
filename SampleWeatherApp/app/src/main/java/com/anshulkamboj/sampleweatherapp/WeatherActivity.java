package com.anshulkamboj.sampleweatherapp;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;


public class WeatherActivity extends ActionBarActivity {
    public static final String WEATHER_BASE_URL = "http://api.openweathermap.org";


    private TextView mCityNameText,mDescriptionText,mHumidityText,mTemperatureText,mWindSpeedText,
            mPressureText;
    private ImageView mWeatherImage;
    private EditText mCityEditText;
    private Button mGetWeatherButton;
    private WeatherApi mWeatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //this function will set up the rest adapter so as to use the weather api
        setUpApis();

        //initialisation of views (inflating the views from the UI to code)
        initialiseViews();

        //setting all the listeners on the inflated views
        setListeners();
    }

    /**
     * set up the apis
     */
    private void setUpApis(){
        final RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL);

        mWeatherApi = builder.setEndpoint(WEATHER_BASE_URL).build().create(WeatherApi.class);
    }

    /**
     * All the views are inflated from the xmls so as to get references
     */
    private void initialiseViews(){
        mCityNameText = (TextView) findViewById(R.id.city_name);
        mDescriptionText = (TextView) findViewById(R.id.description);
        mHumidityText = (TextView) findViewById(R.id.humidity);
        mTemperatureText = (TextView) findViewById(R.id.temperature);
        mWindSpeedText = (TextView) findViewById(R.id.windspeed);
        mPressureText = (TextView) findViewById(R.id.pressure);
        mWeatherImage = (ImageView) findViewById(R.id.weather_image);
        mCityEditText = (EditText) findViewById(R.id.city_edit_text);
        mGetWeatherButton = (Button) findViewById(R.id.get_weather_button);
    }

    /**
     * this function sets all the listeners on the views to perform actions like clicking etc
     */
    private void setListeners(){
        mGetWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetWeatherButton.setText("Syncing...");
                getWeatherDetails(mCityEditText.getText().toString());

            }
        });

    }

    /**
     * function to make a call to weather api to get the details from the server
     * @param cityName passes the city name of which we want the weather
     */
    private void getWeatherDetails(String cityName){

        Map<String,String> params = new HashMap<String,String>();
        params.put("q",cityName);
        mWeatherApi.getWeather(params,new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                mCityNameText.setText("City: "+weatherResponse.name);
                mDescriptionText.setText("Weather Description: "+weatherResponse.weather.get(0).description);
                mTemperatureText.setText("Temperature: "+weatherResponse.main.temp);
                mWindSpeedText.setText("Wind Speed: "+weatherResponse.wind.speed);
                mHumidityText.setText("Humidity: "+weatherResponse.main.humidity);
                mPressureText.setText("Pressure: "+weatherResponse.main.pressure);
                mGetWeatherButton.setText("Get Weather!");

                //Glide loads the image from the link on the UI
                Glide.with(getApplicationContext())
                        .load("http://openweathermap.org/img/w/" + weatherResponse.weather.get(0).icon + ".png")
                        .asBitmap()
                        .centerCrop()
                        .into(mWeatherImage);
            }

            @Override
            public void failure(RetrofitError error) {

                mGetWeatherButton.setText("Get Weather!");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Class that represents the weather apis
     */
    public interface WeatherApi {

        @GET("/data/2.5/weather")
        void getWeather(@QueryMap Map<String,String> params, Callback<WeatherResponse> cb);

    }

    public class WeatherResponse{
        public class Weather{
            public String id;
            public String description;
            public String icon;
        }

        public class Main{
            public String temp;
            public String pressure;
            public String humidity;
            public String temp_min;
            public String temp_max;

        }

        public class Wind{
            public String speed;
            public String deg;
        }

        public String name;
        public List<Weather> weather;
        public Main main;
        public Wind wind;
    }

}
