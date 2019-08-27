package com.chaitas.masterthesis.cluster.util;

import akka.actor.FunctionRef;
import akka.actor.RepointableActorRef;
import akka.remote.RemoteActorRef;
import com.chaitas.masterthesis.cluster.Messages.*;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.*;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import com.chaitas.masterthesis.commons.spatial.Location;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AkkaKryoInitializer {

    // Required method for kryo serialization
    public void customize(Kryo kryo) {
        System.out.println("Registering the classes...");

        // General
        kryo.register(String[].class, 250);
        kryo.register(FunctionRef.class, 251);
        kryo.register(RepointableActorRef.class, 252);
        kryo.register(RemoteActorRef.class, 249);

        // Included in InternalServerMessage
        kryo.register(ControlPacketType.class, 253);
        kryo.register(ReasonCode.class, 254);
        kryo.register(Topic.class, 255);

        kryo.register(Location.class, new Serializer<Location>() {
            public void write (Kryo kryo, Output output, Location object) {
                if(object.isUndefined()){
                    kryo.writeObjectOrNull(output, -1000.0, Double.class);
                    kryo.writeObjectOrNull(output, -1000.0, Double.class);
                } else {
                    kryo.writeObjectOrNull(output, object.getLat(), Double.class);
                    kryo.writeObjectOrNull(output, object.getLon(), Double.class);
                }
            }

            public Location read (Kryo kryo, Input input, Class<Location> type) {
                Double lat = kryo.readObjectOrNull(input, Double.class);
                Double lon = kryo.readObjectOrNull(input, Double.class);
                if (lat == -1000.0 && lon == -1000.0) {
                    return new Location(true);
                } else {
                    return new Location(lat, lon);
                }
            }
        }, 256);

        kryo.register(Geofence.class, new Serializer<Geofence>() {
            public void write (Kryo kryo, Output output, Geofence object) {
                kryo.writeObjectOrNull(output, object.getWKTString(), String.class);
            }

            public Geofence read (Kryo kryo, Input input, Class<Geofence> type) {
                try {
                    String wkt = kryo.readObjectOrNull(input, String.class);
                    Geofence geofence = new Geofence(wkt);
                    return geofence;
                } catch (Exception e) {
                    return null;
                }
            }
        }, 257);


        // Message for communication between clients and brokers/servers
        kryo.register(InternalServerMessage.class, 258);

        // Payloads needed for InternalServerMessage
        kryo.register(CONNECTPayload.class, 259);
        kryo.register(CONNACKPayload.class, 260);
        kryo.register(PINGREQPayload.class, 261);
        kryo.register(PINGRESPPayload.class, 262);
        kryo.register(SUBSCRIBEPayload.class, 263);
        kryo.register(SUBACKPayload.class, 264);
        kryo.register(PUBLISHPayload.class, 265);
        kryo.register(PUBACKPayload.class, 266);

        // Messages for communication between brokers/servers(internal communication)
        kryo.register(ProcessPUBLISH.class, 267);
        kryo.register(ProcessSUBSCRIBE.class, 268);
        kryo.register(SendSUBACK.class, 269);
        kryo.register(SendPUBACK.class, 270);
        kryo.register(GeoMatching.class, 271);

    }

}
