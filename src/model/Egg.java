package model;

/*
 * Egg extends from Product directly as it is not part of meat family
 */
public class Egg extends Product {

    private String size;          // "Small", "Medium", "Large", "Jumbo"
    private int piecesPerTray;    // ex. 30
    
    //constructors for egg
    public Egg(String id, String name, String description,
               double pricePerUnit, String unit, int shelfLifeDays,
               String size, int piecesPerTray) {
        super(id, name, description, pricePerUnit, unit, shelfLifeDays);
        this.size = size;
        this.piecesPerTray = piecesPerTray;
    }
    //GETTERS AND SETTERS for size and piecesPerTray of eggs
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getPiecesPerTray() { return piecesPerTray; }
    public void setPiecesPerTray(int piecesPerTray) { this.piecesPerTray = piecesPerTray; }

    //returns the category of egg which is a string Egg
    @Override
    public String getCategory() {
        return "Egg";
    }
    
    
    //Returns a string which included all the details the product egg has which is size, pieces, and shelflife count from its parent product
    @Override
    public String getDetails() {
        return "Size: " + size + ", " + piecesPerTray + " pcs/tray, Shelf life: "
                + getShelfLifeDays() + " days";
    }
    
    //returning a string to be saved into the file which contains all the of its attributes from product and its own 2 unique attributes (size and piecePerTray)
    @Override
    public String toFileString() {
        // the 7 shared fields + 2 egg fields = 9 fields 
        return baseFileString() + "|" + clean(size) + "|" + piecesPerTray;
    }
}
