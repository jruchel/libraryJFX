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

    public Map<String, Controller> controllers;

    public void put(String key, Controller controller) {
        controllers.putIfAbsent(key, controller);
    }

    public void replace(String key, Controller controller) {
        if (controllers.keySet().contains(key))
            controllers.replace(key, controller);
        else
            put(key, controller);
    }

    public Controller get(String key) {
        return controllers.get(key);
    }

}
