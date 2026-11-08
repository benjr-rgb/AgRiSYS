package objects;

import java.time.LocalDate;
import exception.ValidationException;

public class InventoryItem {

    private Product product;        //an object of product
    private double stock;        // in kg or pieces (depends on product.getUnit())
    private double reorderLevel;    // warn when stock falls to this value or below
    private LocalDate dateStocked;  // date of the most recent stocking

    /*this constructor is for inputing a new item hence at the last constructor
    it stores the current date, while it calls the other constructors using this
    */
    public InventoryItem(Product product, double stock, double reorderLevel) {
        this(product, stock, reorderLevel, LocalDate.now());
    }

    /* this constructor is used when loading from a file which contains the date already
     * and this constructor is also called by the other one where it just passes the current date
     * instead of the stored one */
    public InventoryItem(Product product, double stock, double reorderLevel,
                         LocalDate dateStocked) {
        this.product = product;
        this.stock = stock;
        this.reorderLevel = reorderLevel;
        this.dateStocked = dateStocked;
    }
    
    // Getters and setters for the attributes
    public Product getProduct() { return product; }
    public double getStock() { return stock; }
    public double getReorderLevel() { return reorderLevel; }
    public LocalDate getDateStocked() { return dateStocked; }

    /*Changes the reorder level of a product by taking in a integer
     * it would throw a exception if the user inputed a negative value*/
    public void setReorderLevel(double reorderLevel) throws ValidationException {
        if (reorderLevel < 0) {
            throw new ValidationException("Reorder level cannot be negative.");
        }
        this.reorderLevel = reorderLevel;
    }

    /* Adds a stock to the products current amount*/
    public void restock(double amount) throws ValidationException {
    	//if the amount inputed is 0 or negative, it will throw a exception an and a message
        if (amount <= 0) {
            throw new ValidationException("Restock amount must be greater than 0.");
        }
        
        //stock of a product will be increased by a valid amount number
        stock = stock + amount;
        //dateStocked variable will be updated as we've updated the number of stock a product has
        dateStocked = LocalDate.now();
    }

    /* Deduct inputed amount in the overall stock of a product
     */
    public void deduct(double amount) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Amount to deduct must be greater than 0.");
        }
        if (amount > stock) {
            throw new ValidationException("Not enough stock. Only " + stock + " left.");
        }
        //Will throw an exception if the amount is greater than the quantities product or if its 0 or negative
        stock = stock - amount;
    }

    //Checker if the stock is low and needs to be reordered
    //Will return True if the stock is below or equal to the reorderLevel of a product
    public boolean isLowStock() {
        return stock <= reorderLevel;
    }

    /*Expiry date will be calculated by adding the dateStocked days, to the shelf life of the product
     * so it will return a date which will tell date it will expire
     * */
    public LocalDate getExpiryDate() {
        return dateStocked.plusDays(product.getShelfLifeDays());
    }

    /*Returns true if the date is already passed the expiry date*/
    public boolean isExpired() {
        return LocalDate.now().isAfter(getExpiryDate());
    }

    /**
     * True when the item expires within the next "days" days.
     * Items that are already expired also count (their expiry is before today).
     */
    public boolean isExpiringSoon(int days) {
        LocalDate limit = LocalDate.now().plusDays(days);
        return !getExpiryDate().isAfter(limit);
    }

    /* One string line for the inventory save file and is formated as productId|stock|reorderLevel|date with "|" as a separator*/
    public String toFileString() {
        return product.getId() + "|" + stock + "|" + reorderLevel + "|" + dateStocked;
    }
}
