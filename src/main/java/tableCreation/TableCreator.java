package tableCreation;

import annotations.Entity;
import annotations.Id;
import annotations.JoinColumn;
import annotations.ManyToOne;
import annotations.NotNull;
import annotations.OneToMany;
import annotations.OneToOne;
import annotations.Varchar;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableCreator {
    static final Map<Class, String> mapping = new HashMap<>();
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS %s";
    private static final String NOT_NULL = "NOT NULL";

    List<Class> classes = new LinkedList<>();
    private DataSource dataSource;

    public TableCreator(DataSource dataSource) {
        this.dataSource = dataSource;

        mapping.put(int.class, "INT");
        mapping.put(long.class, "LONG");
        mapping.put(float.class, "FLOAT");
        mapping.put(double.class, "DOUBLE");
        mapping.put(String.class, "VARCHAR");
        mapping.put(Date.class, "DATE");
        mapping.put(BigDecimal.class, "DECIMAL");
    }

    public static void createTable() throws SQLException {
        TableCreator tableCreator = new TableCreator(ConnectionPoll.getConnection());//ToDo by MV: import class
        List<Class> classes = tableCreator.instantiate(sourcePackage);//ToDo by MV: insert path to Entity package
        for (Class singleClass : classes) {
            System.out.println(singleClass.getName());
            tableCreator.createTable(singleClass);
        }
    }

    public List<Class> instantiate(String sourcePackage) {
        try{
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();

            String path = sourcePackage.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.toURI());

                for (File classFile : file.listFiles()) {
                    String fileName = classFile.getName();
                    System.out.println(fileName);
                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(0, fileName.lastIndexOf("."));

                        Class classObject = Class.forName(sourcePackage + "." + className);

                        if (classObject.isAnnotationPresent(Entity.class)) {
                            classes.add(classObject);
                            System.out.println(classObject.getName());
                        }
                    } else instantiate(String.join(".", sourcePackage, fileName));
                }
            }

        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public void createTable(Class singleClass) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = createQuery(singleClass, mapping, CREATE_TABLE_SQL);

        statement.executeUpdate(sqlQuery);
    }

    public <T> String createQuery(Class<T> singleClass, Map<Class, String> typeMap, String queryBase) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = singleClass.getDeclaredFields();
        stringBuilder.append(String.format(queryBase, singleClass.getAnnotation(Entity.class).tableName()));
        stringBuilder.append(" (");

        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Id.class)){
                stringBuilder.append(String.join(
                        " ",
                        field.getName(),
                        typeMap.get(field.getType()),
                        NOT_NULL,
                        "PRIMARY KEY"));
                if (field.getAnnotation(Id.class).isAutoIncremented()) {
                    stringBuilder.append(' ').append("AUTO_INCREMENT");
                }
            } else if (field.isAnnotationPresent(OneToOne.class) ||
            field.isAnnotationPresent(ManyToOne.class)) {
                stringBuilder.append(String.join(
                        " ",
                        field.getAnnotation(JoinColumn.class).name(),
                        typeMap.get(long.class),
                        NOT_NULL));
            } else {
                stringBuilder.append(String.join(
                        " ",
                        field.getName(),
                        typeMap.get(field.getType())));
                if (field.isAnnotationPresent(Varchar.class)) {
                    stringBuilder.append(' ')
                            .append('(')
                            .append(field.getAnnotation(Varchar.class).size())
                            .append(')');
                }
                if (field.isAnnotationPresent(NotNull.class)) {
                    stringBuilder.append(' ')
                            .append(NOT_NULL);
                }
            }
            stringBuilder.append(',');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(");");
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
