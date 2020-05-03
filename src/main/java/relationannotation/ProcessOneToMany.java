package relationannotation;


import annotations.OneToMany;
import connectiontodb.ConnectionPoll;
import crud_services.CRUDService;
import crud_services.SimpleORMInterface;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessOneToMany {


    private static List<Object> getOneToManyLists(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        List<Object> listWithFields = new ArrayList<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(OneToMany.class)) {
                try {
                    f.setAccessible(true);
                    if (!(f.get(object) == null))
                        listWithFields.add(f.get(object));
                    f.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return listWithFields;
    }



    public static void saveOneToMany(Object object) {
        Connection connection1 = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection1,object.getClass());
        List<Object> childList = getOneToManyLists(object);

        for (Object s : childList) {
            List<Object> list = new ArrayList<>();
            list.addAll(List.class.cast(s));
            if (list.size() > 0) {
                for (Object o : list) {

                    try {
                        crudService.insert((SimpleORMInterface) o);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

       // ConnectionPoll.releaseConnection(connection1);
//        try {
//            connection1.close();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }



    public static void updateOneToMany(Object object) {
        Connection connection = ConnectionPoll.getConnection();
        CRUDService crudService = new CRUDService(connection,object.getClass());
        List<Object> childList = getOneToManyLists(object);

        for (Object s : childList) {
            List<Object> list = new ArrayList<>();
            list.addAll(List.class.cast(s));
            if (list.size() > 0) {
                for (Object o : list) {

                    try {
                        crudService.update((SimpleORMInterface) o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
//        ConnectionPoll.releaseConnection(connection);
//        try {
//            connection.close();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }


    public static void selectOneToMany(Object object){

    }



}
