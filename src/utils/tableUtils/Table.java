package utils.tableUtils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Table<V> {

    protected LinkedHashMap<String, List> table;

    private String formatVariableName(String name) {
        Pattern pattern = Pattern.compile("([a-z]+)([A-Z][a-z]+.*)");
        Matcher matcher = pattern.matcher(name);
        StringBuilder sb = new StringBuilder();
        if (matcher.matches()) {
            String group1 = matcher.group(1);
            sb.append(capitalizeFirstLetter(group1)).append(" ");
            return formatVariableName(matcher.group(2), sb);
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String formatVariableName(String name, StringBuilder sb) {
        Pattern pattern = Pattern.compile("([A-Z][a-z]+)([A-Z][a-z]+.*)");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            sb.append(matcher.group(1)).append(" ");
            return formatVariableName(matcher.group(2), sb);
        }
        sb.append(" ").append(name);
        return sb.toString().replaceAll(" {2}", " ");
    }

    public Table(LinkedHashMap<String, List> table) {
        this.table = table;
    }

    public Table(List<V> values) {
        table = new LinkedHashMap<>();
        List<Field> fields;
        if (hasTableFieldAnnotation(values)) {
            fields = getTableFields(values);
        } else {
            fields = Arrays.stream(values.get(0).getClass().getDeclaredFields()).collect(Collectors.toList());
        }
        for (Field f : fields) {
            addColumn(formatVariableName(f.getName()), getProperty(f, values));
        }
    }

    protected String getProperty(Field field, V value) {
        String v = "";
        try {
            v = field.get(value).toString();
        } catch (IllegalAccessException ex) {
            Method getterMethod = Arrays.stream(
                    value.getClass().getMethods())
                    .filter(
                            m -> m.getName().toLowerCase().equals(String.format("get%s", field.getName()).toLowerCase())
                    ).findFirst().orElse(null);
            if (getterMethod == null) {
                getterMethod = Arrays.stream(
                        value.getClass().getMethods())
                        .filter(
                                m -> m.getName().toLowerCase().equals(String.format("is%s", field.getName()).toLowerCase())
                        ).findFirst().orElse(null);
            }
            if (getterMethod != null) {
                try {
                    v = getterMethod.invoke(value).toString();
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return v;
    }

    protected List<String> getProperty(Field field, List<V> values) {
        List<String> result = new ArrayList<>();
        for (V value : values) {
            result.add(getProperty(field, value));
        }
        return result;
    }

    public Table() {
        this.table = new LinkedHashMap<>();
    }

    public void removeColumn(String name) {
        table.remove(name);
    }

    public void addColumn(String name, List data) {
        table.put(name, data);
    }

    protected String getTableNames(String separator) {
        StringBuilder sb = new StringBuilder();
        for (String s : table.keySet()) {
            sb.append(s).append(separator);
        }
        return sb.substring(0, sb.length() - 1);
    }

    protected int getLongestListSize() {
        int max = 0;
        for (String key : table.keySet()) {
            int size = table.get(key).size();
            if (size > max) max = size;
        }
        return max;
    }

    protected String getTableContents(String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getLongestListSize(); i++) {
            for (String key : table.keySet()) {
                try {
                    sb.append(table.get(key).get(i)).append(separator);
                } catch (Exception ex) {
                    sb.append("null").append(separator);
                }
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String separator = ";";
        StringBuilder sb = new StringBuilder();
        sb.append(getTableNames(separator)).append("\n");
        sb.append(getTableContents(separator));
        return sb.toString();
    }

    protected String htmlAttributes(Map<String, String> attributes) {
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
                    sb.append("<td>").append(table.get(key).get(i)).append("</td>");
                } catch (Exception ex) {
                    sb.append("<td>").append("null").append("</td>");
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
        if (!fileName.contains(".csv"))
            fileName += ".csv";
        File file = new File(fileName);
        if (!file.exists()) file.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(toString());
        writer.close();
    }


    protected int getRowsNumber() {
        String key = getKey(0);
        return table.get(key).size();
    }

    protected String getKey(int i) {
        int index = 0;
        for (String key : table.keySet()) {
            if (index == i) return key;
            i++;
        }
        return null;
    }


    protected <E> boolean hasTableFieldAnnotation(List<E> elements) {
        for (Field f : elements.get(0).getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(TableField.class)) {
                return true;
            }
        }
        return false;
    }

    protected <E> List<Field> getTableFields(List<E> elements) {
        List<Field> tableFields = new ArrayList<>();
        for (Field f : elements.get(0).getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(TableField.class)) {
                tableFields.add(f);
            }
        }
        return tableFields;
    }


    protected String capitalizeFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
