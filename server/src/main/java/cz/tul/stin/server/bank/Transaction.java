package cz.tul.stin.server.bank;

public class Transaction {

    private char operation;
    private Currency currency;
    private String message;

    public Transaction(char operation, Currency currency) {
        this.operation = operation;
        this.currency = currency;
        this.message = "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Transaction(char operation, Currency currency, String message) {
        this.operation = operation;
        this.currency = currency;
        this.message = message;
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
                "operation=" + operation +
                ", currency=" + currency +
                ", message='" + message + '\'' +
                '}';
    }
}
