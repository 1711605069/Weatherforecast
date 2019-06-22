package com.example.weatherforecast.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;

    @SerializedName("flu")
    public Fluent fluent;

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }


    public class Sport {

        @SerializedName("txt")
        public String info;

    }
    public class Fluent {

        @SerializedName("txt")
        public String info;

    }


}
