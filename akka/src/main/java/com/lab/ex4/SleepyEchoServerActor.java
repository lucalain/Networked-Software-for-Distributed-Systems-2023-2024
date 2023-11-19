package com.lab.ex4;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;

public class SleepyEchoServerActor extends AbstractActorWithStash {

	@Override
	public Receive createReceive() {
		return awake();
	}

	private final Receive awake() {
		return receiveBuilder().match(TextMsg.class, this::echo).match(SleepMsg.class, this::goToSleep).build();
	}

	private final Receive sleepy() {
		return receiveBuilder().match(TextMsg.class, this::putAside).match(WakeupMsg.class, this::wakeup).build();
	}

	// Processing messages
	void echo(TextMsg msg) {
		System.out.println("SERVER: Echo-ing back msg to "+msg.getSender()+" with text: "+msg.getText());
		msg.getSender().tell(msg, self());
	}
	
	void putAside(TextMsg msg) {
		System.out.println("SERVER: Setting aside msg...");
		stash();
	}

	// Changes of behavior
	void goToSleep (SleepMsg msg) {		
		System.out.println("SERVER: Going to sleep... ");
		getContext().become(sleepy());
	}

	void wakeup (WakeupMsg msg) {		
		System.out.println("SERVER: Waking up!");
		getContext().become(awake());
		unstashAll();
	}

	static Props props() {
		return Props.create(SleepyEchoServerActor.class);
	}

}
