package com.lab.ex5;

import akka.actor.ActorRef;

public class ConfigMsg {

	private ActorRef server;
	
	public ConfigMsg (ActorRef server) {
		this.server = server;
	}

	public ActorRef getServerRef() {
		return server;
	}
}
