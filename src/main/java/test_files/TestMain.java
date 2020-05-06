package test_files;

import simpleorm.SimpleORM;
import tablecreation.TableCreator;

import javax.jws.soap.SOAPBinding;
import java.sql.SQLException;
import java.util.List;

public class TestMain {

    public static void main(String[] args) {

        SimpleORM simpleORM = new SimpleORM();

//        simpleORM.addAnnotatedClass(Users.class);
//        simpleORM.addAnnotatedClass(Auto.class);
//        simpleORM.addAnnotatedClass(Books.class);

        TableCreator tableCreator = new TableCreator();
        try {
            tableCreator.createTable(Users.class);
            tableCreator.createTable(Auto.class);
            tableCreator.createTable(Books.class);
            tableCreator.createForeignKey(Auto.class);
            tableCreator.createForeignKey(Books.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

//// We create user, add autos, books, update values
//        Users user1 = new Users("Vanya2", 25);
//        simpleORM.save(user1);
//
////       create 2 cars and assign them to user
//        Auto auto1 = new Auto("Mazda2", "red");
//        Auto auto2 = new Auto("BMW", "black");
//
//        user1.addAuto(auto1);
//        auto1.setUser(user1);
//
//        auto2.setUser(user1);
//        user1.addAuto(auto2);
//
//        Books book = new Books("Potter 22", 2005);
//        user1.addBooks(book);
//        book.setUser(user1);
//
//        System.out.println("____________________");
//        simpleORM.update(user1);
//
//        System.out.println("____________________");
//        auto1.setModel("Maserati");
//        simpleORM.update(user1);
//
//        //simpleORM.delete(user1);
//
//        simpleORM.delete(auto1);
/////////////////////////////////////////////////////
        Users user3 = (Users) simpleORM.selectByPrimaryId(1, Users.class);
        System.out.println(user3.toString());

        simpleORM.selectObjectByForeignKey(Auto.class, user3);

        for (Auto a:user3.getAutos()) {
            System.out.println(a.toString());
        }






















//        Users user2;
//        user2 = (Users) simpleORM.selectByPrimaryId(4, Users.class);
//        System.out.println("Get from table " + user2.toString());

        // simpleORM.delete(user2);

//        List<Users> list = (List<Users>) (List<?>) simpleORM.selectAllToObject(Users.class);
//        if (list.size() != 0) {
//            for (Users u : list) {
//                System.out.println(u.getId() + " " + u.getName() + " " + u.getAge());
//            }
//        }


//        List<Auto> listAutos = (List<Auto>) (List<?>) simpleORM.selectAllToObject(Auto.class);
//        if (listAutos.size() != 0) {
//            for (Auto u : listAutos) {
//                System.out.println( u.getModel() + " " + u.getColor() + " " );
//            }
//        }

//        System.out.println("____________________");
//        user1.addAuto(auto2);
//        auto2.setUser(user1);
//        simpleORM.update(user1);


//        System.out.println("__________selectAllToObject method______________");
//        List<Auto> listAutos = (List<Auto>) (List<?>) simpleORM.selectAllToObject(Auto.class);
//        if (listAutos.size() != 0) {
//            for (Auto u : listAutos) {
//                System.out.println(u.getId() + " " + u.getModel() + " " + u.getColor() + " " );//+ u.getUser().getId());
//            }
//        }
//
//


//        System.out.println("___________selectAllToString method_____________");
//        List<String> listAutosString = (List<String>) (List<?>) simpleORM.selectAllToString(Auto.class);
//        if (listAutosString.size() != 0) {
//            for (String s : listAutosString) {
//                System.out.println(s );
//            }
//        }
//
//
//        System.out.println("___________selectAllToString method_____________");
//        List<String> listUSersString = (List<String>) (List<?>) simpleORM.selectAllToString(Users.class);
//        if (listUSersString.size() != 0) {
//            for (String s : listUSersString) {
//                System.out.println(s );
//            }
//        }

        //testing selectObjectByForeignKey
//        Users userById = (Users) simpleORM.selectByPrimaryId(1, Users.class);
//        System.out.println(userById.toString());
//
//        simpleORM.selectObjectByForeignKey(Auto.class, userById);
//        System.out.println(userById.getAutos().toString());


//get user from table by id, set new name and update user in table
//        Users user2;
//        user2 = (Users) simpleORM.selectByRowId(1, Users.class);
//        System.out.println("Get from table " + user2.toString());
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
