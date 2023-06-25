package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controllers.ClientController;
import models.Account;
import java.io.IOException;

public class ClientPanel extends JPanel {
    private ClientController clientController;
    private JButton openAccountButton;
    private JButton viewBalanceButton;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton loanApplicationButton;
    private JButton logoutButton;
    private JTextArea accountDetailsTextArea;
    private JList<Account> accountList;

    public ClientPanel(ClientController clientController) {
        this.clientController = clientController;
        this.accountList = new JList<>(clientController.getAccountListModel());

        accountDetailsTextArea = new JTextArea(5, 20);
        accountDetailsTextArea.setEditable(false);

        accountList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Account selectedAccount = accountList.getSelectedValue();
                if (selectedAccount != null) {
                    clientController.updateAccountDetails(selectedAccount, accountDetailsTextArea);
                }
            }
        });


        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        openAccountButton = new JButton("Open Account");
        openAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.openAccount();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(openAccountButton, constraints);

        JScrollPane accountListScrollPane = new JScrollPane(accountList);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        add(accountListScrollPane, constraints);


        viewBalanceButton = new JButton("View Balance");
        viewBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.viewBalance(accountList.getSelectedValue());
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(viewBalanceButton, constraints);

        depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.deposit(accountList.getSelectedValue(), accountDetailsTextArea);
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(depositButton, constraints);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clientController.withdraw(accountList.getSelectedValue(), accountDetailsTextArea);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(withdrawButton, constraints);

        loanApplicationButton = new JButton("Loan Application");
        loanApplicationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    clientController.requestLoan(accountList.getSelectedValue(), accountDetailsTextArea);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(loanApplicationButton, constraints);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.logout();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(logoutButton, constraints);

        accountDetailsTextArea = new JTextArea(5, 20);
        accountDetailsTextArea.setEditable(false);
        JScrollPane accountDetailsScrollPane = new JScrollPane(accountDetailsTextArea);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        add(accountDetailsScrollPane, constraints);
    }
}