package jakka.remote.demo;

import akka.actor.ActorRef;
import akka.actor.ActorIdentity;
import akka.actor.Identify;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.actor.ReceiveTimeout;
import akka.japi.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AkkaSender extends UntypedActor {

    public static Logger logger = LoggerFactory.getLogger(AkkaSender.class);

    private final String path;
    private ActorRef receiver = null;

    public AkkaSender(String path) {
        this.path = path;
        sendIdentifyRequest();
    }

    private void sendIdentifyRequest() {
        getContext().actorSelection(path).tell(new Identify(path), getSelf());
        getContext().system().scheduler().scheduleOnce(Duration.create(3, SECONDS), getSelf(), ReceiveTimeout
                .getInstance(), getContext().dispatcher(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ActorIdentity) {
            receiver = ((ActorIdentity) message).getRef();
            if (receiver == null) {
                logger.warn("Remote actor not available: " + path);
            } else {
                getContext().watch(receiver);
                getContext().become(active, true);
            }

        } else if (message instanceof ReceiveTimeout) {
            sendIdentifyRequest();

        } else {
            logger.info("Not ready yet");
        }
    }

    Procedure<Object> active = new Procedure<Object>() {
        public void apply(Object message) {
            if (message instanceof String) {
                receiver.tell(((String) message).toUpperCase(), getSelf());
                logger.info("Message {} sent", message);
            } else if (message instanceof Terminated) {
                logger.info("Receiver terminated");
                sendIdentifyRequest();
                getContext().unbecome();

            } else if (message instanceof ReceiveTimeout) {
                // ignore

            } else {
                unhandled(message);
            }
            //TODO Sender logic
        }
    };
}
