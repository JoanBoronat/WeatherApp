package dev.edmt.weatherapp.Model;


import java.util.List;

public class WeatherForecast {
    private int dt;
    private MainForecast main;
    private List<Weather> weather;
    private Clouds clouds;
    private Wind wind;
    private Rain rain;
    private Sys sys;
    private String dt_text;

    public WeatherForecast(int dt, MainForecast main, List<Weather> weather, Clouds clouds, Wind wind, Rain rain, Sys sys, String dt_text) {
        this.dt = dt;
        this.main = main;
        this.weather = weather;
        this.clouds = clouds;
        this.wind = wind;
        this.rain = rain;
        this.sys = sys;
        this.dt_text = dt_text;
    }

    public int getDt() {
        return dt;
    }

    public MainForecast getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public Rain getRain() {
        return rain;
    }

    public Sys getSys() {
        return sys;
    }

    public String getDt_text() {
        return dt_text;
    }
}
