public class SimpleProcessor {
    public String state;
    public boolean isBusy;
    public Request currRequest;

    public SimpleProcessor(){
        this.state = "INIT";
        this.isBusy  = false;
    }
}
