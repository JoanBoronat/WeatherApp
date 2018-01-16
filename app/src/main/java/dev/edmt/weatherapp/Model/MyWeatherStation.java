package dev.edmt.weatherapp.Model;


public class MyWeatherStation {

    private float temperature = 0.0f, humidity = 0.0f, luminosity = 0.0f;

    public MyWeatherStation(float temperature, float humidity, float luminosity) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminosity = luminosity;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getLuminosity() {
        return luminosity;
    }
}
