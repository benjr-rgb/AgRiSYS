package model;

/**
 * Egg extends Product directly (eggs are not "Meat"), and has its own extra
 * fields: egg size and how many pieces are in one tray.
 */
public class Egg extends Product {

    private String size;          // "Small", "Medium", "Large", "Jumbo"
    private int piecesPerTray;    // e.g. 30

    public Egg(String id, String name, String description,
               double pricePerUnit, String unit, int shelfLifeDays,
               String size, int piecesPerTray) {
        super(id, name, description, pricePerUnit, unit, shelfLifeDays);
        this.size = size;
        this.piecesPerTray = piecesPerTray;
    }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getPiecesPerTray() { return piecesPerTray; }
    public void setPiecesPerTray(int piecesPerTray) { this.piecesPerTray = piecesPerTray; }

    @Override
    public String getCategory() {
        return "Egg";
    }

    @Override
    public String getDetails() {
        return "Size: " + size + ", " + piecesPerTray + " pcs/tray, Shelf life: "
                + getShelfLifeDays() + " days";
    }

    @Override
    public String toFileString() {
        // the 7 shared fields + 2 egg fields = 9 fields (same count as Meat)
        return baseFileString() + "|" + clean(size) + "|" + piecesPerTray;
    }
}
