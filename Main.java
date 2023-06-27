import dao.AccountDao;
import dao.UserDao;
import controllers.AuthenticationController;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        AccountDao accountDao = new AccountDao();
        AuthenticationController authenticationController = new AuthenticationController(userDao, accountDao);
        authenticationController.startUserInterface();
    }
}
