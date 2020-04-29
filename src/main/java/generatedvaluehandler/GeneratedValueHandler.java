package generatedvaluehandler;

import annotations.GeneratedValue;
import annotations.Id;
import annotations.Table;
import connectiontodb.ConnectionPoll;
import connectiontodb.DBConnection;
import enums.GenerationType;
import exception.NoPrimaryKeyException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public class GeneratedValueHandler {

    private static Map<String, Integer> primaryKeyMap = new HashMap();


    public static void main(String[] args) throws SQLException {

        Users u = new Users();
        GeneratedValueHandler generatedValueHandler = new GeneratedValueHandler();
        int k = generatedValueHandler.getId(u);
        System.out.println(k + " we got it");

    }

    public int getId(Object object) {
        String tableName = getTableName(object);
        String primaryKeyColumnName = getPrimaryKeyColumnName(object);
        GenerationType generationType = getGenerationType(object);

        if (checkInMap(object, tableName) != 0) {
            return checkInMap(object, tableName);
        }

        int generatedKey = getGeneratedValue(tableName, primaryKeyColumnName, generationType);
        primaryKeyMap.put(tableName, generatedKey);

        return generatedKey;
    }

    private int checkInMap(Object object, String tableName) {

        for (Map.Entry<String, Integer> m : primaryKeyMap.entrySet()) {
            if (m.getKey().equals(tableName)) {
                int result = m.getValue();
                m.setValue(result + 1);
                return result;
            }
        }
        return 0;
    }


    //check by Driver + Strategy
    private int getGeneratedValue(String tableName, String primaryKeyColumnName, GenerationType generationType) {

        if (GenerationType.IDENTITY.equals(generationType)) {
            if (DBConnection.getDRIVER().contains("postgresql")) {
                return getGeneratedValueIdentityPostgreSQL(tableName, primaryKeyColumnName);
            } else if (DBConnection.getDRIVER().contains("mysql")) {
                return getGeneratedValueSequenceMySQL(tableName, primaryKeyColumnName);
            }
        }
        if (GenerationType.SEQUENCE.equals(generationType)) {
            if (DBConnection.getDRIVER().contains("postgresql")) {
                return getGeneratedValueSequencePostgreSQL(tableName, primaryKeyColumnName);
            } else if (DBConnection.getDRIVER().contains("mysql")) {
                return getGeneratedValuesIdentityMySQL(tableName, primaryKeyColumnName);
            }
        }
        return 0;
    }


    ////////////////// methods for getID
    private String getTableName(Object object) {
        Table[] table = object.getClass().getAnnotationsByType(Table.class);
        return table[0].name();
    }


    private GenerationType getGenerationType(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field f : fields) {
            if (f.isAnnotationPresent(GeneratedValue.class)) {
                return f.getAnnotation(GeneratedValue.class).strategy();
            }
        }
        return GenerationType.SEQUENCE;
    }


    private String getPrimaryKeyColumnName(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                return f.getName();
            }
        }
        return null;
    }


