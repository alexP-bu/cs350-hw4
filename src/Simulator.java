import java.util.ArrayList;
import java.util.LinkedList;

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a simulator where a single source of events   */
/*   is connected to a single exit point, with a   */
/*   single-processor server in the middle.        */
/*                                                 */
/***************************************************/

public class Simulator {

	/* These are the resources that we intend to monitor */
	private LinkedList<EventGenerator> resources = new LinkedList<EventGenerator>();

	/* Timeline of events */
	private Timeline timeline = new Timeline();

	/* Simulation time */
	private Double now;

	public void addMonitoredResource(EventGenerator resource) {
		this.resources.add(resource);
	}

	/*
	 * This method creates a new monitor in the simulator. To collect
	 * all the necessary statistics, we need at least one monitor.
	 */
	private void addMonitor() {
		/*
		 * Scan the list of resources to understand the granularity of
		 * time scale to use
		 */
		Double monRate = Double.POSITIVE_INFINITY;

		for (int i = 0; i < resources.size(); ++i) {
			// TODO: EDIT CODE HERE FOR PROBABILITY LISTS?
			Double rate = resources.get(i).getRate();
			if (monRate > rate) {
				monRate = rate;
			}
		}

		/*
		 * If this fails, something is wrong with the way the
		 * resources have been instantiated
		 */
		assert !monRate.equals(Double.POSITIVE_INFINITY);

		/* Create a new monitor for this simulation */
		Monitor monitor = new Monitor(timeline, monRate, resources);

	}

	public void simulate(Double simTime) {

		/* Rewind time */
		now = 0.0;

		/* Add monitor to the system */
		addMonitor();

		/* Main simulation loop */
		while (now < simTime) {
			/* Fetch event from timeline */
			Event evt = timeline.popEvent();

			/* Fast-forward time */
			now = evt.getTimestamp();

			/* Extract block responsible for this event */
			EventGenerator block = evt.getSource();

			/* Handle event */
			block.processEvent(evt);

		}

		/* Print all the statistics */
		for (int i = 0; i < resources.size(); ++i) {
			resources.get(i).printStats(now);
		}

	}

	/* Entry point for the entire simulation */
	public static void main(String[] args) {

		/* Parse the input parameters */
		// new vars
		ArrayList<Double> vars = new ArrayList<Double>();
		for (String arg : args) {
			vars.add(Double.valueOf(arg));
		}

		/* Create a new simulator instance */
		Simulator sim = new Simulator();

		/* Create the traffic source */
		Source trafficSource = new Source(sim.timeline, vars.get(1));

		/* Create a new traffic sink */
		Sink trafficSink = new Sink(sim.timeline);

		/* Create a server with 1 cpu and infinite queue length */
		SimpleServer S0 = new SimpleServer(1, 0, sim.timeline, vars.get(2));
		/* Create a server with 2 cpus and infinite queue length */
		// TODO: IMPLEMENT DUAL CORE SERVER
		SimpleServer S1 = new SimpleServer(2, 0, sim.timeline, vars.get(3));
		/* Create a server with 1 cpu and a max queue length of k */
		SimpleServer S2 = new SimpleServer(1, vars.get(11), sim.timeline, vars.get(4));
		/* Create a server with 1 cpu and a probability assigned to a service time */
		// TODO: IMPLEMENT SERVER WITH PROBABILITY/TIME LISTS
		SimpleServer S3 = new SimpleServer(1, 0, sim.timeline,
				new double[] { vars.get(5), vars.get(7), vars.get(9) }, // times
				new double[] { vars.get(6), vars.get(8), vars.get(10) } // probabilities
		);

		/*
		 * Give some names to identify these servers when printing
		 * trace and statistics
		 */
		S0.setName("S0");
		S1.setName("S1");
		S1.setName("S3");

		/* Create two routing nodes */
		RoutingNode rn1 = new RoutingNode(sim.timeline);
		RoutingNode rn2 = new RoutingNode(sim.timeline);

		/* Establish routing */
		trafficSource.routeTo(S0);
		S0.routeTo(rn1);
		rn1.routeTo(S1, vars.get(12));
		rn1.routeTo(S2, vars.get(13));
		S1.routeTo(S3);
		S2.routeTo(S3);
		S3.routeTo(rn2);
		rn2.routeTo(S1, vars.get(15));
		rn2.routeTo(S2, vars.get(16));
		rn2.routeTo(trafficSink, vars.get(14));

		/* Add resources to be monitored */
		sim.addMonitoredResource(S0);
		sim.addMonitoredResource(S1);
		sim.addMonitoredResource(S3);
		sim.addMonitoredResource(trafficSink);

		/* Kick off simulation */
		sim.simulate(vars.get(0));
	}

}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
