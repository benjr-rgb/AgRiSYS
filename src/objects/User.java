package objects;

//abstract class for manager and clerk
public abstract class User {
	
    private String username;
    private String password;  

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }

    //Returns if its a Manager or Clerk object as a string 
    public abstract String getRole();

    //Checks if the user has abilities to edit, delete, and add products base on the objects role
    public abstract boolean canEditProducts();

    //Checks also if the user can restock or set reorder level for products
    public abstract boolean canRestock();

    //Password validator which compares the objects password to the inputed password to see if its the same
    //returns true if it equaled and false if not
    public boolean checkPassword(String attempt) {
        return password.equals(attempt);
    }

    /*Saves the users Username, password, and role in one string
     *and its formatted with a separator "|" Username|Password|Role
     * */
    public String toFileString() {
        return username + "|" + password + "|" + getRole();
    }
}