//    private String getPrimaryKeyColumnName(String tableName) {
//        Connection connection = ConnectionPoll.getConnection();
//        Statement stmt = null;
//        String keyColumnName = null;
//        try {
//            stmt = connection.createStatement();
//
//            String sql = "select kcu.table_schema, kcu.table_name, tco.constraint_name, kcu.ordinal_position as position," +
//                    " kcu.column_name as key_column " +
//                    "from information_schema.table_constraints tco join information_schema.key_column_usage kcu " +
//                    "on kcu.constraint_name =   tco.constraint_name and kcu.constraint_schema = tco.constraint_schema " +
//                    "and kcu.constraint_name = tco.constraint_name where tco.constraint_type = 'PRIMARY KEY' " +
//                    "order by kcu.table_schema, kcu.table_name, position;";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            if (rs == null)
//                throw new NoPrimaryKeyException("There is no table with primary key found in " + DBConnection.getURL());
//
//            while (rs.next()) {
//                String rsTableName = rs.getString("table_name");
//                keyColumnName = rs.getString("key_column");
//                if (rsTableName.equals(tableName)) {
//                    return keyColumnName;
//                }
//            }
//
//        } catch (SQLException | NoPrimaryKeyException throwables) {
//            throwables.printStackTrace();
//        } finally {
//            ConnectionPoll.releaseConnection(connection);
//        }
//        return null;
//    }


    ///methods for getGeneratedValue method +
    private int getGeneratedValueSequencePostgreSQL(String tableName, String primaryKeyColumnName) {
        Connection connection = ConnectionPoll.getConnection();
        Statement stmt = null;
        int generatedKey = 0;
        try {
            stmt = connection.createStatement();
            String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName +
                    " ORDER BY " + primaryKeyColumnName + " DESC LIMIT 1;";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs == null)
                throw new NoPrimaryKeyException("There is no table with primary key found in " + DBConnection.getURL());

            while (rs.next()) {
                generatedKey = rs.getInt(primaryKeyColumnName);
                return generatedKey + 1;
            }
            ConnectionPoll.releaseConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();

        } catch (NoPrimaryKeyException e) {
            e.printStackTrace();
        } finally {
            ConnectionPoll.releaseConnection(connection);
        }
        return generatedKey;
    }

    //done
    private int getGeneratedValueIdentityPostgreSQL(String tableName, String primaryKeyColumnName) {
        Connection connection = ConnectionPoll.getConnection();
        Statement stmt = null;
        int generatedKey = 0;
        try {
            stmt = connection.createStatement();
            String sql = "INSERT INTO " + tableName + " DEFAULT VALUES RETURNING " + primaryKeyColumnName + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs == null)
                throw new NoPrimaryKeyException("There is no table with primary key found in " + DBConnection.getURL());

            while (rs.next()) {
                generatedKey = rs.getInt(primaryKeyColumnName);
                return generatedKey;
            }
            ConnectionPoll.releaseConnection(connection);

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        } catch (NoPrimaryKeyException e) {
            e.printStackTrace();
        } finally {
            ConnectionPoll.releaseConnection(connection);
        }
        return generatedKey;
    }

    ///done
    private int getGeneratedValueSequenceMySQL(String tableName, String primaryKeyColumnName) {
        Connection connection = ConnectionPoll.getConnection();
        Statement stmt = null;
        int generatedKey = 0;
        try {
            stmt = connection.createStatement();
            String sql = "SELECT " + primaryKeyColumnName + " FROM " + tableName +
                    " ORDER BY " + primaryKeyColumnName + " DESC LIMIT 1;";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs == null)
                throw new NoPrimaryKeyException("There is no table with primary key found in " + DBConnection.getURL());

            while (rs.next()) {
                generatedKey = rs.getInt(primaryKeyColumnName);
                return generatedKey + 1;
            }
            ConnectionPoll.releaseConnection(connection);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NoPrimaryKeyException e) {
            e.printStackTrace();
        } finally {
            ConnectionPoll.releaseConnection(connection);
        }
        return generatedKey;
    }

    ///select column_name from information_schema.columns where table_name = "users" and IS_NULLABLE="NO";
    //select column_name, DATA_TYPE from information_schema.columns where table_name = "users" and IS_NULLABLE="NO";

    private int getGeneratedValuesIdentityMySQL(String tableName, String primaryKeyColumnName) {
        Connection connection = ConnectionPoll.getConnection();
        Statement stmt = null;
        int generatedKey = 0;
        try {
            stmt = connection.createStatement();
            String sql1 = getNotNullColumnsQuery(tableName);

            ResultSet rs = stmt.executeQuery(sql1);
            String sql2 = parseDefaultString(rs, tableName, primaryKeyColumnName);

            stmt = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate(sql2);
            rs = stmt.executeQuery(" SELECT LAST_INSERT_ID();");

            while (rs.next()) {
                generatedKey = rs.getInt("LAST_INSERT_ID()");
                return generatedKey;
            }
            ConnectionPoll.releaseConnection(connection);

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        } finally {
            ConnectionPoll.releaseConnection(connection);
        }
        return generatedKey;
    }


    //got all columns that have IS_NULLABLE="NO";
    private String getNotNullColumnsQuery(String tableName) {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE FROM information_schema.columns WHERE table_name = \"" +
                tableName + "\" and IS_NULLABLE = \"NO\";";

        return sql;
    }

    // generate strings with is_nullable colums and values to insert
    private String parseDefaultString(ResultSet rs, String tableName, String primaryKeyColumnName) {
        Map<String, String> listOfColumns = new HashMap<>();
        try {
            while (rs.next()) {
                if (!(rs.getString("COLUMN_NAME").equals("CURRENT_CONNECTIONS") ||
                        rs.getString("COLUMN_NAME").equals("TOTAL_CONNECTIONS"))) {
                    listOfColumns.put(rs.getString("COLUMN_NAME"), rs.getString("DATA_TYPE"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String string = generateSQLWithNotNull(listOfColumns, tableName, primaryKeyColumnName);

        return string;
    }

    //generate insert query with is_nullable columns
    private String generateSQLWithNotNull(Map<String, String> listOfColumns, String tableName, String primaryKeyColumnName) {
        String columns = "";
        String values = "";

        for (Map.Entry<String, String> m : listOfColumns.entrySet()) {
            if (!m.getKey().equals(primaryKeyColumnName)) {
                columns += m.getKey() + ",";
                String mValue = m.getValue();
                if (mValue.contains("CHAR") || mValue.contains("BLOB") || mValue.contains("TEXT") ||
                        mValue.contains("ENUM") || mValue.contains("SET")) {
                    values += "\"\",";
                } else {
                    values += ("0,");
                }
            }
        }

        columns = columns.substring(0, columns.length() - 1);
        values = values.substring(0, values.length() - 1);

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ");";

        return sql;
    }


}
