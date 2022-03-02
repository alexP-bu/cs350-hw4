import java.util.LinkedList;

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a single-processor server with an infinite    */
/*   request queue and exponentially distributed   */
/*   service times, i.e. a x/M/1 server.           */
/*                                                 */
/***************************************************/

class SimpleServer extends EventGenerator {

	private LinkedList<Request> theQueue = new LinkedList<Request>();
	private Double servTime;
	private String name = null;
	private int numCores = 1;
	private double[] times = null;
	private double[] probs = null;
	private double maxLen = 0; // 0 default = infinite queue

	/* Statistics of this server --- to construct rolling averages */
	private Double cumulQ = 0.0;
	private Double cumulW = 0.0;
	private Double cumulTq = 0.0;
	private Double cumulTw = 0.0;
	private Double busyTime = 0.0;
	private int snapCount = 0;
	private int servedReqs = 0;

	public SimpleServer(Timeline timeline, Double servTime) {
		super(timeline);

		/* Initialize the average service time of this server */
		this.servTime = servTime;
	}

	public SimpleServer(Timeline timeline, Double servTime, int numCores){
		super(timeline);
		this.servTime = servTime;
		this.numCores = numCores;
	}

	public SimpleServer(Timeline timeline, double[] times, double[] probs, Double maxLen) {
		super(timeline);
		this.times = times;
		this.probs = probs;
		this.maxLen = maxLen;
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
		//TODO: EDIT EVENT GENERATION CODE SERVETIME TO WORK WITH THE LISTS
		double dice = Math.random();
		if((this.times != null) && (this.probs != null)){
			//TODO
		}else{
			Event nextEvent = new Event(EventType.DEATH, curRequest,
			evt.getTimestamp() + Exp.getExp(1 / this.servTime), this);
		}

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
		super.receiveRequest(evt);

		Request curRequest = evt.getRequest();

		curRequest.recordArrival(evt.getTimestamp());

		/*
		 * Upon receiving the request, check the queue size and act
		 * accordingly
		 */
		if (theQueue.isEmpty()) {
			__startService(evt, curRequest);
		}

		theQueue.add(curRequest);
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

		assert super.next != null;
		super.next.receiveRequest(evt);

		/* Any new request to put into service? */
		if (!theQueue.isEmpty()) {
			Request nextRequest = theQueue.peekFirst();

			__startService(evt, nextRequest);
		}

	}

	@Override
	Double getRate() {
		return 1 / this.servTime;
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
			System.out.println("UTIL " + this.name + ": " + busyTime / time);
			System.out.println("QLEN " + this.name + ": " + cumulQ / snapCount);
		}
	}

	@Override
	public String toString() {
		return (this.name != null ? this.name : "");
	}

}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
