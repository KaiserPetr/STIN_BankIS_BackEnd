package cz.tul.stin.server.bank;

public class User {
    private int id;
    private String firstname, surname, email;

    public User(int id, String firstname, String surname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }
}
