import java.util.HashMap;

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   a single request flowing through multiple     */
/*   system resources. It keeps a set of           */
/*   statistics for each of traversed resource.    */
/*                                                 */
/***************************************************/

class Request {
    private EventGenerator _at;
    private EventGenerator _createdAt;
    private int id;
    private static int unique_ID = 0;
    private HashMap<EventGenerator, Stats> stats = new HashMap<EventGenerator, Stats>();
    private EventGenerator _lastServer;
    private int _procSteps = 0;
    private Double _serviceTime = 0.0;

    public Request(EventGenerator created_at) {
        this._at = created_at;
        this._createdAt = created_at;
        this.id = unique_ID;
        unique_ID++;

        this.stats.put(this._at, new Stats());
    }

    public void moveTo(EventGenerator at) {
        this._at = at;
        this.stats.put(this._at, new Stats());
    }

    public EventGenerator where() {
        return this._at;
    }

    @Override
    public String toString() {
        return "R" + this.id;
    }

    public void recordArrival(Double ts) {
        Stats curStats = this.stats.get(this._at);
        curStats.arrival = ts;
    }

    public void recordServiceStart(Double ts) {
        Stats curStats = this.stats.get(this._at);
        curStats.serviceStart = ts;
        this._lastServer = this._at;
        this._procSteps++;
    }

    public void recordDeparture(Double ts) {
        Stats curStats = this.stats.get(this._at);
        curStats.departure = ts;
        this._serviceTime += curStats.departure - curStats.serviceStart;
    }

    public Double getArrival() {
        Stats curStats = this.stats.get(this._at);
        return curStats.arrival;
    }

    public Double getServiceStart() {
        Stats curStats = this.stats.get(this._at);
        return curStats.serviceStart;
    }

    public Double getTotalService() {
        return this._serviceTime;
    }

    public int getTotalSteps() {
        return this._procSteps;
    }

    public Double getDeparture() {
        Stats curStats = this.stats.get(this._at);
        return curStats.departure;
    }

    public EventGenerator getLastServer() {
        return this._lastServer;
    }

    public EventGenerator getEntryPoint() {
        return this._createdAt;
    }

    public Stats getStatsAtNode(EventGenerator node) {
        return this.stats.get(node);
    }

}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
