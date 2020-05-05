package relationannotation;

import annotations.*;
import connectiontodb.ConnectionPoll;
import crud_services.CRUDService;
import crud_services.SimpleORMInterface;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class processManyToOne {

    private static String getFKColumnName(Class<?> withFieldManyToOne){
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

    private static void setUpCollectionOfObjects(SimpleORMInterface object, List<SimpleORMInterface> list) throws IllegalAccessException {
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


    //??
    public static void selectByFK(Class<? extends SimpleORMInterface> withFieldManyToOne, SimpleORMInterface fk) throws SQLException, IllegalAccessException, InstantiationException {
        Connection connection = ConnectionPoll.getConnection();
        String tableName = withFieldManyToOne.getAnnotation(Table.class).name();
        CRUDService crudService = new CRUDService(connection, withFieldManyToOne);
        ResultSet resultSet = crudService.selectByCondition(getFKColumnName(withFieldManyToOne), fk.getId());
        List<SimpleORMInterface> list = new ArrayList<>();
        SimpleORMInterface object;
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

