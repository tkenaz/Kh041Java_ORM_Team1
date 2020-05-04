package simpleorm;

import annotations.Table;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import connectiontodb.ConnectionPoll;
import connectiontodb.DBConnection;
import crud_services.CRUDService;
import crud_services.SimpleORMInterface;
import relationannotation.ProcessOneToMany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleORM {

    private static List<String> existingTables = new ArrayList<>();

    /**
     * Save external
     *
     * @param object
     */

    public void save(Object object) {
// waiting for Marina's script
        if (!ifTableExists(object)) {
//            call to create table

        }
        saveObject(object);
        ProcessOneToMany.saveOneToMany(object); //Roma update many-to-one insert
        //ManyToOne...

    }


    /**
     * Update external
     *
     * @param object
     */

    public void update(Object object) {
// waiting for Marina's script
        if (!ifTableExists(object)) {
//            call to create table

        }
        updateObject(object);
        ProcessOneToMany.updateOneToMany(object); //Roma update many-to-one insert
        //ManyToOne...

    }


    public Object selectById(int id, Class clazz) {
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, clazz);
        ConnectionPoll.releaseConnection(connection);

        return crudService.selectById(id, clazz);
    }


    public List<Object> selectAll(Class clazz) {
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, clazz);
        ConnectionPoll.releaseConnection(connection);
        return crudService.selectAll(clazz);
    }


    ///INTERNAL METHODS

    private boolean ifTableExists(Object object) {
        if (existingTables.size() > 0) {
            String tableName = object.getClass().getAnnotation(Table.class).name();
            System.out.println(tableName + " tablename that we got");
            for (String s : existingTables) {
                if (tableName.equals(s))
                    return true;
            }
        }
        return ifTableExistsRequestToTable(object);
    }


    private boolean ifTableExistsRequestToTable(Object object) {
        String tableName = object.getClass().getAnnotation(Table.class).name();
        Connection connection = ConnectionPoll.getConnection();
        ResultSet resultSet;

        StringBuilder sql = new StringBuilder("SELECT TABLE_NAME FROM information_schema.tables");
        sql.append(" WHERE table_schema = ? AND table_name = ? LIMIT 1;");

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

    // update internal object
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

    //internal method to save received object in save()
    private void saveObject(Object object) {
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection, object.getClass());
        try {
            if (!connection.isClosed()) {

                try {
                    crudService.insert((SimpleORMInterface) object);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }

                ConnectionPoll.releaseConnection(connection);

                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("fail");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



