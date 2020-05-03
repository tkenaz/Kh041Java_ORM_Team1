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

        ProcessOneToMany processOneToMany = new ProcessOneToMany();

//        auto1.save();
//        user.save();

//        processOneToMany.saveOneToMany(user);

        SimpleORM simpleORM = new SimpleORM();
        simpleORM.save(auto1);
        //simpleORM.selectById(1);
        //simpleORM.selectAll(user.getClass());

    }
}
