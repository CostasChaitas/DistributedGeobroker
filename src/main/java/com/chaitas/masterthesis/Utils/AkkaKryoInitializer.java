package com.chaitas.masterthesis.Utils;

import akka.actor.ActorRef;
import akka.actor.FunctionRef;
import com.chaitas.masterthesis.Messages.ExternalMessages.ControlPacketType;
import com.chaitas.masterthesis.Messages.ExternalMessages.ReasonCode;
import com.chaitas.masterthesis.Messages.ExternalMessages.ExternalMessage;
import com.chaitas.masterthesis.Messages.ExternalMessages.Topic;
import com.chaitas.masterthesis.Messages.ExternalMessages.Payloads.*;
import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Geofence;
import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Location;
import com.chaitas.masterthesis.Messages.InternalMessages.*;
import com.chaitas.masterthesis.Storage.Subscription;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class AkkaKryoInitializer {

    // Required method for kryo serialization
    public void customize(Kryo kryo) {
        // General
        kryo.register(String[].class, 250);
        kryo.register(FunctionRef.class, 251);
        kryo.register(ActorRef.class, 249);
        kryo.register(ImmutablePair.class, 250);

        // Included in ExternalMessage
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
        kryo.register(ExternalMessage.class, 258);

        // Payloads needed for ExternalMessage
        kryo.register(CONNECTPayload.class, 259);
        kryo.register(CONNACKPayload.class, 260);
        kryo.register(DISCONNECTPayload.class, 261);
        kryo.register(PINGREQPayload.class, 262);
        kryo.register(PINGRESPPayload.class, 263);
        kryo.register(UNSUBSCRIBEPayload.class, 264);
        kryo.register(UNSUBACKPayload.class, 265);
        kryo.register(SUBSCRIBEPayload.class, 266);
        kryo.register(SUBACKPayload.class, 267);
        kryo.register(PUBLISHPayload.class, 268);
        kryo.register(PUBACKPayload.class, 269);

        // Messages for communication between brokers/servers(internal communication)
        kryo.register(ProcessCONNECT.class, 270);
        kryo.register(ProcessDISCONNECT.class, 271);
        kryo.register(ProcessPINGREQ.class, 272);
        kryo.register(ProcessUNSUBSCRIBE.class, 273);
        kryo.register(ProcessSUBSCRIBE.class, 274);
        kryo.register(ProcessPUBLISH.class, 275);
        kryo.register(Subscription.class, 276);
        kryo.register(SendACK.class, 277);
        kryo.register(PublisherGeoMatching.class, 278);

    }

}
