package com.chaitas.masterthesis.cluster.util;

import akka.actor.FunctionRef;
import com.chaitas.masterthesis.cluster.Messages.ProcessPUBLISH;
import com.chaitas.masterthesis.cluster.Messages.ProcessSUBSCRIBE;
import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.ReasonCode;
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
import com.chaitas.masterthesis.commons.message.Topic;
import com.chaitas.masterthesis.commons.payloads.PUBLISHPayload;
import com.chaitas.masterthesis.commons.payloads.SUBSCRIBEPayload;
import com.chaitas.masterthesis.commons.spatial.Geofence;
import com.chaitas.masterthesis.commons.spatial.Location;
import com.esotericsoftware.kryo.Kryo;



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

    }

}
