package com.lab.ex2;

import akka.actor.AbstractActorWithStash;
import akka.actor.Props;

public class CounterActorIncrementDecrement extends AbstractActorWithStash {

	private int counter;

	public CounterActorIncrementDecrement() {
		this.counter = 0;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(SimpleMessage.class, this::onMessage).build();
	}

	void onMessage(SimpleMessage msg) {
		if (msg.getOperation() == SimpleMessage.INCREMENT) {
			++counter;
			System.out.println("SimpleMessage received to increase counter to " + counter);
			unstashAll();
		} else if (counter == 0) {
			stash();
		} else {
			--counter;
			System.out.println("SimpleMessage received to decrease counter to " + counter);
		}
	}

	static Props props() {
		return Props.create(CounterActorIncrementDecrement.class);
	}

}
