/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.routing;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Publisher extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    // activate the extension
    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    public void onReceive(Object msg) {
        if (msg instanceof String) {
            String in = (String) msg;
            String out = in.toUpperCase();
            mediator.tell(new DistributedPubSubMediator.Publish("endpoint", out), getSelf());
            log.info("Message " + msg + " published");
        } else {
            unhandled(msg);
        }
    }
}
