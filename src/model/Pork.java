package model;

/* Pork is a Meat. The only thing it adds is its own category name. */
public class Pork extends Meat {

    public Pork(String id, String name, String description,
                double pricePerUnit, String unit, int shelfLifeDays,
                String cut, boolean frozen) {
        super(id, name, description, pricePerUnit, unit, shelfLifeDays, cut, frozen);
    }

    @Override
    public String getCategory() {
        return "Pork";
    }
}
