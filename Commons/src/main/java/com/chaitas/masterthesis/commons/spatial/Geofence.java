package com.chaitas.masterthesis.commons.spatial;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.spatial4j.io.ShapeWriter;
import org.locationtech.spatial4j.shape.Circle;
import org.locationtech.spatial4j.shape.Rectangle;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.SpatialRelation;

import java.text.ParseException;

import static com.chaitas.masterthesis.commons.spatial.SpatialContext.GEO;


public class Geofence  {

    @JsonIgnore
    private final Shape shape;
    @JsonIgnore
    final Rectangle boundingBox;

    private Geofence(Shape shape) {
        this.shape = shape;
        this.boundingBox = shape.getBoundingBox();
    }

    @JsonCreator
    public Geofence(@JsonProperty("WKT") String wkt) throws ParseException {
        WKTReader reader = (WKTReader) GEO.getFormats().getWktReader();
        this.shape = reader.parse(wkt);
        this.boundingBox = this.shape.getBoundingBox();
    }

    @JsonIgnore
    public static Geofence circle(Location location, double radiusDegree) {
        Circle c = GEO.getShapeFactory().circle(location.getLocation(), radiusDegree);
        return new Geofence(c);
    }

    public static Geofence world() {
        Shape worldShape = GEO.getWorldBounds();
        return new Geofence(worldShape);
    }

    public boolean contains(Location location) {
        return shape.relate(location.getLocation()).equals(SpatialRelation.CONTAINS);
    }

    public boolean intersects(Geofence geofence) {
        SpatialRelation sr = shape.relate(geofence.shape);
        return sr.equals(SpatialRelation.INTERSECTS) || sr.equals(SpatialRelation.CONTAINS) ||
                sr.equals(SpatialRelation.WITHIN);
    }

    @JsonIgnore
    public Location getBoundingBoxNorthWest() {
        return new Location(boundingBox.getMaxY(), boundingBox.getMinX());
    }

    @JsonIgnore
    public Location getBoundingBoxNorthEast() {
        return new Location(boundingBox.getMaxY(), boundingBox.getMaxX());
    }

    @JsonIgnore
    public Location getBoundingBoxSouthEast() {
        return new Location(boundingBox.getMinY(), boundingBox.getMaxX());
    }

    @JsonIgnore
    public Location getBoundingBoxSouthWest() {
        return new Location(boundingBox.getMinY(), boundingBox.getMinX());
    }

    @JsonProperty("WKT")
    public String getWKTString() {
        ShapeWriter writer = GEO.getFormats().getWktWriter();
        return writer.toString(shape);
    }
}