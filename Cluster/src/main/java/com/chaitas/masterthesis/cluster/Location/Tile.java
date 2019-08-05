//package cluster.Location;
//
//
//import com.chaitas.masterthesis.commons.spatial.Geofence;
//import org.locationtech.spatial4j.shape.Point;
//import org.locationtech.spatial4j.shape.Rectangle;
//import org.locationtech.spatial4j.shape.Shape;
//
//import static org.locationtech.spatial4j.context.jts.JtsSpatialContext.GEO;
//
//public class Tile {
//
//    private final Geofence geofence;
//
//    public Tile(Geofence geofence) {
//        this.geofence = geofence;
//    }
//
//    public static Rectangle[] getTiles() {
//        int numOfTiles = 10;
//        Shape worldShape = GEO.getWorldBounds();
//
//        double worldWidth = worldShape.getBoundingBox().getWidth();
//        double worldMinX = worldShape.getBoundingBox().getMinX();
//        double worldMaxX = worldShape.getBoundingBox().getMaxX();
//
//        double worldheight = worldShape.getBoundingBox().getWidth();
//        double worldMinY = worldShape.getBoundingBox().getMinY();
//        double worldMaxY = worldShape.getBoundingBox().getMaxY();
//
//        double tileWidth = worldWidth / numOfTiles;
//        double tileHeight = worldheight / numOfTiles;
//
//        Rectangle[] tiles = new Rectangle[numOfTiles];
//
//        for(int tileNum = 0; tileNum < numOfTiles; tileNum++){
//
//            double tileMinX = worldMinX + (tileNum * tileWidth);
//            double tileMaxX = tileMinX + tileWidth;
//            double tileMinY = worldMinY + (tileNum * tileHeight);
//            double tileMaxY = tileMinY + tileHeight;
//
//            Point pointNW = GEO.getShapeFactory().pointXY(tileMinX, tileMinY);
//            Point pointSE = GEO.getShapeFactory().pointXY(tileMaxX, tileMaxY);
//
//            Rectangle rect = GEO.getShapeFactory().rect(pointNW, pointSE);
//
//            tiles[tileNum] = rect;
//
//            System.out.print(" minX: " + rect.getMinX() + " maxX: " + rect.getMaxX() + " minY: " + rect.getMinY() + " maxY: " + rect.getMaxY() +"\n");
//        }
//
//        return tiles;
//    }
//
//}
//
//
//
