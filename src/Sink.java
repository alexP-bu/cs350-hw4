
/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a traffic sink. Any request that arrives at   */
/*   the sink is effectively released from the     */
/*   system.                                       */
/*                                                 */
/***************************************************/

class Sink extends EventGenerator {

	private Double cumulRespTime = 0.0;
	private Double cumulWaitTime = 0.0;
	private Double cumulProcSteps = 0.0;
	private int doneRequests = 0;

	public Sink(Timeline timeline) {
		super(timeline);
	}

	@Override
	void receiveRequest(Event evt) {
		super.receiveRequest(evt);

		Request doneReq = evt.getRequest();

		/* Print the occurrence of this event */
		System.out.println(evt.getRequest() + " DONE "
				+ doneReq.getLastServer()
				+ ": " + evt.getTimestamp());

		/* Update system stats */
		doneRequests++;

		/* Recover the time of entry in the system */
		EventGenerator entry = doneReq.getEntryPoint();
		Stats entryStats = doneReq.getStatsAtNode(entry);
		Double timeOfArrival = entryStats.arrival;

		/*
		 * Extract the total service and number of steps this request
		 * went through
		 */
		Double totalService = doneReq.getTotalService();
		int totalSteps = doneReq.getTotalSteps();

		cumulRespTime += evt.getTimestamp() - timeOfArrival;
		cumulWaitTime += (evt.getTimestamp() - timeOfArrival) - totalService;
		cumulProcSteps += totalSteps;
	}

	@Override
	public void printStats(Double time) {
		System.out.println("TRESP: " + cumulRespTime / doneRequests);
	}

}