/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.demo;

public class ClusterApplication {
    public static void main(String[] args) {
        ClusterCreator.getInstance().startSubscriberActorSystem();
        ClusterCreator.getInstance().startPublisherActorSystem();
    }
}
