package test_files;

import annotations.*;
import crud_services.SimpleORMInterface;
import enums.GenerationType;


//@Entity
@Table(name = "autos")
public class Auto implements SimpleORMInterface {

    @Id(strategy = GenerationType.SEQUENCE, name = "id")
    private int id;

    @Column(name = "model")
    private String model;

    @Column(name="color")
    private String color;

    @ManyToOne(mappedBy = "users")
    @Column(name = "user_id")
    @JoinColumn(name = "user_id")
    private Users user;

    public Auto() {
    }

    public Auto(String model, String color) {
        this.model = model;
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return color + " " + model + " owner " + user.getName();
    }
}
