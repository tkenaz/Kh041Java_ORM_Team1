package generatedvaluehandler;

import annotations.GeneratedValue;
import annotations.Id;
import annotations.Table;
import enums.GenerationType;

@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int age;
}
