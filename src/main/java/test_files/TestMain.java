package test_files;

import generatedvaluehandler.GeneratedValueHandler;
import simpleorm.SimpleORM;

import java.util.List;

public class TestMain {

    public static void main(String[] args) {
//        Users user = new Users("Vasya", 30);
//        user.setId(GeneratedValueHandler.generatedValueHandler.getId(user));
//        System.out.println(user.getId());
//
//        Auto auto1 = new Auto("Mazda", "black");
//        user.addAuto(auto1);
//        auto1.setId(GeneratedValueHandler.generatedValueHandler.getId(auto1));

//        Books books = new Books("Book1", 1993);
//        user.addBooks(books);

//        System.out.println(auto1.getId());
//        System.out.println(auto1.toString());
//        System.out.println(user.getBooks());
//        System.out.println("____________");

//        ProcessOneToMany processOneToMany = new ProcessOneToMany();


//        processOneToMany.saveOneToMany(user);

        SimpleORM simpleORM = new SimpleORM();
        //simpleORM.save(auto1);///////////////????????CRUD
        //simpleORM.save(user);
        //simpleORM.selectById(129, Users.class);
        //simpleORM.selectAll(user.getClass());


//create user and save to table
        Users user1 = new Users("Vasya2", 2);
        user1.setId(GeneratedValueHandler.generatedValueHandler.getId(user1));
        simpleORM.save(user1);

// create 2 cars and assign them to user
        Auto auto1 = new Auto("Mazda", "red");
        Auto auto2 = new Auto("BMW", "black");

        user1.addAuto(auto1);
        user1.addAuto(auto2);

        auto1.setUser(user1);
        auto2.setUser(user1);

        simpleORM.update(user1);


//get user from table by id, set new name and update user in table
        Users user2;
        user2 = (Users) simpleORM.selectByRowId(1, Users.class);
        System.out.println("Get from table " + user2.toString());
//       simpleORM.update(user2);


//        Select all from table
//        List<Users> list = (List<Users>) (List<?>) simpleORM.selectAll(Users.class);
//        if (list.size() != 0) {
//            for (Users u : list) {
//                System.out.println(u.getName() + " " + u.getId() + " " + u.getAge());
//            }
//        }


//        Select all from table
//        List<Auto> listAutos = (List<Auto>) (List<?>) simpleORM.selectAll(Auto.class);
//        if (list.size() != 0) {
//            for (Auto u : listAutos) {
//                System.out.println( u.getModel() + " " + u.getColor() + " " + u.getUser());
//            }
//        }

//        Users user = (Users) simpleORM.selectByRowId(2, Users.class);
//        //Books book = (Books) simpleORM.selectById(user.getId(), Books.class);
//        //System.out.println(book.toString());
//        Auto auto2 = (Auto) simpleORM.selectByRowId(4, Auto.class);
//        System.out.println(auto2.toString());

    }
}
