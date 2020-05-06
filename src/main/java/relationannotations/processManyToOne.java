package relationannotations;

import CRUD_Services.CRUDService;
import CRUD_Services.SimpleORMInterface;
import annotations.*;
import connectiontodb.ConnectionPoll;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class processManyToOne {

    private String getFKColumnName(Class<? extends SimpleORMInterface> withFieldManyToOne, SimpleORMInterface fk){
        String result = null;
        Field[] fields = withFieldManyToOne.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(field.isAnnotationPresent(ManyToOne.class)){
                if (field.getType().equals(fk.getClass())) {
                    result = field.getAnnotation(JoinColumn.class).name();
                    field.setAccessible(false);
                    break;
                }
            }
            field.setAccessible(false);
        }
        return result;
    }

    private void setUpCollectionOfObjects(SimpleORMInterface object, List<SimpleORMInterface> list) throws IllegalAccessException {
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

    public void selectByFK(Class<? extends SimpleORMInterface> withFieldManyToOne, SimpleORMInterface fk, CRUDService crudService) throws SQLException, IllegalAccessException, InstantiationException {
        String tableName = withFieldManyToOne.getAnnotation(Table.class).name();
        ArrayList<SimpleORMInterface> list = crudService.selectByCondition(getFKColumnName(withFieldManyToOne, fk), fk.getId());
        System.out.println(list.size());
        setUpCollectionOfObjects(fk, list);
        for(SimpleORMInterface object : list) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    field.set(object, fk);
                }
            }
        }

    }
}
