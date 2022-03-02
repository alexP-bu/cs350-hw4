 
    

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a single event that can be sorted in time.    */
/*   Each event is uniquely identified via an ID   */
/*   and a type. Each event also has a timestamp   */
/*   and a request the event refers to.            */
/*                                                 */
/***************************************************/

public class Event implements Comparable<Event> {

    private EventType type;
    private Double ts;
    private Request rq;
    private EventGenerator source;
    
    /* Verbose constructor (with request) */
    public Event(EventType type, Request rq, Double ts, EventGenerator source) {
        super();
        this.type = type;
        this.ts = ts;
	this.rq = rq;
	this.source = source;
    }
    
    /* Generate next event given a previous event of the same type */
    public Event(Event evt, Double IAT) {
        super();
	this.type = evt.type;
	this.ts = evt.ts + Exp.getExp(1/IAT);
	this.rq = evt.rq;
    }
    
    @Override
    public int compareTo(Event evt) {
        return this.getTimestamp().compareTo(evt.getTimestamp());
    }

    /* timestamp getter */
    public Double getTimestamp() {
	return this.ts;
    }

    /* type getter */
    public EventType getType() {
	return this.type;
    }

    /* Request getter */
    public Request getRequest() {
	return this.rq;
    }

    /* Event source block getter */
    public EventGenerator getSource() {
	return this.source;
    }

    @Override
    public String toString() {
        return this.rq.toString() + type + ": " + ts;
    }
}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
