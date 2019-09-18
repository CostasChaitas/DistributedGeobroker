<<<<<<< HEAD
=======
// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

>>>>>>> master
package com.chaitas.masterthesis.commons.spatial;

import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory;

public class SpatialContext {

    public static final JtsSpatialContext GEO;
    static {
        JtsSpatialContextFactory factory = new JtsSpatialContextFactory();
        factory.geo = true;
        factory.readers.clear();
        factory.readers.add(WKTReader.class);
        factory.writers.clear();
        factory.writers.add(WKTWriter.class);
        GEO = new JtsSpatialContext(factory);
    }

}
