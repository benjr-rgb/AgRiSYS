package model;

//CAN ONLY VIEW PRODUCTS AND INVENTORY HENCE IT RETURNS FALSE ON canEDITPRODUCTS AND canRESTOCK
public class Clerk extends User {

    public Clerk(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() { return "Clerk"; }

    @Override
    public boolean canEditProducts() { return false; }

    @Override
    public boolean canRestock() { return false; }
}
