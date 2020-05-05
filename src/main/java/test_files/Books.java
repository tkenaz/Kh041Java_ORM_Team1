package test_files;

import annotations.*;

import crud_services.SimpleORMInterface;
import enums.GenerationType;


//@Entity
@Table(name = "books")
public class Books implements SimpleORMInterface {

    @Id(strategy = GenerationType.SEQUENCE, name = "id")
    private int id;

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "book_size")
    private int size;

    @ManyToOne(mappedBy = "users")
    @JoinColumn(name = "users_id")
    @ForeignKey(name = "users_id")
    private Users user;

    public Books() {
    }

    public Books(String bookName, int size) {
        this.bookName = bookName;
        this.size = size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return size + " " + bookName + " owner " + user.getNameUSer();
    }
}
