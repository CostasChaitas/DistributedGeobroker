package com.chaitas.masterthesis.commons.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@payloadType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CONNECTPayload.class, name = "CONNECTPayload"),
        @JsonSubTypes.Type(value = CONNACKPayload.class, name = "CONNACKPayload"),
        @JsonSubTypes.Type(value = DISCONNECTPayload.class, name = "DISCONNECTPayload"),
        @JsonSubTypes.Type(value = PINGREQPayload.class, name = "PINGREQPayload"),
        @JsonSubTypes.Type(value = PINGRESPPayload.class, name = "PINGRESPPayload"),
        @JsonSubTypes.Type(value = SUBSCRIBEPayload.class, name = "SUBSCRIBEPayload"),
        @JsonSubTypes.Type(value = PUBLISHPayload.class, name = "PUBLISHPayload"),
        @JsonSubTypes.Type(value = SUBACKPayload.class, name = "SUBACKPayload"),
        @JsonSubTypes.Type(value = PUBACKPayload.class, name = "PUBACKPayload"),
        @JsonSubTypes.Type(value = INCOMPATIBLEPayload.class, name = "INCOMPATIBLEPayload"),

})
public abstract class AbstractPayload {

    @JsonIgnore
    @Nullable
    public final CONNECTPayload getCONNECTPayload() {
        return this instanceof CONNECTPayload ? (CONNECTPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final CONNACKPayload getCONNACKPayload() {
        return this instanceof CONNACKPayload ? (CONNACKPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final DISCONNECTPayload getDISCONNECTPayload() {
        return this instanceof DISCONNECTPayload ? (DISCONNECTPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final PINGREQPayload getPINGREQPayload() {
        return this instanceof PINGREQPayload ? (PINGREQPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final PINGRESPPayload getPINGRESPPayload() {
        return this instanceof PINGRESPPayload ? (PINGRESPPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final PUBLISHPayload getPUBLISHPayload() {
        return this instanceof PUBLISHPayload ? (PUBLISHPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final PUBACKPayload getPUBACKPayload() {
        return this instanceof PUBACKPayload ? (PUBACKPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final SUBSCRIBEPayload getSUBSCRIBEPayload() {
        return this instanceof SUBSCRIBEPayload ? (SUBSCRIBEPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final SUBACKPayload getSUBACKPayload() {
        return this instanceof SUBACKPayload ? (SUBACKPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final UNSUBSCRIBEPayload getUNSUBSCRIBEPayload() {
        return this instanceof UNSUBSCRIBEPayload ? (UNSUBSCRIBEPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final UNSUBACKPayload getUNSUBACKPayload() {
        return this instanceof UNSUBACKPayload ? (UNSUBACKPayload)this : null;
    }

    @JsonIgnore
    @Nullable
    public final INCOMPATIBLEPayload getINCOMPATIBLEPayload() {
        return this instanceof INCOMPATIBLEPayload ? (INCOMPATIBLEPayload)this : null;
    }

}
