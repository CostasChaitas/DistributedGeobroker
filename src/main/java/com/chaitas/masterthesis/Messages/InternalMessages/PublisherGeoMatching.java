package com.chaitas.masterthesis.Messages.InternalMessages;

import com.chaitas.masterthesis.Storage.Subscription;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PublisherGeoMatching {

    public ProcessPUBLISH publication;
    public Subscription subscription;

    public PublisherGeoMatching(@NotNull @JsonProperty("publication") ProcessPUBLISH publication,
                                @NotNull @JsonProperty("publication") Subscription subscription){
        this.publication = publication;
        this.subscription = subscription;
    }

}