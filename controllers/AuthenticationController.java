package controllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import dao.UserDao;
import dao.AccountDao;
import models.User;
import models.User.UserType;
import gui.AuthenticationPanel;

public class AuthenticationController {
    private UserDao userDao;
    private AccountDao accountDao;
    private JFrame authenticationFrame;

    public AuthenticationController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;

        authenticationFrame = new JFrame("Bank System - Authentication");
        authenticationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void startAuthentication() {
        AuthenticationPanel panel = new AuthenticationPanel(this);
        authenticationFrame.getContentPane().add(panel);
        authenticationFrame.pack();
        authenticationFrame.setVisible(true);
    }

    public void login(String username, String password, UserType userType) {
        List<User> users;

        try {
            users = userDao.loadUsers();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(authenticationFrame, "Error loading user data. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.getUserType() == userType) {
                if (userType == UserType.Client) {
                    authenticationFrame.dispose();
                    initializeClientUI();
                } else if (userType == UserType.Employee) {
                        authenticationFrame.dispose();

                        JDialog dialog = new JDialog(authenticationFrame, "Choose option", true);
                        JButton button1 = new JButton("Accept");
                        button1.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ClientController.acceptRequest = true;
                                authenticationFrame.dispose();
                            }
                        });
                        JButton button2 = new JButton("Reject");
                        button2.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ClientController.acceptRequest = false;
                                authenticationFrame.dispose();
                            }
                        });
                        JPanel panel = new JPanel();
                        panel.add(button1);
                        panel.add(button2);

                        dialog.getContentPane().add(panel);

                        dialog.setSize(200, 100);
                        dialog.setLocationRelativeTo(authenticationFrame);
                        dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(authenticationFrame, "Invalid type of user. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(authenticationFrame, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }

    public void register(String username, String password, UserType userType) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(authenticationFrame, "Please fill in all fields.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<User> users = userDao.loadUsers();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(authenticationFrame, "Username already exists. Please try again.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            User newUser = new User(username, password, userType);
            userDao.saveUser(newUser);
            JOptionPane.showMessageDialog(authenticationFrame, "Registration successful! Please, log in.", "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(authenticationFrame, "Error saving user data. Please try again.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeClientUI() {
        ClientController clientController = new ClientController(userDao, accountDao);
        clientController.startClientInterface();
    }
}
