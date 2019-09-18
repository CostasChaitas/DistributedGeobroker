package com.chaitas.masterthesis.cluster.Messages;

import akka.actor.ActorRef;
<<<<<<< HEAD
import com.chaitas.masterthesis.commons.message.InternalServerMessage;
=======
import com.chaitas.masterthesis.cluster.Storage.Subscription;
import com.chaitas.masterthesis.commons.message.ExternalMessage;
>>>>>>> master
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ProcessSUBSCRIBE {

<<<<<<< HEAD
    public InternalServerMessage message;
    public ActorRef wsClientActor;
    public int tileId;

    public ProcessSUBSCRIBE(@NotNull @JsonProperty("message") InternalServerMessage message,
                            @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor,
                            @NotNull @JsonProperty("tileId") int tileId){
        this.message = message;
        this.wsClientActor = wsClientActor;
        this.tileId = tileId;
    }
}
=======
    public ExternalMessage message;
    public ActorRef wsClientActor;
    public Subscription subscription;

    public ProcessSUBSCRIBE(@NotNull @JsonProperty("message") ExternalMessage message,
                            @NotNull @JsonProperty("wsClientActor") ActorRef wsClientActor){
        this.message = message;
        this.wsClientActor = wsClientActor;
    }

}
>>>>>>> master
