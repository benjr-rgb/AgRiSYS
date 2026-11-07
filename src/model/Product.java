package model;

/**
 * Product is the ABSTRACT parent class of everything the shop sells.
 * "abstract" means you can NOT write "new Product(...)". You must create one
 * of its children instead (Pork, Beef, Chicken, Egg).
 *
 * Everything the children have in common lives here (id, name, price, ...).
 * Things that are different per child are declared abstract and every child
 * must provide its own version (this is POLYMORPHISM).
 *
 * We do not use enums, so the allowed categories and units are plain String
 * arrays that the rest of the program can check against.
 */
public abstract class Product {

   	/* String array that contains the only available categories such as pork, beef, or egg, and when a manager adds a 
	 * product it will used as a checker in the function (isInList) if the category and unit inputed by the manager is found on both the arrays
	 * and after that it assigns the unit attribute from the UNITS array based on the managers input of unit measurement.
    */
    public static final String[] CATEGORIES = { "Pork", "Beef", "Chicken", "Egg" };
    public static final String[] UNITS = { "kg", "pieces" };

    // only this class can touch these attributes and appropriate getters and setters can be seen below.
    private String id; //product id
    private String name; // product name
    private String description; //description of the product
    private double pricePerUnit; //the price per kg or pieces
    private String unit;          // unit of the product can be kg or pieces
    private int shelfLifeDays;    // days can last before expiration

    public Product(String id, String name, String description,
                   double pricePerUnit, String unit, int shelfLifeDays) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pricePerUnit = pricePerUnit;
        this.unit = unit;
        this.shelfLifeDays = shelfLifeDays;
        
        //Constructors
        
    }

    // GETTERS AND SETTERS FOR PRODUCTS ATTRIBUTESS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public int getShelfLifeDays() { return shelfLifeDays; }
    public void setShelfLifeDays(int shelfLifeDays) { this.shelfLifeDays = shelfLifeDays; }

    // ABSTRACT METHODS THAT SUBCLASSES NEEDS TO IMPLEMENT

    /* Returns "Pork", "Beef", "Chicken" or "Egg". */
    public abstract String getCategory();

    /* Returns a short text describing the category specific details. */
    public abstract String getDetails();

    /* Returns one line of text used to save the product in a file. */
    public abstract String toFileString();

    

    //Calculates the product price by multiplying the products price per unit by the quantity bought
    public double computePrice(double quantity) {
        return pricePerUnit * quantity;
    }

    /**
     * The first 7 fields of the save line. Every child calls this and then
     * adds its own extra fields at the end.
     * Fields are separated by the "|" character.
     * Format: category|id|name|description|price|unit|shelfLifeDays
     */
    
    /* This formats the products information in one string to be saved in a text file,
     * it's formatted by writing them by having a seperator "|" for easier file reading
     * category|id|name|description|price|unit|shelfLifeDays
     * used clean function to replace any "|" in name to be put as "/" in file so that when reading
     * it won't be confused as a attribute
     * */
    protected String baseFileString() {
        return getCategory() + "|" + id + "|" + clean(name) + "|" + clean(description)
                + "|" + pricePerUnit + "|" + unit + "|" + shelfLifeDays;
    }

    /* Changes any "|" in name and description so that when reading the file it wont be confused
     * as a separator "|"
     */
    protected String clean(String text) {
        return text.replace("|", "/").replace("\n", " ");
    }

    /** Checks if a value is inside a list (ignoring upper/lower case). */
    /* Checks the category and unit inputted is in the arrays CATEGORIES and UNITS which contains
     * only the available unit measurement and categories
     * Used for loop to loop through the list while comparing the inputed string (ignores the case regardless)
     * */
    public static boolean isInList(String value, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
