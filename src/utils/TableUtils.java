package utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableUtils {
    public class Table {

        private LinkedHashMap<String, List> table;

        public Table(LinkedHashMap<String, List> table) {
            this.table = table;
        }

        public Table(List objects, Class cls) {
            table = new LinkedHashMap<>();
            for (Field f : cls.getFields()) {
                addColumn(f.getName(), getProperty(f.getName(), objects, cls));
            }
        }

        private List<String> getProperty(String property, List objects, Class cls) {
            List<String> result = new ArrayList<>();
            for (Object object : objects) {
                try {
                    String value = cls.getField(property).get(object).toString();
                    result.add(value);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        public Table() {
            this.table = new LinkedHashMap<>();
        }

        public void addColumn(String name, List data) {
            table.put(name, data);
        }

        private String getTableNames() {
            StringBuilder sb = new StringBuilder();
            for (String s : table.keySet()) {
                sb.append(s).append(",");
            }
            return sb.substring(0, sb.length() - 1);
        }

        private int getLongestListSize() {
            int max = 0;
            for (String key : table.keySet()) {
                int size = table.get(key).size();
                if (size > max) max = size;
            }
            return max;
        }

        private String getTableContents() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getLongestListSize(); i++) {
                for (String key : table.keySet()) {
                    try {
                        sb.append(table.get(key).get(i)).append(",");
                    } catch (Exception ex) {
                        sb.append("null").append(",");
                    }
                }
                sb.setLength(sb.length() - 1);
                sb.append("\n");
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getTableNames()).append("\n");
            sb.append(getTableContents());
            return sb.toString();
        }

        private String htmlAttributes(Map<String, String> attributes) {
            StringBuilder sb = new StringBuilder();
            for (String key : attributes.keySet()) {
                sb.append(" ").append(key).append("=").append("\"").append(attributes.get(key)).append("\" ");
            }
            return sb.toString();
        }


        public String toHTML(Map<String, String> attributes) {
            StringBuilder sb = new StringBuilder();
            sb.append("<table");
            sb.append(htmlAttributes(attributes));
            sb.append(">\n");
            sb.append("<tr>\n");
            for (String key : this.table.keySet()) {
                sb.append("<th>").append(key).append("</th>\n");
            }
            sb.append("</tr>\n");
            for (int i = 0; i < getLongestListSize(); i++) {
                sb.append("<tr>\n");
                for (String key : table.keySet()) {
                    try {
                        sb.append("<th>").append(table.get(key).get(i)).append("</th>");
                    } catch (Exception ex) {
                        sb.append("<th>").append("null").append("</th>");
                    }
                    sb.append("\n");
                }
                sb.append("</tr>");
                sb.append("\n");
            }
            sb.append("</table>");
            return sb.toString();
        }

        public void toCSV(String fileName) throws IOException {
            File file = new File(fileName);
            if (!file.exists()) file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(toString());
            writer.close();
        }

        public void toJavaFXTableView(TableView<List<StringProperty>> tableView) {
            List<String> keys = new ArrayList<>(table.keySet());
            List<TableColumn<List<StringProperty>, String>> columns = new ArrayList<>();
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                columns.add(new TableColumn<>(key));
            }

            for (int i = 0; i < columns.size(); i++) {
                int finalI = i;
                columns.get(i).setCellValueFactory(data -> data.getValue().get(finalI));
            }
            tableView.getColumns().setAll(columns);
            tableView.setItems(getData());
        }


        private ObservableList<List<StringProperty>> getData() {
            ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();

            for (int i = 0; i < getRowsNumber(); i++) {
                data.add(getRow(i));
            }
            return data;
        }

        private int getRowsNumber() {
            String key = table.keySet().iterator().next();
            return table.get(key).size();
        }

        private List<StringProperty> getRow(int i) {
            List<StringProperty> row = new ArrayList<>();
            for (String key : table.keySet()) {
                row.add(new SimpleStringProperty(table.get(key).get(i).toString()));
            }
            return row;
        }


    }

    public static <E> void toJavaFXTableView(List<E> elements, TableView<E> tableView) {
        List<TableColumn<E, String>> columns = new ArrayList<>();
        List<Field> tableFields = new ArrayList<>();
        for (Field f : elements.get(0).getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(TableField.class)) {
                tableFields.add(f);
            }
        }
        for (Field f : tableFields) {
            TableColumn<E, String> column = new TableColumn<>(firstLetterUppercase(f.getName()));
            column.setCellValueFactory(new PropertyValueFactory<>(f.getName()));
            columns.add(column);
        }
        tableView.getColumns().clear();
        tableView.getColumns().addAll(columns);
        tableView.getItems().addAll(elements);
    }

    private static String firstLetterUppercase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
