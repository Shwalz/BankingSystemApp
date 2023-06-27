package controllers;

import java.io.IOException;

public class BankException extends IOException {
    public BankException(String message) {
        super(message);
    }
}
