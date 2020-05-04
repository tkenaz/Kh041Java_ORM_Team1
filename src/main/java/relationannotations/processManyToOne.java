package relationannotations;

import CRUD_Services.CRUDService;
import CRUD_Services.OurORM;
import annotations.*;
import connectiontodb.ConnectionPoll;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class processManyToOne {

    private String getFKColumnName(Class<?> withFieldManyToOne){
        String result = null;
        Field[] fields = withFieldManyToOne.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(field.isAnnotationPresent(ManyToOne.class)){
                result = field.getAnnotation(JoinColumn.class).name();
                field.setAccessible(false);
                break;
            }
            field.setAccessible(false);
        }
        return result;
    }

    private void setUpCollectionOfObjects(OurORM object, List<OurORM> list) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(field.isAnnotationPresent(OneToMany.class)){
                field.set(object, list);
                field.setAccessible(false);
                break;
            }
            field.setAccessible(false);
        }
    }

    void selectByFK(Class<? extends OurORM> withFieldManyToOne, OurORM fk) throws SQLException, IllegalAccessException, InstantiationException {
        Connection connection = ConnectionPoll.getConnection();
        String tableName = withFieldManyToOne.getAnnotation(Table.class).name();
        CRUDService crudService = new CRUDService(connection, withFieldManyToOne);
        ResultSet resultSet = crudService.selectByCondition(getFKColumnName(withFieldManyToOne), fk.getId());
        List<OurORM> list = new ArrayList<>();
        OurORM object;
        while(resultSet.next()){
            object = withFieldManyToOne.newInstance();
            Field[] fields = withFieldManyToOne.getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                if(field.isAnnotationPresent(Column.class)){
                    field.set(object, resultSet.getObject(field.getAnnotation(Column.class).name()));
                }
                field.setAccessible(false);
            }
            list.add(object);
        }
        setUpCollectionOfObjects(fk, list);
        ConnectionPoll.releaseConnection(connection);
    }
}
