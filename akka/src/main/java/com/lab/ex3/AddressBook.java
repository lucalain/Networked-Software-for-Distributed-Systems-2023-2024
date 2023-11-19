package com.lab.ex3;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class AddressBook {

	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef server = sys.actorOf(AddressBookServerActor.props(), "server");
		final ActorRef client = sys.actorOf(AddressBookClientActor.props(), "client");
		
		// Tell the client who is the server
		client.tell(new ConfigMsg(server), ActorRef.noSender());

		// An example execution
		client.tell(new PutMsg("Luca","luca.mottola@polimi.it"), ActorRef.noSender());
		client.tell(new PutMsg("Alessandro","alessandro.margara@polimi.it"), ActorRef.noSender());

		client.tell(new GetMsg("Alessandro"), ActorRef.noSender());
		client.tell(new GetMsg("Gianpaolo"), ActorRef.noSender());
		
		sys.terminate();

	}

}
