package test_files;

import annotations.*;
import crud_services.SimpleORMInterface;
import enums.GenerationType;

import java.util.ArrayList;
import java.util.List;

@Table(name = "can_use_auto")
@ManyToMany(mappedBy = "autos")
public class CanUseAuto implements SimpleORMInterface {

    @Id(strategy = GenerationType.SEQUENCE, name = "id")
    private int id;

    @Column(name = "name")
    private String nameUSer;

    @Column(name = "age")
    private int age;

    @OneToMany(mappedBy = "users") //name of the table
    private List<Auto> autos;


    public CanUseAuto() {
    }

    public CanUseAuto(String nameUSer, int age) {
        this.nameUSer = nameUSer;
        this.age = age;
        autos = new ArrayList<Auto>();
    }

    public void addAuto(Auto auto) {
        autos.add(auto);
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

    public void setAutos(Auto auto) {
        autos.add(auto);
    }


    @Override
    public String toString() {
        return "Name: " + this.getNameUSer() + " Age: " + this.age + " Id: " + this.getId();
    }
}


