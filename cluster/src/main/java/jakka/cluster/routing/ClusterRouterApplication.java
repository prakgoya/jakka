/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.routing;

public class ClusterRouterApplication {
    public static void main(String[] args) {
        ClusterRouterCreator.getInstance().startSubscriberActorSystem();
        ClusterRouterCreator.getInstance().startPublisherActorSystem();
    }
}
