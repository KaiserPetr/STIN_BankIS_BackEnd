package cz.tul.stin.server.bank;

public class Transaction {

    private String id;
    private char operation;
    private Currency currency;
    private String message;

    public Transaction(String id, char operation, Currency currency, String message) {
        this.id = id;
        this.operation = operation;
        this.currency = currency;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Transaction(String id, char operation, Currency currency) {
        this.id = id;
        this.operation = operation;
        this.currency = currency;
        this.message = "";
    }

    public String getId() {
        return id;
    }
    public char getOperation() {
        return operation;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", operation=" + operation +
                ", currency=" + currency.toString() +
                ", message='" + message + '\'' +
                '}';
    }
}
