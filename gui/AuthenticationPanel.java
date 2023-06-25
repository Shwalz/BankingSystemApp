package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import models.User.UserType;
import controllers.AuthenticationController;

public class AuthenticationPanel extends JPanel {
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<UserType> userTypeComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationController authenticationController;

    public AuthenticationPanel(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        add(passwordField, constraints);

        JLabel userTypeLabel = new JLabel("User Type:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        add(userTypeLabel, constraints);

        userTypeComboBox = new JComboBox<>(UserType.values());

        userTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UserType selectedValue = (UserType) userTypeComboBox.getSelectedItem();

                if (selectedValue.equals(UserType.Employee)) {
                    loginButton.setEnabled(false);
                } else {
                    loginButton.setEnabled(true);
                }
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        add(userTypeComboBox, constraints);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                UserType userType = (UserType) userTypeComboBox.getSelectedItem();
                authenticationController.login(username, password, userType);
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        add(loginButton, constraints);

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                UserType userType = (UserType) userTypeComboBox.getSelectedItem();
                authenticationController.register(username, password, userType);
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        add(registerButton, constraints);
    }
}
