package updating;

import controllers.Controller;

import java.util.*;

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
        controllers.put(key, controller);
    }


    public Controller get(String key) {
        return controllers.get(key);
    }



}
