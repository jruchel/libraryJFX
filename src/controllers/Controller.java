package controllers;

import java.util.HashMap;
import java.util.Map;

public abstract class Controller {
    protected Map<String, Object> parameters = new HashMap<>();

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public abstract void initializeManually();
}
