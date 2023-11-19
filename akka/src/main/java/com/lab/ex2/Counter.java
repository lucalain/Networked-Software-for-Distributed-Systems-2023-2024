package com.lab.ex2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Counter {

	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef counter = sys.actorOf(CounterActorIncrementDecrement.props(), "counter");

		counter.tell(new SimpleMessage(SimpleMessage.DECREMENT), ActorRef.noSender());
		counter.tell(new SimpleMessage(SimpleMessage.DECREMENT), ActorRef.noSender());
		counter.tell(new SimpleMessage(SimpleMessage.INCREMENT), ActorRef.noSender());
		counter.tell(new SimpleMessage(SimpleMessage.DECREMENT), ActorRef.noSender());
		counter.tell(new SimpleMessage(SimpleMessage.INCREMENT), ActorRef.noSender());
		counter.tell(new SimpleMessage(SimpleMessage.INCREMENT), ActorRef.noSender());
		
		sys.terminate();

	}

}
