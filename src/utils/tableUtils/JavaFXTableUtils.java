package utils.tableUtils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JavaFXTableUtils {

    protected static <E> boolean hasTableFieldAnnotation(List<E> elements) {
        for (Field f : elements.get(0).getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(TableField.class)) {
                return true;
            }
        }
        return false;
    }

    protected static <E> List<Field> getTableFields(List<E> elements) {
        List<Field> tableFields = new ArrayList<>();
        for (Field f : elements.get(0).getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(TableField.class)) {
                tableFields.add(f);
            }
        }
        return tableFields;
    }

    protected static String capitalizeFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    public static <E> void toJavaFXTableView(List<E> elements, TableView<E> tableView) {
        List<TableColumn<E, String>> columns = new ArrayList<>();

        for (Field f : getTableFields(elements)) {
            TableColumn<E, String> column = new TableColumn<>(capitalizeFirstLetter(f.getName()));
            column.setCellValueFactory(new PropertyValueFactory<>(f.getName()));
            columns.add(column);
        }
        tableView.getColumns().clear();
        tableView.getColumns().addAll(columns);
        tableView.getItems().addAll(elements);
    }
}
