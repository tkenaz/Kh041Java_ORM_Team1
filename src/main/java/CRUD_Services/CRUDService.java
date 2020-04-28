package CRUD_Services;

import annotations.AutoIncremented;
import annotations.Entity;
import annotations.FieldName;
import annotations.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CRUDService {
    private Connection connection;
    private Class<?> entityClass;
    private String tableName;

    public CRUDService(Connection connection, Class<?> entityClass) {
        this.connection = connection;
        this.entityClass = entityClass;
        tableName = entityClass.getAnnotation(Entity.class).tableName();
    }

    void insert(Object...values){
        String columnName;
        Field[] fields = entityClass.getDeclaredFields();
        StringBuilder query = new StringBuilder("INSERT INTO ");
        StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
        query.append(tableName).append(" (");
        for(int i = 0; i < fields.length; i++){
            if(fields[i].getAnnotation(FieldName.class)!=null && fields[i].getAnnotation(AutoIncremented.class)==null){
                columnName = fields[i].getAnnotation(FieldName.class).name();
                query.append(" ").append(columnName);
                valuesForQuery.append(" '").append(values[i].toString()).append("'");
                if(i < fields.length - 1){
                    query.append(",");
                    valuesForQuery.append(",");
                }
            }
        }
        valuesForQuery.append(");");
        query.append(")").append(valuesForQuery);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());){
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    <T> void insert(T object) throws IllegalAccessException {
        if (object.getClass().equals(entityClass)){
            String columnName;
            Field[] fields = entityClass.getDeclaredFields();
            StringBuilder query = new StringBuilder("INSERT INTO ");
            StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
            query.append(tableName).append(" (");
            for(int i = 0; i < fields.length; i++){
                if(fields[i].isAnnotationPresent(FieldName.class) && !fields[i].isAnnotationPresent(AutoIncremented.class)){
                    columnName = fields[i].getAnnotation(FieldName.class).name();
                    query.append(" ").append(columnName);
                    if(!fields[i].isAccessible()){
                        fields[i].setAccessible(true);
                    }
                    valuesForQuery.append(" '").append(fields[i].get(object).toString()).append("'");
                    if(i < fields.length - 1){
                        query.append(",");
                        valuesForQuery.append(",");
                    }
                }
            }
            valuesForQuery.append(");");
            query.append(")").append(valuesForQuery);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());){
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else throw new IllegalAccessException();
    }
    void update(HashMap<String, String> columnsAndValuesToUpdate,String conditionalColumnName, String conditionValue){
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET");
        for(Map.Entry<String, String> pair : columnsAndValuesToUpdate.entrySet()){
            query.append(" ").append(pair.getKey()).append(" = ").append(pair.getValue()).append(",");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ").append(conditionValue).append(";");
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString());){
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void deleteById(int idToDelete){
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE id = ?;");
        try(PreparedStatement delete = connection.prepareStatement(query.toString());) {
            delete.setInt(1, idToDelete);
            delete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void deleteByCondition(String conditionalColumnName, String conditionValue){
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ").append(conditionalColumnName).append(" = ");
        query.append("'").append(conditionValue).append("';");
        try(PreparedStatement delete = connection.prepareStatement(query.toString());) {
            delete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    ResultSet selectAll(){
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(";");
        ResultSet resultSet = null;
        try(PreparedStatement select = connection.prepareStatement(query.append(tableName).toString());) {
            resultSet = select.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    ResultSet selectAllWithOrder(String columnNameToOrderBy, boolean naturalOrder){
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(" ORDER BY ").append(columnNameToOrderBy);
        if(!naturalOrder){
            query.append(" DESC;");
        }
        else {
            query.append(";");
        }
        ResultSet resultSet = null;
        try(PreparedStatement select = connection.prepareStatement(query.toString());) {
            resultSet = select.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    ResultSet selectById(int id) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append("; WHERE id = ?;");
        ResultSet resultSet = null;
        try (PreparedStatement select = connection.prepareStatement(query.append(tableName).toString());) {
            select.setInt(1, id);
            resultSet = select.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

}
