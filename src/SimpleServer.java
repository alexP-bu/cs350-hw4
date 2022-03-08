import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

class SimpleServer extends EventGenerator {

	private String name = null;
	private LinkedList<Request> theQueue = new LinkedList<Request>();
	Map<Double, Double> serviceTimesTable = new HashMap<>(); // table of service times with their probabilities
	private double maxQLen = 0; // 0 default = infinite queue

	/* Statistics of this server --- to construct rolling averages */
	private Double cumulQ = 0.0;
	private Double cumulW = 0.0;
	private Double cumulTq = 0.0;
	private Double cumulTw = 0.0;
	private Double busyTime = 0.0;
	private Double dropped = 0.0;
	private int snapCount = 0;
	private int servedReqs = 0;

	public SimpleServer(int numCores, double maxQLen, Timeline timeline, Double servTime) {
		super(timeline);
		this.maxQLen = maxQLen;
		// put a service time in the service time table with a 100% chance of occuring
		this.serviceTimesTable.put(servTime, 1.0);
	}

	public SimpleServer(int numCores, double maxQLen, Timeline timeline, double[] times, double[] probs) {
		super(timeline);
		this.maxQLen = maxQLen;
		// load variables into the server's time table
		for (int i = 0; i < times.length; i++) {
			double prob = probs[i];
			serviceTimesTable.merge(times[i], prob, (key, val) -> (val + prob));
		}
	}

	/*
	 * Given a name to this server that will be used for the trace and
	 * statistics
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * Internal method to be used to simulate the beginning of service
	 * for a queued/arrived request.
	 */
	private void __startService(Event evt, Request curRequest) {
		double serviceTime = getServiceTime();

		/* Generate death event */
		Event nextEvent = new Event(EventType.DEATH, curRequest, evt.getTimestamp() + Exp.getExp(1 / serviceTime),
				this);

		curRequest.recordServiceStart(evt.getTimestamp());
		cumulTw += curRequest.getServiceStart() - curRequest.getArrival();

		/* Print the occurrence of this event */
		System.out.println(curRequest + " START" +
				(this.name != null ? " " + this.name : "") +
				": " + evt.getTimestamp());
		
		super.timeline.addEvent(nextEvent);
	}

	@Override
	void receiveRequest(Event evt) {
		Request curRequest = evt.getRequest();
		
		// add request if:
		// queue length is infinite
		// or queue length is less than size
		if ((maxQLen == 0) || (theQueue.size() - 1 < maxQLen)) {
			super.receiveRequest(evt);
			curRequest.recordArrival(evt.getTimestamp());
			/*
			 * Upon receiving the request, check the queue size and act
			 * accordingly if there are available cores
			 */

			if (theQueue.isEmpty()) {
				__startService(evt, curRequest);
			}

			theQueue.add(curRequest);

		} else {
			// Drop request and print the drop occurance
			System.out.println(curRequest + " DROP" +
					(this.name != null ? " " + this.name : "") +
					": " + evt.getTimestamp());
			curRequest.recordDeparture(evt.getTimestamp());
			servedReqs++;
			dropped++;
		}

	}

	@Override
	void releaseRequest(Event evt) {
		/* What request we are talking about? */
		Request curRequest = evt.getRequest();

		/* Remove the request from the server queue */
		Request queueHead = theQueue.removeFirst();

		/* If the following is not true, something is wrong */
		assert curRequest == queueHead;

		curRequest.recordDeparture(evt.getTimestamp());

		/* Update busyTime */
		busyTime += curRequest.getDeparture() - curRequest.getServiceStart();

		/* Update cumulative response time at this server */
		cumulTq += curRequest.getDeparture() - curRequest.getArrival();

		/* Update number of served requests */
		servedReqs++;

		/* Print the occurrence of this event */
		System.out.println(evt.getRequest() + " DONE "
				+ this.name
				+ ": " + evt.getTimestamp());

		assert super.next != null;
		super.next.receiveRequest(evt);
		// pop processor from busy to available

		/* Any new request to put into service? */
		if (!theQueue.isEmpty()) {
			Request nextRequest = theQueue.peekFirst();
			__startService(evt, nextRequest);
		}
	}

	// get service time using the service time table probabilities
	private double getServiceTime() throws RuntimeException {
		double dice = Math.random();
		double cumulP = 0;
		for (Map.Entry<Double, Double> entry : serviceTimesTable.entrySet()) {
			cumulP += entry.getValue();

			if (dice < cumulP) {
				return entry.getKey();
			}
		}

		throw new RuntimeException(
				new Exception("ERROR GETTING SERVICE TIME IN SERVER " + this.name));
	}

	// returns average service time
	@Override
	Double getRate() {
		double cumulTs = 0;
		for (Map.Entry<Double, Double> entry : serviceTimesTable.entrySet()) {
			cumulTs += (entry.getKey() * entry.getValue());
		}
		return cumulTs / serviceTimesTable.size();
	}

	@Override
	void executeSnapshot() {
		snapCount++;
		cumulQ += theQueue.size();
		cumulW += Math.max(theQueue.size() - 1, 0);
	}

	@Override
	void printStats(Double time) {
		if (this.name == null) {
			System.out.println("UTIL: " + busyTime / time);
			System.out.println("QLEN: " + cumulQ / snapCount);
			System.out.println("TRESP: " + cumulTq / servedReqs);
		} else {
			System.out.println(this.name + " UTIL: " + busyTime / time);
			System.out.println(this.name + " QLEN: " + cumulQ / snapCount);
			System.out.println(this.name + " TRESP: " + cumulTq / servedReqs);
		}
		//print dropped num
		if(this.name.equals("S2")){
			System.out.println(this.name + " DROPPED: " + dropped);
		}
	}

	@Override
	public double getQ(){
		return cumulQ / snapCount;		
	}

	@Override
	public String toString() {
		return (this.name != null ? this.name : "");
	}

}
