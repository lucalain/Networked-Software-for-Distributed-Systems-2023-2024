package com.lab.ex4;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class EchoClientActor extends AbstractActor {

	private ActorRef server;

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(TextMsg.class, this::sendText).match(ConfigMsg.class, this::configure)
				.match(SleepMsg.class, this::reroute).match(WakeupMsg.class, this::reroute).build();
	}

	void reroute(Msg msg) {
		System.out.println("CLIENT: Rerouting message " +msg);
		server.tell(msg, self());
	}

	void sendText(TextMsg msg) {
		if (msg.getSender() == ActorRef.noSender()) {
			// Message coming from outside the actor system
			System.out.println("CLIENT: Sending text message: " + msg.getText());
			msg.setSender(self());
			server.tell(msg, self());
	 	} else {
			// The server is sending this back 
	 		System.out.println("CLIENT: Received reply, text: " + msg.getText());
	 	}
	}

	void configure(ConfigMsg msg) {
		System.out.println("CLIENT: Received configuration message!");
		server = msg.getServerRef();
	}

	static Props props() {
		return Props.create(EchoClientActor.class);
	}

}
