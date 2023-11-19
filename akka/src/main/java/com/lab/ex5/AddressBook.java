package com.lab.ex5;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class AddressBook {

	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef client = sys.actorOf(AddressBookClientActor.props(), "client");
		
		scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, SECONDS);
		
		// Asking the supervisor to create the server
		final ActorRef supervisor = sys.actorOf(AddressBookSupervisorActor.props(), "supervisor");
		scala.concurrent.Future<Object> waitingForAddressBookServer = ask(supervisor, Props.create(AddressBookServerActor.class), 5000);
		ActorRef server = null;
		try {
			server = (ActorRef) waitingForAddressBookServer.result(timeout, null);
		} catch (TimeoutException | InterruptedException e) {
			e.printStackTrace();
		}
		
		// Tell the client who is the server
		client.tell(new ConfigMsg(server), ActorRef.noSender());

		// An example execution
		client.tell(new PutMsg("Luca","luca.mottola@polimi.it"), ActorRef.noSender());
		client.tell(new PutMsg("Alessandro","alessandro.margara@polimi.it"), ActorRef.noSender());

		client.tell(new GetMsg("Alessandro"), ActorRef.noSender());
		client.tell(new GetMsg("Gianpaolo"), ActorRef.noSender());
		
		// Now make the server fail
		client.tell(new PutMsg("Fail!","none"), ActorRef.noSender());
		
		// If resuming, this should return luca.mottola@polimi.it
		client.tell(new GetMsg("Luca"), ActorRef.noSender());
		
		sys.terminate();

	}

}
