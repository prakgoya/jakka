/*------------------------------------------------------------------------------
 - Copyright (c) 2015. Prakhar Goyal.
 - All rights reserved.
 -----------------------------------------------------------------------------*/

package jakka.remote.demo;

public class RemoteApplication {
    public static void main(String[] args) {
        Simulator.getInstance().startReceiverActorSystem();
        Simulator.getInstance().startSenderActorSystem();
    }
}
