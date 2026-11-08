package managers;

import java.util.ArrayList;

import objects.Clerk;
import objects.Manager;
import objects.User;

/**
 * AuthService keeps the list of user accounts and checks logins.
 */
public class LoginManager {

    private ArrayList<User> users;

    public LoginManager() {
        users = new ArrayList<User>();
    }

    public ArrayList<User> getUsers() { return users; }

    /** Replaces the whole list (used when loading from a file). */
    public void setUsers(ArrayList<User> users) { this.users = users; }

    /** Creates one Manager and one Clerk. Used on the very first run. */
    public void seedDefaultUsers() {
        users.clear();
        users.add(new Manager("manager", "manager123"));
        users.add(new Clerk("clerk", "clerk123"));
    }

    /**
     * Returns the matching User if the username and password are right,
     * otherwise returns null.
     * The returned object is really a Manager or a Clerk, but we only see it
     * as a User, and its permission methods still behave correctly.
     */
    public User login(String username, String password) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.checkPassword(password)) {
                return u;
            }
        }
        return null;
    }
}
