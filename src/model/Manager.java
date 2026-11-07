package model;

//CAN DO ALL
public class Manager extends User {

    public Manager(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() { return "Manager"; }

    @Override
    public boolean canEditProducts() { return true; }

    @Override
    public boolean canRestock() { return true; }
}
