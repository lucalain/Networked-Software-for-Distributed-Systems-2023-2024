package com.lab.ex4;

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Echo {

	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef server = sys.actorOf(SleepyEchoServerActor.props(), "server");
		final ActorRef client = sys.actorOf(EchoClientActor.props(), "client");

		// Tell the client who is the server
		client.tell(new ConfigMsg(server), ActorRef.noSender());

		// An example execution
		client.tell(new TextMsg("Hello Luca!", ActorRef.noSender()), ActorRef.noSender());
		client.tell(new TextMsg("Hello Alessandro!", ActorRef.noSender()), ActorRef.noSender());

		client.tell(new SleepMsg(), ActorRef.noSender());

		client.tell(new TextMsg("You should be sleeping now 1!", ActorRef.noSender()), ActorRef.noSender());
		client.tell(new TextMsg("You should be sleeping now 2!", ActorRef.noSender()), ActorRef.noSender());

		client.tell(new WakeupMsg(), ActorRef.noSender());

		// Wait for messages to eventually arrive
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sys.terminate();

	}

}
