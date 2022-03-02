import java.util.LinkedList;

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a simulation monitor with exponentially       */
/*   distributed inter-arrival of snapshotting     */
/*   events.                                       */
/*                                                 */
/***************************************************/

class Monitor extends EventGenerator {

    private Double rate;
    private Simulator sim;
    private LinkedList<EventGenerator> resources;

    /* Construct a traffic source */
    public Monitor (Timeline timeline, Double lambda, LinkedList<EventGenerator> resources) {
	super(timeline);
	this.rate = lambda;
	this.resources = resources;

	/* Insert the very first event into the timeline */
	Event firstEvent = new Event(EventType.MONITOR, null,  0.0, this);

	super.timeline.addEvent(firstEvent);
    }

    @Override
    void processEvent(Event evt) {
	/* New monitor event! Generate next and acquire statistics */
	
	Event nextEvent = new Event(EventType.MONITOR, null,
				    evt.getTimestamp() + Exp.getExp(this.rate), this);

	for (int i = 0; i < resources.size(); ++i) {
	    resources.get(i).executeSnapshot();
	}
	
	super.timeline.addEvent(nextEvent);	
    }

    @Override
    Double getRate() {
	return this.rate;
    }
    
}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
