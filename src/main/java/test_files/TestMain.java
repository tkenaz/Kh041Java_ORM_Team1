package test_files;

import generatedvaluehandler.GeneratedValueHandler;
import relationannotation.ProcessOneToMany;
import simpleorm.SimpleORM;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

    public static void main(String[] args) {
        Users user = new Users("Vasya", 30);
        user.setId(GeneratedValueHandler.generatedValueHandler.getId(user));
        System.out.println(user.getId());

        Auto auto1 = new Auto("Mazda", "black");
        user.addAuto(auto1);
        auto1.setId(GeneratedValueHandler.generatedValueHandler.getId(auto1));

//        Books books = new Books("Book1", 1993);
//        user.addBooks(books);

        System.out.println(auto1.getId());
        System.out.println(auto1.toString());
        System.out.println(user.getBooks());
        System.out.println("____________");

//        ProcessOneToMany processOneToMany = new ProcessOneToMany();


//        processOneToMany.saveOneToMany(user);

        SimpleORM simpleORM = new SimpleORM();
        //simpleORM.save(auto1);///////////////????????CRUD
        //simpleORM.save(user);
        //simpleORM.selectById(129, Users.class);
        //simpleORM.selectAll(user.getClass());


//get user from table by id, set new name and update user in table
//        Users user2;
//        user2 = (Users) simpleORM.selectById(1, Users.class);
//        System.out.println("Get from table " + user2.getName() + " " + user2.getAge());
//        user2.setName("Updated");
//        simpleORM.update(user2);


//        Select all from table
        List<Users> list = (List<Users>) (List<?>) simpleORM.selectAll(Users.class);
        if (list.size() != 0) {
            for (Users u : list) {
                System.out.println( u.getName()  + " " + u.getId() + " " + u.getAge());
            }
        }


//        Select all from table
        List<Auto> listAutos = (List<Auto>) (List<?>) simpleORM.selectAll(Auto.class);
        if (list.size() != 0) {
            for (Auto u : listAutos) {
                System.out.println( u.getModel() + " " + u.getColor() + " " + u.getUser());
            }
        }


    }
}
