package com.chaitas.masterthesis.commons;

public enum ControlPacketType {
    Reserved, //
    CONNECT, //
    CONNACK, //
    PUBLISH, //
    PUBACK, //
    PUBREC, //
    // PUBREL,
    // PUBCOMP,
    SUBSCRIBE, //
    SUBACK, //
    UNSUBSCRIBE, //
    UNSUBACK, //
    PINGREQ, //
    PINGRESP, //
    DISCONNECT, //
    AUTH, //
    // Inter-Broker Communication (no typical MQTT messages, so other spelling)
    BrokerForwardDisconnect, //
    BrokerForwardPingreq, //
    BrokerForwardSubscribe, //
    BrokerForwardUnsubscribe, //
    BrokerForwardPublish //
}