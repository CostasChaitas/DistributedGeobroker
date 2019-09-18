// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.Utils

import com.chaitas.masterthesis.Messages.ExternalMessages.ControlPacketType
import com.chaitas.masterthesis.Messages.ExternalMessages.ExternalMessage
import com.chaitas.masterthesis.Messages.ExternalMessages.ReasonCode
import com.chaitas.masterthesis.Messages.ExternalMessages.Topic
import com.chaitas.masterthesis.Messages.ExternalMessages.Payloads.*
import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Geofence
import com.chaitas.masterthesis.Messages.ExternalMessages.Spatial.Location

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class KryoSerializer {
    val kryo = Kryo()
    private val output = Output(1024, -1)
    private val input = Input()

    /**
     * Specifying new customised serialisers for kryo to work on our different payloads
     */
    init {
        kryo.register(Topic::class.java, object : Serializer<Topic>() {
            override fun write(kryo: Kryo, output: Output, o: Topic) {
                kryo.writeObjectOrNull(output, o.topic, String::class.java)
            }
            override fun read(kryo: Kryo, input: Input, aClass: Class<Topic>): Topic? {
                val topic = kryo.readObjectOrNull(input, String::class.java) ?: return null
                return Topic(topic)
            }
        })
        kryo.register(Location::class.java, object : Serializer<Location>() {
            override fun write(kryo: Kryo, output: Output, o: Location) {
                if (o.isUndefined) {
                    kryo.writeObjectOrNull(output, -1000.0, Double::class.javaPrimitiveType)
                    kryo.writeObjectOrNull(output, -1000.0, Double::class.javaPrimitiveType)
                } else {
                    kryo.writeObjectOrNull(output, o.lat, Double::class.javaPrimitiveType)
                    kryo.writeObjectOrNull(output, o.lon, Double::class.javaPrimitiveType)
                }
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<Location>): Location? {
                val lat = kryo.readObjectOrNull(input, Double::class.javaPrimitiveType!!) ?: return null
                val lon = kryo.readObjectOrNull(input, Double::class.javaPrimitiveType!!) ?: return null
                return if (lat == -1000.0 && lon == -1000.0) {
                    Location(true)
                } else {
                    Location(lat, lon)
                }
            }
        })
        kryo.register(Geofence::class.java, object : Serializer<Geofence>() {
            override fun write(kryo: Kryo, output: Output, o: Geofence) {
                kryo.writeObjectOrNull(output, o.wktString, String::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<Geofence>): Geofence? {
                try {
                    val str = kryo.readObjectOrNull(input, String::class.java) ?: return null
                    return Geofence(str)
                } catch (ex: Exception) {
                    return null
                }

            }
        })
        kryo.register(CONNACKPayload::class.java, object : Serializer<CONNACKPayload>() {
            override fun write(kryo: Kryo, output: Output, o: CONNACKPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<CONNACKPayload>): CONNACKPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return CONNACKPayload(reasonCode)
            }
        })
        kryo.register(CONNECTPayload::class.java, object : Serializer<CONNECTPayload>() {
            override fun write(kryo: Kryo, output: Output, o: CONNECTPayload) {
                kryo.writeObjectOrNull(output, o.location, Location::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<CONNECTPayload>): CONNECTPayload? {
                val location = kryo.readObjectOrNull(input, Location::class.java) ?: return null
                return CONNECTPayload(location)
            }
        })
        kryo.register(DISCONNECTPayload::class.java, object : Serializer<DISCONNECTPayload>() {
            override fun write(kryo: Kryo, output: Output, o: DISCONNECTPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<DISCONNECTPayload>): DISCONNECTPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return DISCONNECTPayload(reasonCode)
            }
        })
        kryo.register(PINGREQPayload::class.java, object : Serializer<PINGREQPayload>() {
            override fun write(kryo: Kryo, output: Output, o: PINGREQPayload) {
                kryo.writeObjectOrNull(output, o.location, Location::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<PINGREQPayload>): PINGREQPayload? {
                val location = kryo.readObjectOrNull(input, Location::class.java) ?: return null
                return PINGREQPayload(location)
            }
        })
        kryo.register(PINGRESPPayload::class.java, object : Serializer<PINGRESPPayload>() {
            override fun write(kryo: Kryo, output: Output, o: PINGRESPPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<PINGRESPPayload>): PINGRESPPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return PINGRESPPayload(reasonCode)
            }
        })
        kryo.register(PUBACKPayload::class.java, object : Serializer<PUBACKPayload>() {
            override fun write(kryo: Kryo, output: Output, o: PUBACKPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<PUBACKPayload>): PUBACKPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return PUBACKPayload(reasonCode)
            }
        })
        kryo.register(PUBLISHPayload::class.java, object : Serializer<PUBLISHPayload>() {
            override fun write(kryo: Kryo, output: Output, o: PUBLISHPayload) {
                kryo.writeObjectOrNull(output, o.content, String::class.java)
                kryo.writeObjectOrNull(output, o.geofence, Geofence::class.java)
                kryo.writeObjectOrNull(output, o.topic, Topic::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<PUBLISHPayload>): PUBLISHPayload? {
                val content = kryo.readObjectOrNull(input, String::class.java) ?: return null
                val g = kryo.readObjectOrNull(input, Geofence::class.java) ?: return null
                val topic = kryo.readObjectOrNull(input, Topic::class.java) ?: return null
                return PUBLISHPayload(topic, g, content)
            }
        })
        kryo.register(SUBACKPayload::class.java, object : Serializer<SUBACKPayload>() {
            override fun write(kryo: Kryo, output: Output, o: SUBACKPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<SUBACKPayload>): SUBACKPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return SUBACKPayload(reasonCode)
            }
        })
        kryo.register(SUBSCRIBEPayload::class.java, object : Serializer<SUBSCRIBEPayload>() {
            override fun write(kryo: Kryo, output: Output, o: SUBSCRIBEPayload) {
                kryo.writeObjectOrNull(output, o.geofence, Geofence::class.java)
                kryo.writeObjectOrNull(output, o.topic, Topic::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<SUBSCRIBEPayload>): SUBSCRIBEPayload? {
                val geofence = kryo.readObjectOrNull(input, Geofence::class.java) ?: return null
                val topic = kryo.readObjectOrNull(input, Topic::class.java) ?: return null
                return SUBSCRIBEPayload(topic, geofence)
            }
        })
        kryo.register(UNSUBACKPayload::class.java, object : Serializer<UNSUBACKPayload>() {
            override fun write(kryo: Kryo, output: Output, o: UNSUBACKPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<UNSUBACKPayload>): UNSUBACKPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return UNSUBACKPayload(reasonCode)
            }
        })
        kryo.register(UNSUBSCRIBEPayload::class.java, object : Serializer<UNSUBSCRIBEPayload>() {
            override fun write(kryo: Kryo, output: Output, o: UNSUBSCRIBEPayload) {
                kryo.writeObjectOrNull(output, o.topic, Topic::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<UNSUBSCRIBEPayload>): UNSUBSCRIBEPayload? {
                val topic = kryo.readObjectOrNull(input, Topic::class.java) ?: return null
                return UNSUBSCRIBEPayload(topic)
            }
        })
        kryo.register(INCOMPATIBLEPayload::class.java, object : Serializer<INCOMPATIBLEPayload>() {
            override fun write(kryo: Kryo, output: Output, o: INCOMPATIBLEPayload) {
                kryo.writeObjectOrNull(output, o.reasonCode, ReasonCode::class.java)
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<INCOMPATIBLEPayload>): INCOMPATIBLEPayload? {
                val reasonCode = kryo.readObjectOrNull(input, ReasonCode::class.java) ?: return null
                return INCOMPATIBLEPayload(reasonCode)
            }
        })
        kryo.register(ExternalMessage::class.java, object : Serializer<ExternalMessage>() {
            override fun write(kryo: Kryo, output: Output, o: ExternalMessage) {
                kryo.writeObjectOrNull(output, o.clientIdentifier, String::class.java)
                kryo.writeObjectOrNull(output, o.controlPacketType, ControlPacketType::class.java)
                when (o.controlPacketType) {
                    ControlPacketType.CONNACK -> kryo.writeObjectOrNull(output, o.payload, CONNACKPayload::class.java)
                    ControlPacketType.CONNECT -> kryo.writeObjectOrNull(output, o.payload, CONNECTPayload::class.java)
                    ControlPacketType.DISCONNECT -> kryo.writeObjectOrNull(output, o.payload, DISCONNECTPayload::class.java)
                    ControlPacketType.PINGREQ -> kryo.writeObjectOrNull(output, o.payload, PINGREQPayload::class.java)
                    ControlPacketType.PINGRESP -> kryo.writeObjectOrNull(output, o.payload, PINGRESPPayload::class.java)
                    ControlPacketType.PUBACK -> kryo.writeObjectOrNull(output, o.payload, PUBACKPayload::class.java)
                    ControlPacketType.PUBLISH -> kryo.writeObjectOrNull(output, o.payload, PUBLISHPayload::class.java)
                    ControlPacketType.SUBACK -> kryo.writeObjectOrNull(output, o.payload, SUBACKPayload::class.java)
                    ControlPacketType.SUBSCRIBE -> kryo.writeObjectOrNull(output, o.payload, SUBSCRIBEPayload::class.java)
                    ControlPacketType.UNSUBACK -> kryo.writeObjectOrNull(output, o.payload, UNSUBACKPayload::class.java)
                    ControlPacketType.UNSUBSCRIBE -> kryo.writeObjectOrNull(output, o.payload, UNSUBSCRIBEPayload::class.java)
                    ControlPacketType.INCOMPATIBLEPayload -> kryo.readObjectOrNull(input, INCOMPATIBLEPayload::class.java)
                }
            }

            override fun read(kryo: Kryo, input: Input, aClass: Class<ExternalMessage>): ExternalMessage? {
                val clientIdentifier = kryo.readObjectOrNull(input, String::class.java) ?: return null
                val controlPacketType = kryo.readObjectOrNull(input, ControlPacketType::class.java) ?: return null
                val o: AbstractPayload
                when (controlPacketType) {
                    ControlPacketType.CONNACK -> o = kryo.readObjectOrNull(input, CONNACKPayload::class.java) ?: return null
                    ControlPacketType.CONNECT -> o = kryo.readObjectOrNull(input, CONNECTPayload::class.java) ?: return null
                    ControlPacketType.DISCONNECT -> o = kryo.readObjectOrNull(input, DISCONNECTPayload::class.java) ?: return null
                    ControlPacketType.PINGREQ -> o = kryo.readObjectOrNull(input, PINGREQPayload::class.java) ?: return null
                    ControlPacketType.PINGRESP -> o = kryo.readObjectOrNull(input, PINGRESPPayload::class.java) ?: return null
                    ControlPacketType.PUBACK -> o = kryo.readObjectOrNull(input, PUBACKPayload::class.java) ?: return null
                    ControlPacketType.PUBLISH -> o = kryo.readObjectOrNull(input, PUBLISHPayload::class.java) ?: return null
                    ControlPacketType.SUBACK -> o = kryo.readObjectOrNull(input, SUBACKPayload::class.java) ?: return null
                    ControlPacketType.SUBSCRIBE -> o = kryo.readObjectOrNull(input, SUBSCRIBEPayload::class.java) ?: return null
                    ControlPacketType.UNSUBACK -> o = kryo.readObjectOrNull(input, UNSUBACKPayload::class.java) ?: return null
                    ControlPacketType.UNSUBSCRIBE -> o = kryo.readObjectOrNull(input, UNSUBSCRIBEPayload::class.java) ?: return null
                    ControlPacketType.INCOMPATIBLEPayload -> o = kryo.readObjectOrNull(input, INCOMPATIBLEPayload::class.java) ?: return null
                    else -> return null
                }
                return ExternalMessage(clientIdentifier, controlPacketType, o)
            }
        })

    }

    fun write(o: Any): ByteArray {
        kryo.writeObjectOrNull(output, o, o.javaClass)
        val arr = output.toBytes()
        output.clear()
        return arr
    }

    fun <T> read(bytes: ByteArray, targetClass: Class<T>): T {
        input.buffer = bytes
        return kryo.readObjectOrNull(input, targetClass)
    }

}