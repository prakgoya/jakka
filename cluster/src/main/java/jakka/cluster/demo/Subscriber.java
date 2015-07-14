/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.demo;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Subscriber extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public Subscriber() {
        ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();
        // subscribe to the topic named "endpoint"
        mediator.tell(new DistributedPubSubMediator.Subscribe("endpoint", getSelf()), getSelf());
    }

    public void onReceive(Object msg) {
        if (msg instanceof String) {
            log.info("Got: " + msg);
        } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            log.info("Subscribing");
        } else {
            unhandled(msg);
        }
    }
}
