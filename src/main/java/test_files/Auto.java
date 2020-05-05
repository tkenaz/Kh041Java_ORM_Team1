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

    @ForeignKey(name = "users_id")
    @ManyToOne(mappedBy = "users")
    @JoinColumn(name = "users_id")
    private Users user;

    public Auto() {
    }

    public Auto(String model, String colors_auto) {
        this.model = model;
        this.color = colors_auto;
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
        return color + " " + model + " owner ";
    }
}
