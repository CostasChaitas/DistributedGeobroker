package com.chaitas.masterthesis.commons.message;

import com.chaitas.masterthesis.commons.ControlPacketType;
import com.chaitas.masterthesis.commons.payloads.AbstractPayload;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class InternalServerMessage {
    private String clientIdentifier;
    private ControlPacketType controlPacketType;
    private AbstractPayload payload;

    public InternalServerMessage(
            @JsonProperty("clientIdentifier") String clientIdentifier,
            @JsonProperty("controlPacketType") ControlPacketType controlPacketType,
            @JsonProperty("payload") AbstractPayload payload) {
        this.clientIdentifier = clientIdentifier;
        this.controlPacketType = controlPacketType;
        this.payload = payload;
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public ControlPacketType getControlPacketType() {
        return controlPacketType;
    }

    public AbstractPayload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "InternalServerMessage{" +
                "clientIdentifier='" + clientIdentifier + '\'' +
                ", controlPacketType=" + controlPacketType +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalServerMessage)) {
            return false;
        }
        InternalServerMessage that = (InternalServerMessage) o;
        return Objects.equals(getClientIdentifier(), that.getClientIdentifier()) &&
                getControlPacketType() == that.getControlPacketType() &&
                Objects.equals(getPayload(), that.getPayload());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getClientIdentifier(), getControlPacketType(), getPayload());
    }
}
