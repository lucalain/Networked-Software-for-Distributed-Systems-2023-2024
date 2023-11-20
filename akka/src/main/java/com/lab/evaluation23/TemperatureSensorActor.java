package com.lab.evaluation23;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.concurrent.ThreadLocalRandom;

public class TemperatureSensorActor extends AbstractActor {

	private ActorRef dispatcher;
	private final static int MIN_TEMP = 0;
	private final static int MAX_TEMP = 50;

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(GenerateMsg.class, this::onGenerate)
				.match(ConfigMsg.class, this::onConfig)
				.build();
	}

	private void onGenerate(GenerateMsg msg) {
		System.out.println("TEMPERATURE SENSOR: Sensing temperature!");
		int temp = ThreadLocalRandom.current().nextInt(MIN_TEMP, MAX_TEMP + 1);
		dispatcher.tell(new TemperatureMsg(temp,self()), self());
	}
	private void onConfig(ConfigMsg msg) {
		System.out.println("DISPATCHER ON TEMPERATURE SENSOR configured");
		this.dispatcher = msg.getDispatcher();
	}

	static Props props() {
		return Props.create(TemperatureSensorActor.class);
	}

}
