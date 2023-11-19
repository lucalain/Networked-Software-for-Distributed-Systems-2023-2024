package com.lab.ex3;

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
