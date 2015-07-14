/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.cluster.dispatcher;

public class ClusterDispatcerApplication {
    public static void main(String[] args) {
        ClusterDispatcherCreator.getInstance().startSubscriberActorSystem();
        ClusterDispatcherCreator.getInstance().startPublisherActorSystem();
    }
}
