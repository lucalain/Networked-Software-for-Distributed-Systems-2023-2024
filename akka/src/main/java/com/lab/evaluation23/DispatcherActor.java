package com.lab.evaluation23;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.lab.evaluation23.DispatchLogicMsg.LOAD_BALANCER;

public class DispatcherActor extends AbstractActorWithStash {

	private final static int NO_PROCESSORS = 2;

	private HashMap<ActorRef, List<ActorRef>> loadBalanceMap;
	private List<ActorRef> processors;
	private int counter = 0;

	private static SupervisorStrategy strategy =
			new OneForOneStrategy(
					1,
					Duration.ofMinutes(1),
					DeciderBuilder.match(Exception.class, e -> SupervisorStrategy.resume())
							.build());

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	public DispatcherActor() {
		processors = new ArrayList<>();
		loadBalanceMap = new HashMap<>();
		for (int i = 0; i < NO_PROCESSORS; i++) {
			ActorRef processor = getContext().actorOf(SensorProcessorActor.props());
			processors.add(processor);
			loadBalanceMap.put(processor, new ArrayList<ActorRef>());
		}
	}
	@Override
	public Receive createReceive() {
		return loadBalance();
	}

	public Receive loadBalance() {
		return receiveBuilder()
				.match(TemperatureMsg.class, this::dispatchDataLoadBalancer)
				.match(DispatchLogicMsg.class, this::swichLogic)
				.build();
	}
	public Receive roundRobin() {
		return receiveBuilder()
				.match(TemperatureMsg.class, this::dispatchDataRoundRobin)
				.match(DispatchLogicMsg.class, this::swichLogic)
				.build();
	}

	private void swichLogic(DispatchLogicMsg msg) {
		if(msg.getLogic() == LOAD_BALANCER) {
			getContext().become(loadBalance());
			System.out.println("LoadBalancer");
		} else {
			getContext().become(roundRobin());
			System.out.println("RoundRobin");
		}
	}
	private void dispatchDataLoadBalancer(TemperatureMsg msg) {
		int min = Integer.MAX_VALUE;
		ActorRef p = null;

		for(ActorRef r :  loadBalanceMap.keySet()){
			if(loadBalanceMap.get(r).contains(msg.getSender())){
				p = r;
				break;
			}

			if(loadBalanceMap.get(r).size()< min) {
				p = r;
				min = loadBalanceMap.get(r).size();
			}
		}

		if(!loadBalanceMap.get(p).contains(msg.getSender())) {
			loadBalanceMap.get(p).add(msg.getSender());
		}

		p.tell(msg, self());

	}

	private void dispatchDataRoundRobin(TemperatureMsg msg) {
		processors.get(counter).tell(msg, self());
		counter = (counter+1)%NO_PROCESSORS;
	}

	static Props props() {
		return Props.create(DispatcherActor.class);
	}
}
