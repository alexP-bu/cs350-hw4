 

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a traffic source from the outside of the      */
/*   simulated system. The inter-arrival of the    */
/*   incoming traffic is exponentially distributed */
/*                                                 */
/***************************************************/

class Source extends EventGenerator {

    private Double rate;

    /* Construct a traffic source */
    public Source (Timeline timeline, Double lambda) {
	super(timeline);
	this.rate = lambda;

	/* Insert the very first event into the timeline at time 0 */
	Request firstRequest = new Request(this);
	Event firstEvent = new Event(EventType.BIRTH, firstRequest,  0.0, this);

	super.timeline.addEvent(firstEvent);
    }

    @Override
    void receiveRequest(Event evt) {
	Request curRequest = evt.getRequest();
	curRequest.recordArrival(evt.getTimestamp());

	/* When it's time to process as new arrival, generate the next
	 * arrival and request, and hand-off the current request to
	 * the next hop */	
	Request nextReq = new Request(this);
	Event nextEvent = new Event(EventType.BIRTH, nextReq,
				    evt.getTimestamp() + Exp.getExp(this.rate), this);

	/* Print the occurrence of this event */
	System.out.println(evt.getRequest() + " ARR: " + evt.getTimestamp());
	
	super.timeline.addEvent(nextEvent);
	
	assert super.next != null;
	super.next.receiveRequest(evt);
    }

    @Override
    Double getRate() {
	return this.rate;
    }

    
}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
