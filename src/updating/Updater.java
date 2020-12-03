package updating;

import controllers.Controller;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class Updater {
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private static Iterable<Class> getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            URI uri = new URI(resource.toString());
            dirs.add(new File(uri.getPath()));
        }
        List<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    public static void update(Class updateRequester) throws ClassNotFoundException, IOException, URISyntaxException {
        List<Method> methods;
        for (Class c : getClasses("controllers")) {
            methods = (Arrays.stream(c.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(OnUpdate.class) && arrayContains(m.getAnnotation(OnUpdate.class).updatedBy(), updateRequester)).collect(Collectors.toList()));
            methods.forEach(m -> {
                Platform.runLater(() -> {
                    ControllerAccess.getInstance().forEach(c.getName(), (controller) -> {
                        try {
                            m.invoke(controller);
                        } catch (IllegalAccessException | InvocationTargetException ignored) {
                        }
                    });
                });

            });
        }
    }

    private static boolean arrayContains(Class[] array, Class c) {
        for (Class ci : array) {
            if (ci.equals(c)) return true;
        }
        return false;
    }
}
