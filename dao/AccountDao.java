package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import models.Account;
import services.Currency;

public class AccountDao {
    private static final String ACCOUNT_DATA_FILE = "accountdata.txt";
    private static final String FILE_ENCODING = "UTF-8";

    public List<Account> loadAccounts() throws IOException {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(ACCOUNT_DATA_FILE),
                        FILE_ENCODING))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Account account = parseAccountFromLine(line);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to load account data", e);
        }
        return accounts;
    }

    public void saveAccount(Account account) throws IOException {
        List<Account> accounts = loadAccounts();
        int index = -1;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(account.getAccountNumber())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            accounts.set(index, account);
        } else {
            accounts.add(account);
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(ACCOUNT_DATA_FILE),
                        FILE_ENCODING))) {
            for (Account a : accounts) {
                String line = convertAccountToLine(a);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Failed to save account data", e);
        }
    }

    private Account parseAccountFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            String accountNumber = parts[0];
            Currency currency = Currency.valueOf(parts[1]);
            double balance = Double.parseDouble(parts[2]);
            return new Account(accountNumber, currency, balance);
        } else {
            return null;
        }
    }

    private String convertAccountToLine(Account account) {
        String accountNumber = account.getAccountNumber();
        String currency = account.getCurrency().name();
        String balance = Double.toString(account.getBalance());
        return accountNumber + "," + currency + "," + balance;
    }
}
