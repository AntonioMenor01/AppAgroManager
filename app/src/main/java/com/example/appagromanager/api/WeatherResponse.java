package com.example.appagromanager.api;

import java.util.List;

public class WeatherResponse {

    public Location location;
    public Current current;
    public Forecast forecast;

    public class Location {
        public String name;
        public String country;
    }

    public class Current {
        public float temp_c;
        public float humidity;
    }

    public class Forecast {
        public List<ForecastDay> forecastday;
    }

    public class ForecastDay {
        public Day day;
    }

    public class Day {
        public float maxtemp_c;
        public float mintemp_c;
        public float daily_chance_of_rain;
    }
}