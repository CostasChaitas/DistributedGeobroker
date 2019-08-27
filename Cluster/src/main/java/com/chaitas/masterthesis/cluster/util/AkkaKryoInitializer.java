package com.chaitas.masterthesis.cluster.util;

import akka.actor.FunctionRef;
import com.chaitas.masterthesis.cluster.Messages.ProcessPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.ProcessSUBSCRIBE;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.*;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import com.chaitas.masterthesis.commons.spatial.Location;
import com.esotericsoftware.kryo.Kryo;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.io.jts.JtsBinaryCodec;
import org.locationtech.spatial4j.shape.impl.RectangleImpl;


public class AkkaKryoInitializer {

    public void customize(Kryo kryo) {
        System.out.println("Registering the classes...");

        kryo.register(String[].class, 250);
        kryo.register(FunctionRef.class, 251);

        kryo.register(ControlPacketType.class, 260);
        kryo.register(ReasonCode.class, 261);
        kryo.register(Location.class, 262);
        kryo.register(Geofence.class, 263);
        kryo.register(Topic.class, 264);
        kryo.register(ProcessPUBLISH.class, 265);
        kryo.register(ProcessSUBSCRIBE.class, 266);
        kryo.register(InternalServerMessage.class, 267);
        kryo.register(PUBLISHPayload.class, 268);
        kryo.register(SUBSCRIBEPayload.class, 269);
        kryo.register(CONNECTPayload.class, 270);
        kryo.register(CONNACKPayload.class, 271);
        kryo.register(PINGREQPayload.class, 272);
        kryo.register(PINGRESPPayload.class, 273);
        kryo.register(RectangleImpl.class, 274);
        kryo.register(JtsSpatialContext.class, 275);
        kryo.register(JtsBinaryCodec.class, 276);
    }

}
