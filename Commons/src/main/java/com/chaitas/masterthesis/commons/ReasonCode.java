// Code adapted from Geobroker project : https://github.com/MoeweX/geobroker

package com.chaitas.masterthesis.commons;

public enum ReasonCode {
    NormalDisconnection, //
    ProtocolError, //
    NotConnected, //
    GrantedQoS0, //
    Success, //
    NoMatchingSubscribers, //
    NoSubscriptionExisted, //

    ConnectionAlreadyExist, //
    IncompatiblePayload , //
    // New Reason Codes
    LocationUpdated, //
    WrongBroker, //
    NoMatchingSubscribersButForwarded //  locally there are no subscribers, but others MIGHT have some
}
