// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.commons;

public enum ControlPacketType {
    Reserved, //
    CONNECT, //
    CONNACK, //
    DISCONNECT, //
    PINGREQ, //
    PINGRESP, //
    PUBLISH, //
    PUBACK, //
    SUBSCRIBE, //
    SUBACK, //
    UNSUBSCRIBE, //
    UNSUBACK, //
    NOTCONNECTED, //
    CONNECTIONEXIST, //
    MATCH, //
    INCOMPATIBLEPayload, //
}