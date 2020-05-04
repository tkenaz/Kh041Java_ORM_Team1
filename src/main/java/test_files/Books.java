package test_files;

import annotations.Column;

import annotations.Id;
import annotations.JoinColumn;
import annotations.Table;
import crud_services.SimpleORMInterface;
import enums.GenerationType;


//@Entity
@Table(name = "books")
public class Books implements SimpleORMInterface {

    @Id(strategy = GenerationType.SEQUENCE, name = "id")
    private int id;

    @Column(name = "book")
    private String bookName;

    @Column(name = "year")
    private int year;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
    @Column(name = "user_id")
    @JoinColumn(name = "user_id")
    private Users user;

    public Books() {
    }

    public Books(String bookName, int year) {
        this.bookName = bookName;
        this.year = year;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return year + " " + bookName + " owner " + user.getName();
    }
}
