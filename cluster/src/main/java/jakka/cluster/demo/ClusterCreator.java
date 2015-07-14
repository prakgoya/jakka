/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import scala.concurrent.duration.Duration;

import java.net.InetAddress;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ClusterCreator {
    private static final Logger logger = Logger.getLogger(ClusterCreator.class.getName());
    public ActorRef publisherActor;
    public ActorRef subscriberActor;
    private static ClusterCreator instance = null;

    static ActorSystem publisherActorSystem = null;
    static ActorSystem subscriberActorSystem = null;

    public ClusterCreator() {
    }

    public static ClusterCreator getInstance() {
        if (instance == null) {
            instance = new ClusterCreator();
        }
        return instance;
    }

    public void startPublisherActorSystem() {
        logger.info("Starting publisher actor system ");
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unknown host", e);
        }
        Config config = ConfigFactory.parseString("akka.netty.tcp.hostname = \"" + hostAddress + "\",akka.cluster" +
                ".seed-nodes = [\"akka.tcp://ClusterSystem@" + hostAddress + ":2553\"]");
        publisherActorSystem = ActorSystem.create("ClusterSystem", config.withFallback(ConfigFactory.load
                ("cluster-publisher")));
        publisherActor = publisherActorSystem.actorOf(Props.create(Publisher.class), "publisherActor");
        logger.info("Started publisher actor system ");

        publisherActorSystem.scheduler().schedule(Duration.create(10, SECONDS),
                Duration.create(10, SECONDS), new Runnable() {
                    public void run() {
                        String uuid = UUID.randomUUID().toString();
                        publisherActor.tell(uuid, null);
                    }
                }, publisherActorSystem.dispatcher());

    }

    public void startSubscriberActorSystem() {
        logger.info("Starting subscriber actor system ");
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unknown host", e);
        }
        Config config = ConfigFactory.parseString("akka.netty.tcp.hostname = \"" + hostAddress + "\",akka.cluster" +
                ".seed-nodes = [\"akka.tcp://ClusterSystem@" + hostAddress + ":2553\"]");
        subscriberActorSystem = ActorSystem.create("ClusterSystem", config.withFallback(ConfigFactory.load
                ("cluster-subscriber")));
        subscriberActor = subscriberActorSystem.actorOf(Props.create(Subscriber.class), "subscriberActor");
        logger.info("Started subscriber actor system ");
    }
}
