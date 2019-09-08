package com.chaitas.masterthesis.commons.spatial;

import static com.chaitas.masterthesis.commons.spatial.SpatialContext.GEO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.locationtech.spatial4j.io.ShapeWriter;
import org.locationtech.spatial4j.shape.Point;

import java.util.Objects;
import java.util.Random;

import static org.locationtech.spatial4j.distance.DistanceUtils.DEG_TO_KM;
import static org.locationtech.spatial4j.distance.DistanceUtils.KM_TO_DEG;

public class Location {

    @JsonIgnore
    private final Point point;

    @JsonIgnore
    private boolean undefined = false;

    @JsonIgnore
    private Location(Point point) {
        this.point = point;
    }

    @JsonIgnore
    public Location(boolean undefined) {
        this.undefined = undefined;
        this.point = null;
    }

    /**
     * Creates a location with the given lat/lon coordinates.
     *
     * @param lat - the latitude (Breitengrad)
     * @param lon - the longitude (Längengrad)
     */
    public Location(@NotNull @JsonProperty("lat") double lat,
                    @NotNull @JsonProperty("lon") double lon) {
        point = GEO.getShapeFactory().pointLatLon(lat, lon);
    }

    /**
     * Creates an undefined location, i.e., one that does not have a point and is not contained in any geofence.
     */
    @JsonIgnore
    public static Location undefined() {
        return new Location(true);
    }

    /**
     * Creates a random location (Not inclusive of (-90, 0))
     */
    @JsonIgnore
    public static Location random() {
        Random random = new Random();
        // there have been rounding errors
        return new Location(Math.min((random.nextDouble() * -180.0) + 90.0, 90.0),
                Math.min((random.nextDouble() * -360.0) + 180.0, 180.0));
    }

    /**
     * @param location - starting location
     * @param distance - distance from starting location in km
     * @param direction - direction (0 - 360)
     */
    @JsonIgnore
    public static Location locationInDistance(Location location, double distance, double direction) {
        if (location.isUndefined()) {
            return Location.undefined();
        }
        Point result = GEO.getDistCalc().pointOnBearing(location.point,
                distance * KM_TO_DEG,
                direction,
                GEO,
                GEO.getShapeFactory().pointLatLon(0.0, 0.0));

        return new Location(result);
    }

    /**
     * Distance between this location and the given one, as determined by the Haversine formula, in radians
     *
     * @param toL - the other location
     * @return distance in radians or -1 if one location is undefined
     */
    @JsonIgnore
    public double distanceRadiansTo(Location toL) {
        if (undefined || toL.undefined) {
            return -1.0;
        }
        return GEO.getDistCalc().distance(point, toL.getPoint());
    }

    /**
     * Distance between this location and the given one, as determined by the Haversine formula, in km
     *
     * @param toL - the other location
     * @return distance in km or -1 if one location is undefined
     */
    @JsonIgnore
    public double distanceKmTo(Location toL) {
        if (undefined || toL.undefined) {
            return -1.0;
        }
        return distanceRadiansTo(toL) * DEG_TO_KM;
    }

    /*****************************************************************
     * Getters and String
     ****************************************************************/
    @JsonIgnore
    public Point getPoint() {
        return point;
    }

    public double getLat() {
        return point.getLat();
    }

    public double getLon() {
        return point.getLon();
    }

    public String getWKTString() {
        if (undefined) {
            return "{ undefined }";
        }
        ShapeWriter writer = GEO.getFormats().getWktWriter();
        return writer.toString(point);
    }

    @JsonIgnore
    public boolean isUndefined() {
        return undefined;
    }

    @Override
    public String toString() {
        return getWKTString();
    }

    /*****************************************************************
     * Generated methods
     ****************************************************************/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return undefined == location.undefined && Objects.equals(point, location.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, undefined);
    }

    @JsonIgnore
    public static void main(String[] args) {
        Location l = new Location(39.984702, 116.318417);
        Location l2 = new Location(39.974702, 116.318417);
        System.out.println("Distance is {}km" + l.distanceKmTo(l2));

        l = new Location(57.34922076607738, 34.53035122251791);
        l2 = new Location(57.34934475583778, 34.53059311887825);
        System.out.println("Distance is {}km" + l.distanceKmTo(l2));
    }
}
