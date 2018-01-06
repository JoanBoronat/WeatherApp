package dev.edmt.weatherapp.Model;

import java.util.List;

public class ListOWM {
    private int cod;
    private String message;
    private String country;
    private int cnt;
    private List<WeatherForecast> list;
    private City city;

    public ListOWM() {
    }

    public ListOWM(int cod, String message, String country, int cnt, List<WeatherForecast> list, City city) {
        this.cod = cod;
        this.message = message;
        this.country = country;
        this.cnt = cnt;
        this.list = list;
        this.city = city;
    }

    public int getCod() {
        return cod;
    }

    public String getMessage() {
        return message;
    }

    public String getCountry() {
        return country;
    }

    public int getCnt() {
        return cnt;
    }

    public List<WeatherForecast> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }
}
