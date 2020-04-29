package generatedvaluehandler;

import annotations.GeneratedValue;
import annotations.Id;
import annotations.Table;
import enums.GenerationType;

@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String name;

    private int age;
}
