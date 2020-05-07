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
    private String nameUSer;

    @Column(name = "age")
    private int age;

    @OneToMany(mappedBy = "users") //name of the table
    private List<Auto> autos = new ArrayList<>();

    @OneToMany(mappedBy = "users") //name of the table
    private List<Books> books = new ArrayList<>();


    public Users() {
    }

    public Users(String name, int age) {
        this.nameUSer = name;
        this.age = age;
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

    public void setNameUSer(String nameUSer) {
        this.nameUSer = nameUSer;
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public String getNameUSer() {
        return nameUSer;
    }

    public List<Auto> getAutos() {
        return autos;
    }

    public List<Books> getBooks() {
        return books;
    }

    @Override
    public String toString() {
        return "Name: " + this.getNameUSer() + " Age: " + this.age + " Id: " + this.getId();
    }
}
