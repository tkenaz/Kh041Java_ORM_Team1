package CRUD_Services;

import annotations.*;
import enums.GenerationType;
import generatedvaluehandler.GeneratedValueHandler;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CRUDService {
    private Connection connection;
    private Class<? extends SimpleORMInterface> entityClass;
    private String tableName;

    public CRUDService(Connection connection, Class<? extends SimpleORMInterface> entityClass) {
        this.connection = connection;
        this.entityClass = entityClass;
        tableName = entityClass.getAnnotation(Table.class).name();
    }

    void insert(Object... values) {
        String columnName;
        Field[] fields = entityClass.getDeclaredFields();
        StringBuilder query = new StringBuilder("INSERT INTO ");
        StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
        query.append(tableName).append(" (");
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getAnnotation(Table.class) != null && fields[i].getAnnotation(Id.class) == null) {
                columnName = fields[i].getAnnotation(Column.class).name();
                query.append(" ").append(columnName);
                valuesForQuery.append(" '").append(values[i].toString()).append("'");
                if (i < fields.length - 1) {
                    query.append(",");
                    valuesForQuery.append(",");
                }
            }
            if (i == values.length - 1) break;
        }
        valuesForQuery.append(");");
        query.append(")").append(valuesForQuery);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(SimpleORMInterface object) throws IllegalAccessException {
        String query = QuerySamples.forUpdate(object);
        System.out.println(query);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(SimpleORMInterface object) throws IllegalAccessException, NoSuchFieldException {
        if (object.getClass().isAnnotationPresent(Table.class)) {
            int generatedId = new GeneratedValueHandler().getId(object);
            object.setId(generatedId);
            if (object.getClass().getDeclaredField("id").getAnnotation(Id.class).strategy() == GenerationType.IDENTITY) {
                update(object);
            } else if (object.getClass().getDeclaredField("id").getAnnotation(Id.class).strategy() == GenerationType.SEQUENCE) {
                String query = QuerySamples.forInsert(object);
                System.out.println(query);
                try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else throw new IllegalAccessException();
    }

    public void update(HashMap<String, Object> columnsAndValuesToUpdate, String conditionalColumnName, Object conditionValue) {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET");
        for (Map.Entry<String, Object> pair : columnsAndValuesToUpdate.entrySet()) {
            query.append(" ").append(pair.getKey()).append(" = ");
            if (pair.getValue() instanceof Number) {
                query.append(pair.getValue()).append(",");
            } else {
                query.append("'").append(pair.getValue()).append("'").append(",");
            }
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ");
        if (conditionValue instanceof Number) {
            query.append(conditionValue).append(";");
        } else {
            query.append("'").append(conditionValue).append("'").append(";");
        }
        System.out.println(query);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(int idToDelete) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE id = ?;");
        try (PreparedStatement delete = connection.prepareStatement(query.toString());) {
            delete.setInt(1, idToDelete);
            int count = delete.executeUpdate();
            if (count == 1) {
                System.out.println("1 row affected");
            } else {
                System.out.println("No row with such id found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByCondition(String conditionalColumnName, Object conditionValue) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ");
        if (conditionValue instanceof Number) {
            query.append(conditionValue).append(";");
        } else {
            query.append("'").append(conditionValue).append("';");
        }
        try (PreparedStatement delete = connection.prepareStatement(query.toString());) {
            System.out.println(query);
            int count = delete.executeUpdate();
            if (count > 0) {
                System.out.println(count + " rows affected");
            } else if (count == 1) {
                System.out.println("1 row affected");
            } else {
                System.out.println("No rows with such value found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SimpleORMInterface> selectByCondition(String conditionalColumnName, Object conditionValue) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ");
        if (conditionValue instanceof Number) {
            query.append(conditionValue).append(";");
        } else {
            query.append("'").append(conditionValue).append("';");
        }
        ResultSet resultSet = null;
        try (PreparedStatement select = connection.prepareStatement(query.toString());) {
            resultSet = select.executeQuery();
            return resultSetProcessing(resultSet);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<SimpleORMInterface> selectAll() {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(";");
        ResultSet resultSet = null;
        System.out.println(query);
        try (PreparedStatement select = connection.prepareStatement(query.toString());) {
            resultSet = select.executeQuery();
            return resultSetProcessing(resultSet);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<SimpleORMInterface> selectAllWithOrder(String columnNameToOrderBy, boolean naturalOrder) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(" ORDER BY ").append(columnNameToOrderBy);
        if (!naturalOrder) {
            query.append(" DESC;");
        } else {
            query.append(";");
        }
        System.out.println(query);
        ResultSet resultSet = null;
        try (PreparedStatement select = connection.prepareStatement(query.toString());) {
            resultSet = select.executeQuery();
            return resultSetProcessing(resultSet);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<SimpleORMInterface> selectById(SimpleORMInterface object) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(" WHERE id = ?;");
        ResultSet resultSet = null;
        System.out.println("SELECT * FROM users WHERE id = " + object.getId() + ";");
        try (PreparedStatement select = connection.prepareStatement(query.toString());) {
            select.setInt(1, object.getId());
            resultSet = select.executeQuery();
            return resultSetProcessing(resultSet);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<SimpleORMInterface> selectById(int id) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(" WHERE id = ").append(id + ";");
        ResultSet resultSet = null;
        System.out.println(query);
        try (PreparedStatement select = connection.prepareStatement(query.toString());) {
            resultSet = select.executeQuery();
            return resultSetProcessing(resultSet);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<SimpleORMInterface> resultSetProcessing(ResultSet rs)
            throws SQLException, IllegalAccessException, InstantiationException {
        ArrayList<SimpleORMInterface> list = new ArrayList<>();
        SimpleORMInterface object;
        while (rs.next()) {
            System.out.println(1);
            object = entityClass.newInstance();
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    field.set(object, rs.getObject(field.getAnnotation(Column.class).name()));
                }
                else if(field.isAnnotationPresent(ManyToOne.class)) {
                    field.set(object, rs.getObject(field.getAnnotation(Column.class).name()));
                }
                field.setAccessible(false);
            }
            list.add(object);
        }
        return list;
    }

}
