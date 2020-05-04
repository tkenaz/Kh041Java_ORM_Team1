package test_files;

import annotations.*;
import crud_services.SimpleORMInterface;
import enums.GenerationType;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
public class Users implements SimpleORMInterface {

    @Id(strategy = GenerationType.SEQUENCE, name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @OneToMany(mappedBy = "users") //name of the table
    private List<Auto> autos;

    //@OneToMany(mappedBy = "users") //name of the table
    private List<Books> books;


    public Users() {
    }

    public Users(String name, int age) {
        this.name = name;
        this.age = age;
        autos = new ArrayList<Auto>();
        books = new ArrayList<Books>();
    }

    public void addAuto(Auto auto) {
        auto.setUser(this);
        autos.add(auto);
    }

    public void addBooks(Books book) {
        book.setUser(this);
        books.add(book);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Auto> getAutos(){
        return autos;
    }

    public List<Books> getBooks(){
        return books;
    }
}
