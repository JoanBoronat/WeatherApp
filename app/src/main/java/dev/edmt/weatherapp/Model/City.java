package dev.edmt.weatherapp.Model;


public class City {

    private int id;
    private String name;
    private Coord coord;
    private String country;
    private int population;

    public City(int id, String name, Coord coord, String country, int population) {
        this.id = id;
        this.name = name;
        this.coord = coord;
        this.country = country;
        this.population = population;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }
}
