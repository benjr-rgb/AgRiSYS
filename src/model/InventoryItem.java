package model;

import java.time.LocalDate;
import exception.ValidationException;

/**
 * InventoryItem = "how much stock do we have of ONE product".
 *
 * It HAS-A Product (this is called composition/association): the item does not
 * copy the product's name or price, it just points to the Product object.
 * Quantity is a double because meat is sold by weight (e.g. 12.5 kg).
 */
public class InventoryItem {

    private Product product;
    private double quantity;        // in kg or pieces (depends on product.getUnit())
    private double reorderLevel;    // warn when quantity falls to this value or below
    private LocalDate dateStocked;  // date of the most recent stocking

    /** Constructor used when creating a NEW item: dateStocked = today. */
    public InventoryItem(Product product, double quantity, double reorderLevel) {
        this(product, quantity, reorderLevel, LocalDate.now());
    }

    /** Constructor used when LOADING from a file: we already know the date. */
    public InventoryItem(Product product, double quantity, double reorderLevel,
                         LocalDate dateStocked) {
        this.product = product;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.dateStocked = dateStocked;
    }

    public Product getProduct() { return product; }
    public double getQuantity() { return quantity; }
    public double getReorderLevel() { return reorderLevel; }
    public LocalDate getDateStocked() { return dateStocked; }

    /** Changes the reorder level (the "low stock" threshold). */
    public void setReorderLevel(double reorderLevel) throws ValidationException {
        if (reorderLevel < 0) {
            throw new ValidationException("Reorder level cannot be negative.");
        }
        this.reorderLevel = reorderLevel;
    }

    /** Adds stock. The amount must be greater than 0. */
    public void restock(double amount) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Restock amount must be greater than 0.");
        }
        quantity = quantity + amount;
        // Simplification: the whole stock uses the date of the latest restock.
        dateStocked = LocalDate.now();
    }

    /**
     * Removes stock. Not used in PM 2, but PM 3 (sales) will call it.
     * We write it now so we do not have to redesign this class later.
     */
    public void deduct(double amount) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Amount to deduct must be greater than 0.");
        }
        if (amount > quantity) {
            throw new ValidationException("Not enough stock. Only " + quantity + " left.");
        }
        quantity = quantity - amount;
    }

    /** True when stock is at or below the reorder level. */
    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    /** Expiry date = date stocked + shelf life of the product. */
    public LocalDate getExpiryDate() {
        return dateStocked.plusDays(product.getShelfLifeDays());
    }

    /** True when today is already past the expiry date. */
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

    /** One line for the inventory save file: productId|quantity|reorderLevel|date */
    public String toFileString() {
        return product.getId() + "|" + quantity + "|" + reorderLevel + "|" + dateStocked;
    }
}
