package tasks;

import utils.Properties;
import web.Requests;

public abstract class Task implements Runnable {
    protected Requests requests;
    protected String appURL;

    public Task() {
        this.appURL = Properties.getSiteURL();
        this.requests = Requests.getInstance();
    }
}
