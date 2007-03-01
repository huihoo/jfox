/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package net.sourceforge.jfox.jms.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.Destination;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */

public class JMSDestinations {

//    private List<Destination> destinations = new ArrayList<Destination>();

	private Map<Destination, List<ConsumerMeta>> destinations = new HashMap<Destination, List<ConsumerMeta>>();

	private final static JMSDestinations me = new JMSDestinations();

	private JMSDestinations() {
	}

	public static JMSDestinations getInstance() {
		return me;
	}

	/**
	 * register a new destination
	 */
	public void registerDestination(Destination destination) {
		destinations.put(destination, new ArrayList<ConsumerMeta>());
	}

	//TODO: session.close ��ʱ��Ҫ unregister �� Session ������Destination
	public void unregisterDestination(Destination destination) {
		destinations.remove(destination);
	}

	public boolean isDestinationRegistered(Destination destination) {
		return destinations.containsKey(destination);
	}

	public void registerConsumer(Destination destination, ConsumerMeta consumerMeta) {
		if (!isDestinationRegistered(destination)) {
			List<ConsumerMeta> metas = new ArrayList<ConsumerMeta>();
			metas.add(consumerMeta);
			destinations.put(destination, metas);
		} else {
			List<ConsumerMeta> metas = getConsumerMetas(destination);
			metas.add(consumerMeta);
		}
	}

	public List<ConsumerMeta> getConsumerMetas(Destination destination) {
		if (!isDestinationRegistered(destination)) {
			return new ArrayList<ConsumerMeta>();
		} else {
			return destinations.get(destination);
		}
	}

	public boolean hashConsumer(Destination destination) {
		return !destinations.get(destination).isEmpty();
	}

	public static void main(String[] args) {

	}
}

