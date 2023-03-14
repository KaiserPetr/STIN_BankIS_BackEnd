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

    public Transaction(char operation, Currency currency, String message) {
        this.operation = operation;
        this.currency = currency;
        this.message = message;
    }

    public char getOperation() {
        return operation;
    }

    public void setOperation(char operation) {
        this.operation = operation;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "operation=" + operation +
                ", currency=" + currency +
                '}';
    }
}
