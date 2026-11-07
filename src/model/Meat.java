package model;

public abstract class Meat extends Product {
	//an abstract class that extends from product for poultry meets to inherit
	
    private String cut;       // example "Belly", "Ribeye", "Breast"
    private boolean frozen;   // true = frozen, false = fresh

    public Meat(String id, String name, String description,
                double pricePerUnit, String unit, int shelfLifeDays,
                String cut, boolean frozen) {

    	super(id, name, description, pricePerUnit, unit, shelfLifeDays);
        this.cut = cut;
        this.frozen = frozen;
    }
    
    //getters and setters for attributes cut and frozen
    public String getCut() { return cut; }
    public void setCut(String cut) { this.cut = cut; }

    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    @Override
    
    //returns a string which contains the cut(string), frozen(boolean F if fresh, T is frozen), and its shelf life.
    public String getDetails() {
        String storage = frozen ? "Frozen" : "Fresh";
        return "Cut: " + cut + ", " + storage + ", Shelf life: " + getShelfLifeDays() + " days";
    }
    
    
    @Override
    //calls the function basefilestring from product which will format it then adds the extra 2 attribute from class Meat
    //to save it in the file as singular string
    public String toFileString() {
        // the 7 shared fields + 2 meat fields = 9 fields
        return baseFileString() + "|" + clean(cut) + "|" + frozen;
    }
}
