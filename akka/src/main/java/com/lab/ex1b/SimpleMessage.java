package com.lab.ex1b;

public class SimpleMessage {

	private int operation;
	public static final int INCREMENT = 1;
	public static final int DECREMENT = 0;
	
	public SimpleMessage (int operation) {
		this.operation = operation;
	}

	public int getOperation() {
		return operation;
	}
}
