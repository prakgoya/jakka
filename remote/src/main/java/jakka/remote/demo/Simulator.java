package jakka.remote.demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;

import java.net.InetAddress;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Simulator {

    public static Logger logger = LoggerFactory.getLogger(Simulator.class);
    public ActorRef senderActor;
    public ActorRef receiverActor;

    public ActorSystem senderActorSystem;
    public ActorSystem receiverActorSystem;

    private static Simulator instance = null;

    public Simulator() {
    }

    public static Simulator getInstance() {
        if (instance == null) {
            instance = new Simulator();
        }
        return instance;
    }

    public void startSenderActorSystem() {
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("Unknown host", e);
        }

        logger.info("Starting sender actor system");

        senderActorSystem = ActorSystem.create("senderActorSystem", ConfigFactory.load("simulator-sender"));
        String path = "akka.tcp://receiverActorSystem@" + hostAddress + ":2554/user/receiverActor";
        senderActor = senderActorSystem.actorOf(Props.create(AkkaSender.class, path), "senderActor");

        logger.info("Started sender actor system");

        senderActorSystem.scheduler().schedule(Duration.create(1, SECONDS),
                Duration.create(1, SECONDS), new Runnable() {
                    public void run() {
                        String uuid = UUID.randomUUID().toString();
                        senderActor.tell(uuid, null);
                    }
                }, senderActorSystem.dispatcher());
        logger.info("Started scheduler for sender actor system");
    }

    public void startReceiverActorSystem() {
        logger.info("Starting receiver actor system");

        receiverActorSystem = ActorSystem.create("receiverActorSystem", ConfigFactory.load("simulator-receiver"));
        receiverActor = receiverActorSystem.actorOf(Props.create(AkkaReceiver.class), "receiverActor");

        logger.info("Started receiver actor system");
    }

}
