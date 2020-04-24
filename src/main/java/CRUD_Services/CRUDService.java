package CRUD_Services;

import annotations.Entity;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRUDService {
    private Connection connection;
    private Class<?> entityClass;
    private static final String SELECT_ALL = "SELECT * FROM ?";

    public CRUDService(Connection connection, Class<?> entityClass) {
        this.connection = connection;
        this.entityClass = entityClass;
    }
    private String getTableName(){
       return entityClass.getAnnotation(Entity.class).tableName();
    }
    ResultSet selectAll(){
        PreparedStatement select = null;
        ResultSet resultSet = null;
        try {
            select = connection.prepareStatement(SELECT_ALL);
            select.setString(1, getTableName());
            resultSet = select.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

}
