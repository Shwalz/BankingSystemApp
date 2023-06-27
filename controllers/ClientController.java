package controllers;

import javax.swing.*;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import dao.AccountDao;
import dao.UserDao;
import models.User;
import models.Account;
import services.Currency;
import gui.ClientPanel;


public class ClientController implements InitializableUI{
    private UserDao userDao;
    private AccountDao accountDao;
    private Account selectedAccount;
    public static boolean acceptRequest = false;
    private JFrame clientFrame;
    private DefaultListModel<Account> accountListModel;
    private JList<Account> accountList;
    private JTextArea accountDetailsTextArea;

    public ClientController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;

        accountListModel = new DefaultListModel<>();
        accountList = new JList<>(accountListModel);
        accountDetailsTextArea = new JTextArea();

        clientFrame = new JFrame("Bank System - Client Interface");
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeAccountList();
    }

    @Override
    public void startUserInterface() {
        ClientPanel panel = new ClientPanel(this);
        clientFrame.getContentPane().add(panel);
        clientFrame.pack();
        clientFrame.setVisible(true);
        initializeAccountDetails();
    }

    private void initializeAccountList() {
        try {
            accountListModel.clear();
            accountListModel.addAll(accountDao.load());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(clientFrame, "Error loading account data. Please try again.", "Account List Failed", JOptionPane.ERROR_MESSAGE);
        }

        accountList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = accountList.locationToIndex(evt.getPoint());
                    Account selectedAccount = accountListModel.getElementAt(index);
                    updateAccountDetails(selectedAccount, accountDetailsTextArea);
                }
            }
        });
    }

    private void initializeAccountDetails() {
        accountList.addListSelectionListener(e -> {
            Account selectedAccount = accountList.getSelectedValue();
            if (selectedAccount != null) {
                updateAccountDetails(selectedAccount, null);
            }
        });
    }

    public void openAccount() {
        String accountNumber = JOptionPane.showInputDialog(clientFrame, "Please enter the account number:", "Open Account", JOptionPane.PLAIN_MESSAGE);
        String currencyString = JOptionPane.showInputDialog(clientFrame, "Please enter the currency (USD/PLN):", "Open Account", JOptionPane.PLAIN_MESSAGE);
        Currency currency;
        try {
            currency = Currency.valueOf(currencyString);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(clientFrame, "Invalid currency. Please enter USD or PLN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isAccountNumberExists(accountNumber)) {
            JOptionPane.showMessageDialog(clientFrame, "Account with the specified number already exists. Opening a new account is not possible.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (accountNumber == null || accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(clientFrame, "Account number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Account newAccount = new Account(accountNumber, currency, 0.0);
        accountListModel.addElement(newAccount);

        try {
            accountDao.save(newAccount);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(clientFrame, "Error saving account data. Please try again.", "Open Account Failed", JOptionPane.ERROR_MESSAGE);
        }

        initializeAccountList();
        updateAccountDetails(newAccount, accountDetailsTextArea);
        updateAccountList();
    }

    private boolean isAccountNumberExists(String accountNumber) {
        for (int i = 0; i < accountListModel.size(); i++) {
            Account account = accountListModel.getElementAt(i);
            if (account.getAccountNumber().equals(accountNumber)) {
                return true;
            }
        }
        return false;
    }

    private void updateAccountList() {
        initializeAccountList();

        try {
            accountListModel.clear();
            accountListModel.addAll(accountDao.load());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(clientFrame, "Error loading account data. Please try again.", "Account List Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewBalance(Account selectedValue) {
        selectedAccount = selectedValue;
        if (selectedAccount != null) {

            double balance = selectedAccount.getBalance();
            JOptionPane.showMessageDialog(clientFrame, "Account Balance: " + balance + " " + selectedAccount.getCurrency(), "Balance", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(clientFrame, "No account selected. Please select an account.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deposit(Account selectedValue, JTextArea accountDetailsTextArea) {
        Account selectedAccount = selectedValue;
        if (selectedAccount != null) {
            String amountString = JOptionPane.showInputDialog(clientFrame, "Enter Amount to Deposit:");
            double amount = Double.parseDouble(amountString);
            double currentBalance = selectedAccount.getBalance();
            selectedAccount.setBalance(currentBalance + amount);
            updateAccountDetails(selectedAccount, accountDetailsTextArea);
            accountList.getSelectedValue();
            try {
                accountDao.save(selectedAccount);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(clientFrame, "Error saving account data. Please try again.", "Deposit Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(clientFrame, "No account selected. Please select an account.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void withdraw(Account selectedValue, JTextArea accountDetailsTextArea) throws BankException {
        Account selectedAccount = selectedValue;
        if (selectedAccount != null) {
            String amountString = JOptionPane.showInputDialog(clientFrame, "Enter Amount to Withdraw:");
            double amount = Double.parseDouble(amountString);
            double currentBalance = selectedAccount.getBalance();
            if (amount <= currentBalance && amount < 1000) {
                selectedAccount.setBalance(currentBalance - amount);
                updateAccountDetails(selectedAccount, accountDetailsTextArea);
                JOptionPane.showMessageDialog(clientFrame, "Withdrawal Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                try {
                    accountDao.save(selectedAccount);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(clientFrame, "Error saving account data. Please try again.", "Withdrawal Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else if (amount > currentBalance) {
                JOptionPane.showMessageDialog(clientFrame, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (amount >= 1000) {

                JFrame frame = new JFrame("Employee interface. Login");
                JTextField textField1 = new JTextField();
                JTextField textField2 = new JPasswordField();

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                panel.add(new JLabel("Username:"));
                panel.add(textField1);
                panel.add(new JLabel("Password:"));
                panel.add(textField2);

                JButton button = new JButton("Login");

                button.addActionListener(e -> {
                    String text1 = textField1.getText();
                    String text2 = textField2.getText();

                    AuthenticationController ac = new AuthenticationController(new UserDao(), new AccountDao());
                    ac.login(text1,text2, User.UserType.Employee);

                    if (acceptRequest) {
                        selectedAccount.setBalance(currentBalance - amount);
                        updateAccountDetails(selectedAccount, accountDetailsTextArea);
                        JOptionPane.showMessageDialog(clientFrame, "Withdrawal Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                        acceptRequest = false;
                    } else {
                        JOptionPane.showMessageDialog(clientFrame, "Withdrawal request has been cancelled", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }

                    frame.dispose();
                });

                panel.add(button);

                frame.getContentPane().add(panel);

                frame.setSize(300, 200);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(clientFrame, "No account selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void requestLoan(Account selectedValue, JTextArea accountDetailsTextArea) throws BankException {
        Account selectedAccount = selectedValue;
        if (selectedAccount != null) {
            String amountString = JOptionPane.showInputDialog(clientFrame, "Enter Amount for Loan:");
            double amount = Double.parseDouble(amountString);
            double currentBalance = selectedAccount.getBalance();


                JFrame frame = new JFrame("Employee interface. Login");
                JTextField textField1 = new JTextField();
                JTextField textField2 = new JPasswordField();

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                panel.add(new JLabel("Username:"));
                panel.add(textField1);
                panel.add(new JLabel("Password:"));
                panel.add(textField2);

                JButton button = new JButton("Login");

                button.addActionListener(e -> {
                    String text1 = textField1.getText();
                    String text2 = textField2.getText();

                    AuthenticationController ac = new AuthenticationController(new UserDao(), new AccountDao());
                    ac.login(text1,text2, User.UserType.Employee);

                    if (acceptRequest) {
                        selectedAccount.setBalance(currentBalance + amount);
                        updateAccountDetails(selectedAccount, accountDetailsTextArea);
                        JOptionPane.showMessageDialog(clientFrame, "Loan Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                        acceptRequest = false;
                    } else {
                        JOptionPane.showMessageDialog(clientFrame, "Loan has been calncelled", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }

                    frame.dispose();
                });

                panel.add(button);

                frame.getContentPane().add(panel);

                frame.setSize(300, 200);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
    }

    public void updateAccountDetails(Account account, JTextArea textArea) {
        String accountDetails = getAccountDetails(account);
        textArea.setText(accountDetails);
    }

    private String getAccountDetails(Account account) {
        String accountDetails = "Account Number: " + account.getAccountNumber() + "\n"
                + "Currency: " + account.getCurrency() + "\n"
                + "Balance: " + account.getBalance();
        return accountDetails;
    }

    public DefaultListModel<Account> getAccountListModel() {
        return accountListModel;
    }

    public void logout() {
        clientFrame.dispose();
        AuthenticationController authenticationController = new AuthenticationController(userDao, accountDao);
        authenticationController.startUserInterface();
    }
}
