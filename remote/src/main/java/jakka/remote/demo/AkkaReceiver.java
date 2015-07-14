package jakka.remote.demo;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AkkaReceiver extends UntypedActor {

    public static Logger logger = LoggerFactory.getLogger(AkkaReceiver.class);

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            logger.info("Got: " + message);
        } else {
            unhandled(message);
        }
        //TODO Processing of message
    }
}
