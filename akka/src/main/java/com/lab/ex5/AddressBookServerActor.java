package com.lab.ex5;

import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class AddressBookServerActor extends AbstractActor {

	private HashMap<String, String> addresses;

	public AddressBookServerActor() {
		this.addresses = new HashMap<String, String>();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(GetMsg.class, this::generateReply).match(PutMsg.class, this::storeEntry).build();
	}

	void generateReply(GetMsg msg) {
		System.out.println("SERVER: Received query for name " + msg.getName());
		String email = addresses.get(msg.getName());
		sender().tell(new ReplyMsg(email), self());
	}

	void storeEntry(PutMsg msg) throws Exception {
		if (msg.getName() == "Fail!") {
			throw new Exception("Server failing");
		} else {
			System.out.println("SERVER: Received new entry " + msg.getName() + " - " + msg.getEmail());
			addresses.put(msg.getName(), msg.getEmail());

		}
	}

	static Props props() {
		return Props.create(AddressBookServerActor.class);
	}

}
