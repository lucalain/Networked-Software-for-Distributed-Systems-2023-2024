package com.lab.ex1b;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class CounterActorIncrementDecrement extends AbstractActor {

	private int counter;

	public CounterActorIncrementDecrement() {
		this.counter = 0;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(SimpleMessage.class, this::onMessage).build();
	}

	void onMessage(SimpleMessage msg) {
		if (msg.getOperation()==SimpleMessage.INCREMENT) {
			++counter;
			System.out.println("SimpleMessage received to increase counter to " + counter);
		} else {
			--counter;
			System.out.println("SimpleMessage received to decrease counter to " + counter);			
		}
	}

	static Props props() {
		return Props.create(CounterActorIncrementDecrement.class);
	}

}
