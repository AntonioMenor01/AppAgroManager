package com.example.appagromanager.models;

public class WeatherResponse {
    public Main main;
    public String name;

    public class Main {
        public float temp;
        public float humidity;
    }
}
