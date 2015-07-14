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

import static java.util.concurrent.TimeUnit.SECONDS;

public class ClusterCreator {
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
        System.out.println("Starting publisher actor system ");
        String hostIp = null;
        try {
            hostIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.out.println("Unknown Host");
        }
        Config config = ConfigFactory.parseString("akka.netty.tcp.hostname = \"" + hostIp + "\",akka.cluster" +
                ".seed-nodes = [\"akka.tcp://ClusterSystem@" + hostIp + ":2553\"]");
        publisherActorSystem = ActorSystem.create("ClusterSystem", config.withFallback(ConfigFactory.load
                ("cluster-publisher")));
        publisherActor = publisherActorSystem.actorOf(Props.create(Publisher.class), "publisherActor");
        System.out.println("Started publisher actor system ");

        publisherActorSystem.scheduler().schedule(Duration.create(10, SECONDS),
                Duration.create(10, SECONDS), new Runnable() {
                    public void run() {
                        String uuid = UUID.randomUUID().toString();
                        publisherActor.tell(uuid, null);
                    }
                }, publisherActorSystem.dispatcher());

    }

    public void startSubscriberActorSystem() {
        System.out.println("Starting subscriber actor system ");
        String hostIp = null;
        try {
            hostIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.out.println("Unknown Host");
        }
        Config config = ConfigFactory.parseString("akka.netty.tcp.hostname = \"" + hostIp + "\",akka.cluster" +
                ".seed-nodes = [\"akka.tcp://ClusterSystem@" + hostIp + ":2553\"]");
        subscriberActorSystem = ActorSystem.create("ClusterSystem", config.withFallback(ConfigFactory.load
                ("cluster-subscriber")));
        subscriberActor = subscriberActorSystem.actorOf(Props.create(Subscriber.class), "subscriberActor");
        System.out.println("Started subscriber actor system ");
    }
}
