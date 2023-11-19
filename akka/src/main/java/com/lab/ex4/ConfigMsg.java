package com.lab.ex4;

import akka.actor.ActorRef;

public class ConfigMsg extends Msg {

	private ActorRef server;
	
	public ConfigMsg (ActorRef server) {
		this.server = server;
	}

	public ActorRef getServerRef() {
		return server;
	}
}
