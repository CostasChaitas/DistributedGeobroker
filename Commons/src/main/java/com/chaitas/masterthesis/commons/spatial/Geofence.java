package com.chaitas.masterthesis.commons.spatial;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class Geofence {

    public Location location;
    public double radiusDegree;

    public Geofence(
            @NotNull @JsonProperty("location") Location location,
            @NotNull @JsonProperty("radiusDegree") double radiusDegree) {
        this.location = location;
        this.radiusDegree = radiusDegree;
    }

//    public static Geofence circle(Location location, double radiusDegree) {
//        Circle c = GEO.getShapeFactory().circle(location.getLocation(), radiusDegree);
//        return new Geofence(c);
//    }
//
//    public static Geofence world() {
//        Shape worldShape = GEO.getWorldBounds();
//        return new Geofence(worldShape);
//    }
//
//    public boolean contains(Location location) {
//        Shape shape =  GEO.getShapeFactory().circle(location.getLocation(), radiusDegree);
//        return shape.relate(location.getLocation()).equals(SpatialRelation.CONTAINS);
//    }
//
//    public boolean intersects(Geofence geofence) {
//        Shape shape =  GEO.getShapeFactory().circle(location.getLocation(), radiusDegree);
//        SpatialRelation sr = shape.relate(geofence.shape);
//        return sr.equals(SpatialRelation.INTERSECTS) || sr.equals(SpatialRelation.CONTAINS) ||
//                sr.equals(SpatialRelation.WITHIN);
//    }
//
//    public String getWKTString() {
//        ShapeWriter writer = GEO.getFormats().getWktWriter();
//        return writer.toString(shape);
//    }
//
//    public Location getBoundingBoxNorthWest() {
//        return new Location(boundingBox.getMaxY(), boundingBox.getMinX());
//    }
//
//    public Location getBoundingBoxNorthEast() {
//        return new Location(boundingBox.getMaxY(), boundingBox.getMaxX());
//    }
//
//    public Location getBoundingBoxSouthEast() {
//        return new Location(boundingBox.getMinY(), boundingBox.getMaxX());
//    }
//
//    public Location getBoundingBoxSouthWest() {
//        return new Location(boundingBox.getMinY(), boundingBox.getMinX());
//    }
}
