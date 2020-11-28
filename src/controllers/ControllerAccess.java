package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ControllerAccess {

    public static ControllerAccess instance;

    public static ControllerAccess getInstance() {
        if (instance == null) instance = new ControllerAccess();
        return instance;
    }

    private ControllerAccess() {
        this.controllers = new HashMap<>();
    }

    public Map<String, List<Controller>> controllers;

    public void put(String key, Controller controller) {
        List<Controller> controllersList = new ArrayList<>();
        controllersList.add(controller);
        controllers.putIfAbsent(key, controllersList);
    }

    public void add(String key, Controller controller) {
        if (!controllers.containsKey(key))
            controllers.put(key, new ArrayList<>());
        if (!controllers.get(key).contains(controller))
            controllers.get(key).add(controller);
    }

    public List<Controller> get(String key) {
        return controllers.get(key);
    }

    public void forEach(String key, Consumer<Controller> consumer) {
        if (controllers.containsKey(key))
            controllers.get(key).forEach(consumer);
    }

    public Controller get(String key, int i) {
        return controllers.get(key).get(i);
    }

}
