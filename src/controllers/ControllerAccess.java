package controllers;

import java.util.HashMap;
import java.util.Map;

public class ControllerAccess {

    public static ControllerAccess instance;

    public static ControllerAccess getInstance() {
        if (instance == null) instance = new ControllerAccess();
        return instance;
    }

    private ControllerAccess() {
        this.controllers = new HashMap<>();
    }

    public Map<String, Object> controllers;

    public void put(String key, Object controller) {
        controllers.putIfAbsent(key, controller);
    }

    public void replace(String key, Object controller) {
        controllers.replace(key, controller);
    }

    public Object get(String key) {
        return controllers.get(key);
    }

}
