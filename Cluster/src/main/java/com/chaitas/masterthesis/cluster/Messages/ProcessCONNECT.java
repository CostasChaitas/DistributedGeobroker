package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessCONNECT {

    public ExternalMessage message;
    public ActorRef wsClientActor;
    public ActorRef topicShardRegion;

    public ProcessCONNECT(@NotNull @JsonProperty("message") ExternalMessage message,
                          @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                          @NotNull @JsonProperty("topicShardRegion") ActorRef topicShardRegion){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.topicShardRegion = topicShardRegion;
    }
}
