package dev.edmt.weatherapp.Model;


public class MainForecast {

    private double temp;
    private double temp_min;
    private double temp_max;
    private double pressure;
    private double sea_level;
    private double grnd_level;
    private int humidity;
    private double temp_kf;

    public MainForecast(double temp, double temp_min, double temp_max, double pressure, double sea_level, double grnd_level, int humidity, double temp_kf) {
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.sea_level = sea_level;
        this.grnd_level = grnd_level;
        this.humidity = humidity;
        this.temp_kf = temp_kf;
    }

    public double getTemp() {
        return temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getPressure() {
        return pressure;
    }

    public double getSea_level() {
        return sea_level;
    }

    public double getGrnd_level() {
        return grnd_level;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getTemp_kf() {
        return temp_kf;
    }
}
