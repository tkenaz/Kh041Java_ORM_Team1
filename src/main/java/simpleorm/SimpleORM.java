package simpleorm;

import annotations.Table;
import connectiontodb.ConnectionPoll;
import connectiontodb.DBConnection;
import crud_services.CRUDService;
import crud_services.SimpleORMInterface;
import relationannotation.ProcessOneToMany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SimpleORM {


//save for users
    public void save(Object object) {

        System.out.println("If table Exists: " + ifTableExists(object));
// waiting for Marina's script
//        if (!ifTableExists(object)) {
//            call to create table
//        }
 //       Как сохранять обьект
        saveObject(object);
        ProcessOneToMany.saveOneToMany(object);
        //ManyToOne...

    }



    private void saveObject(Object object) {
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, object.getClass());

        try {
            crudService.insert((SimpleORMInterface) object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        ConnectionPoll.releaseConnection(connection);

        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



 //Update for users
    public void update(Object object) {

        System.out.println("If table Exists: " + ifTableExists(object));
// waiting for Marina's script
//        if (!ifTableExists(object)) {
//            call to create table
//        }
        updateObject(object);
        ProcessOneToMany.updateOneToMany(object);
        //ManyToOne...

    }


    private void updateObject(Object object) {
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, object.getClass());

        try {
            crudService.update((SimpleORMInterface) object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ConnectionPoll.releaseConnection(connection);

        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private boolean ifTableExists(Object object) {
        String tableName = object.getClass().getAnnotation(Table.class).name();
        Connection connection = ConnectionPoll.getConnection();
        ResultSet resultSet;

            StringBuilder sql = new StringBuilder("SELECT TABLE_NAME FROM information_schema.tables");
            sql.append(" WHERE table_schema = ? AND table_name = ? LIMIT 1;");

            //System.out.println(sql);
            try (PreparedStatement checkTable = connection.prepareStatement(sql.toString())) {
                checkTable.setString(1, DBConnection.getDBName());
                checkTable.setString(2, tableName);
                resultSet = checkTable.executeQuery();

                while (resultSet.next()) {
                    return resultSet.getString("TABLE_NAME").equals(tableName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return false;
    }

    public ResultSet selectAll(Class clazz){
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, clazz.getClass());

        ResultSet resultSet =  crudService.selectAll();

//        ConnectionPoll.releaseConnection(connection);
//
//        try {
//            connection.close();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        return resultSet;
    }


    public ResultSet select(Object object){
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, object.getClass());

        ResultSet resultSet = crudService.selectById((SimpleORMInterface) object);

//        ConnectionPoll.releaseConnection(connection);
//
//        try {
//            connection.close();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        return resultSet;
    }



}



