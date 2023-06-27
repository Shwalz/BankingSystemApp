package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import models.User;
import models.User.UserType;

public class UserDao extends DatabaseAccessObject<User> {
    private static final String USER_DATA_FILE = "userdata.txt";
    private static final String FILE_ENCODING = "UTF-8";

    public List<User> load() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(USER_DATA_FILE),
                        FILE_ENCODING))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseFromLine(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to load user data", e);
        }
        return users;
    }

    public void save(User user) throws IOException {
        List<User> users = load();
        users.add(user);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(USER_DATA_FILE),
                        FILE_ENCODING))) {
            for (User u : users) {
                String line = convertToLine(u);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Failed to save user data", e);
        }
    }

    public User parseFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            String username = parts[0];
            String password = parts[1];
            String role = parts[2];
            return new User(username, password, UserType.valueOf(role));
        } else {
            return null;
        }
    }

    public String convertToLine(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        UserType userType = user.getUserType();
        return username + "," + password + "," + userType.name();
    }
}

