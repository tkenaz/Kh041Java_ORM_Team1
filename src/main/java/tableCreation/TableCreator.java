package tableCreation;

import annotations.ForeignKey;
import annotations.Id;
import annotations.ManyToOne;
import annotations.NotNull;
import annotations.OneToMany;
import annotations.OneToOne;
import annotations.Table;
import annotations.Varchar;
import connectiontodb.ConnectionPoll;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TableCreator {
    static final Map<Class, String> mapping = new HashMap<>();
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS %s";
    private static final String CREATE_FOREGN_KEY_SQL = "ALTER TABLE %s";
    private static final String DROP_TABLE_SQL = "DROP TABLE %s";
    private static final String NOT_NULL = "NOT NULL";

    public TableCreator(Connection connection) {
        mapping.put(int.class, "INT");
        mapping.put(long.class, "LONG");
        mapping.put(float.class, "FLOAT");
        mapping.put(double.class, "DOUBLE");
        mapping.put(String.class, "VARCHAR");
        mapping.put(Date.class, "DATE");
        mapping.put(BigDecimal.class, "DECIMAL");
    }

    public void createTable(Class singleClass) throws SQLException {
        Connection connection = ConnectionPoll.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = createTableQuery(singleClass, mapping, CREATE_TABLE_SQL);

        statement.executeUpdate(sqlQuery);
    }

    public <T> String createTableQuery(Class<T> singleClass, Map<Class, String> typeMap, String queryBase) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = singleClass.getDeclaredFields();
        stringBuilder.append(String.format(queryBase, singleClass.getAnnotation(Table.class).name()));
        stringBuilder.append(" (");

        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ForeignKey.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Id.class)) {
                stringBuilder.append(String.join(
                        " ",
                        field.getName(),
                        typeMap.get(field.getType()),
                        NOT_NULL,
                        "PRIMARY KEY"));
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

    public void createForeignKey(Class singleClass) throws SQLException {
        Connection connection = ConnectionPoll.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = createForeignKeyQuery(singleClass, mapping, CREATE_FOREGN_KEY_SQL);

        statement.executeUpdate(sqlQuery);
    }

    public <T> String createForeignKeyQuery(Class<T> singleClass, Map<Class, String> typeMap, String queryBase) {

        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = singleClass.getDeclaredFields();
        stringBuilder.append(String.format(queryBase, singleClass.getAnnotation(Table.class).name()));//ToDo by MV: check this

        stringBuilder.append(" ADD COLUMN ");

        for (Field field : fields) {

            if (field.isAnnotationPresent(ForeignKey.class)) {
                stringBuilder.append(field.getAnnotation(ForeignKey.class).name())
                        .append(" INT")
                        .append(',')
                        .append(' ');
            }

            if (field.isAnnotationPresent(OneToOne.class) ||
                    field.isAnnotationPresent(ManyToOne.class)) {

                stringBuilder.append(" ADD FOREIGN KEY (")
                        .append(field.getName())
                        .append(") REFERENCES ")
                        .append(field.getAnnotation(ManyToOne.class).mappedBy())
                        .append(" (id)");

            }
        }
        stringBuilder.append(";");
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public <T> void dropTable(Class<T> singleClass) throws SQLException {
        Connection connection = ConnectionPoll.getConnection();
        Statement statement = connection.createStatement();
        String sqlQuery = String.format(DROP_TABLE_SQL, singleClass.getAnnotation(Table.class).name());
        statement.executeUpdate(sqlQuery);
        System.out.println("The table " + singleClass.getAnnotation(Table.class).name() + " has been dropped.");
    }

    /*ToDo by MV: these are planned improvments if I am lucky enough to have spare time*/
    /*

    private static String sourcePackagePath = "main.java";
    List<Class> classes = new LinkedList<>();

    public List<Class> instantiateEntityClass(String sourcePackage) {
        try {
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

                        if (classObject.isAnnotationPresent(Table.class)) {
                            classes.add(classObject);
                            System.out.println(classObject.getName());
                        }
                    } else instantiateEntityClass(String.join(".", sourcePackage, fileName));
                }
            }

        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void createTable() throws SQLException {
        TableCreator tableCreator = new TableCreator(ConnectionPoll.getConnection());
        List<Class> classes = tableCreator.instantiateEntityClass(sourcePackagePath);
        for (Class singleClass : classes) {
            System.out.println(singleClass.getName());
            tableCreator.createTable(singleClass);
        }
    }

    public static void createTables() throws SQLException {
        System.out.println("Scanning using Reflections:");
        TableCreator tableCreator = new TableCreator(ConnectionPoll.getConnection());
        List<Class> classes = tableCreator.instantiateEntityClass(sourcePackagePath);
        for (Class aClass : classes) {
            System.out.println(aClass.getName());
            tableCreator.createTable(aClass);
        }
    }

    public void dropAllTables() {
    }*/

}
