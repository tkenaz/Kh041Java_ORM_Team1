package crud_services;

import annotations.Id;
import annotations.Table;
import connectiontodb.ConnectionPoll;
import enums.GenerationType;
import generatedvaluehandler.GeneratedValueHandler;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CRUDService {
    private Connection connection;
    private Class<?> entityClass;
    private String tableName;

    public CRUDService(Connection connection, Class<?> entityClass) {
        this.connection = connection;
        this.entityClass = entityClass;
        tableName = entityClass.getAnnotation(Table.class).name();
    }
/*
    public void insert(Object object) {
        String columnName;
        Field[] fields = entityClass.getDeclaredFields();
        StringBuilder query = new StringBuilder("INSERT INTO ");
        StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
        query.append(tableName).append(" (");
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getAnnotation(Column.class) != null) {
                columnName = fields[i].getAnnotation(Column.class).name();
                    query.append(columnName).append(",");
                    //fields[i].get(object);
                try {
                    fields[i].setAccessible(true);
                    valuesForQuery.append("'").append(fields[i].get(object)).append("',");

                    fields[i].setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        String v = valuesForQuery.toString().substring(0, valuesForQuery.length()-1);
        String q = query.toString().substring(0, query.length()-1);
        String sql = q + ") " + v + ") ;";

       // String sql = QuerySamples.forInsert(object);
        try  {
            Statement statement = connection.prepareStatement(sql);
            System.out.println(sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */


//    public void insert(OurORM object) {
//        String sql = null;
//        try {
//            sql = QuerySamples.forInsert(object);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        // String sql = QuerySamples.forInsert(object);
//        try  {
//            Statement statement = connection.prepareStatement(sql);
//            System.out.println(sql);
//            statement.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public void update(SimpleORMInterface object) throws IllegalAccessException {
        String query = QuerySamples.forUpdate(object);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            System.out.println(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insert(SimpleORMInterface object) throws IllegalAccessException, NoSuchFieldException {
       //Connection c = ConnectionPoll.getConnection();
        if (object.getClass().isAnnotationPresent(Table.class)) {
            int generatedId = new GeneratedValueHandler().getId(object);
            object.setId(generatedId);
            if (object.getClass().getDeclaredField("id").getAnnotation(Id.class).strategy()
                    == GenerationType.IDENTITY) {
                update(object);
            } else if (object.getClass().getDeclaredField("id").getAnnotation(Id.class).strategy()
                    == GenerationType.SEQUENCE) {
                String query = QuerySamples.forInsert(object);

                try  {

                    Statement statement = connection.createStatement();// = connection.prepareStatement(query);
                    statement.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else throw new IllegalAccessException();
//        ConnectionPoll.releaseConnection(c);
//        try {
//            c.close();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }


    void update(HashMap<String, String> columnsAndValuesToUpdate, String conditionalColumnName, String conditionValue) {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET");
        for (Map.Entry<String, String> pair : columnsAndValuesToUpdate.entrySet()) {
            query.append(" ").append(pair.getKey()).append(" = ").append(pair.getValue()).append(",");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ").append(conditionValue).append(";");
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void delete(int idToDelete) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE id = ?;");
        try (PreparedStatement delete = connection.prepareStatement(query.toString());) {
            delete.setInt(1, idToDelete);
            delete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void delete(String conditionalColumnName, String conditionValue) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ");
        query.append("'").append(conditionValue).append("';");
        try (PreparedStatement delete = connection.prepareStatement(query.toString());) {
            delete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectAll() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(";");
        ResultSet resultSet = null;
        try (PreparedStatement select = connection.prepareStatement(query.append(tableName).toString());) {
            resultSet = select.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet selectAllWithOrder(String columnNameToOrderBy, boolean naturalOrder) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(" ORDER BY ").append(columnNameToOrderBy);
        if (!naturalOrder) {
            query.append(" DESC;");
        } else {
            query.append(";");
        }
        ResultSet resultSet = null;
        try (PreparedStatement select = connection.prepareStatement(query.toString());) {
            resultSet = select.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet selectById(SimpleORMInterface object) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append("; WHERE id = ?;");
        ResultSet resultSet = null;
        try (PreparedStatement select = connection.prepareStatement(query.append(tableName).toString());) {
            select.setInt(1, object.getId());
            resultSet = select.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

}
