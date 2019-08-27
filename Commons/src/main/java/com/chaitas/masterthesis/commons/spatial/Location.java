package com.chaitas.masterthesis.commons.spatial;

import static com.chaitas.masterthesis.commons.spatial.SpatialContext.GEO;
import static org.locationtech.spatial4j.distance.DistanceUtils.DEG_TO_KM;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.locationtech.spatial4j.shape.Point;

public class Location {

    @JsonIgnore
    private final Point point;
    @JsonIgnore
    public boolean undefined = false;

    public Location(@NotNull @JsonProperty("lat") double lat,
                    @NotNull @JsonProperty("lon") double lon) {
        point = GEO.getShapeFactory().pointXY(lat, lon);
    }

    @JsonIgnore
    public Location(boolean undefined) {
        this.undefined = undefined;
        this.point = null;
    }

    // Distance between this location and the given one, as determined by the Haversine formula, in km
    public double distanceKmTo(Location toL) {
        return distanceRadiansTo(toL) * DEG_TO_KM;
    }

    // Distance between this location and the given one, as determined by the Haversine formula, in radians
    public double distanceRadiansTo(Location toL) {
        return GEO.getDistCalc().distance(point, toL.getLocation());
    }

    public double getLat() {
        return point.getX();
    }

    public double getLon() {
        return point.getY();
    }

    public Point getLocation() {
        return this.point;
    }

    @JsonIgnore
    public boolean isUndefined() { return undefined; }
}