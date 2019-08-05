package com.chaitas.masterthesis.commons.spatial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    public double lat;
    public double lon;
    @JsonIgnore
    public boolean undefined = false;

    public Location(
             @JsonProperty("lat") double lat,
             @JsonProperty("lon") double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @JsonIgnore
    public Location(boolean undefined) {
        this.undefined = undefined;
    }

    @JsonIgnore
    public static Location undefined() {
        return new Location(true);
    }

    public double getLat() { return lat; }
    public double getLon() {
        return lon;
    }
    @JsonIgnore
    public boolean isUndefined() { return undefined; }
}
