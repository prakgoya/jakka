/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.dispatcher;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import akka.routing.BalancingPool;
import akka.routing.RoundRobinPool;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import jakka.cluster.dispatcher.Publisher;
import jakka.cluster.dispatcher.Subscriber;
import scala.concurrent.duration.Duration;

import java.net.InetAddress;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClusterDispatcherCreator {
    private static final Logger logger = Logger.getLogger(ClusterDispatcherCreator.class.getName());
    public ActorRef publisherActor;
    public ActorRef subscriberActor;
    private static ClusterDispatcherCreator instance = null;

    static ActorSystem publisherActorSystem = null;
    static ActorSystem subscriberActorSystem = null;

    public static ClusterDispatcherCreator getInstance() {
        if (instance == null) {
            instance = new ClusterDispatcherCreator();
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
        publisherActor = publisherActorSystem.actorOf(new RoundRobinPool(10).props(Props.create(Publisher.class)), "publisherActor");
        logger.info("Started publisher actor system ");

        publisherActorSystem.scheduler().schedule(Duration.create(3, SECONDS),
                Duration.create(10, MILLISECONDS), new Runnable() {
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
        subscriberActor = subscriberActorSystem.actorOf(Props.create(Subscriber.class),
                "subscriberActor");
        logger.info("Started subscriber actor system ");
    }
}
