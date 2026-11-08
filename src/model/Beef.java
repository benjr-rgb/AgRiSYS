package model;

/* Beef is a Meat. The only thing it adds is its own category name. */
public class Beef extends Product {

    public Beef(String id, String name, String description,
            double pricePerUnit, String unit, int shelfLifeDays
            ) {
    super(id, name, description, pricePerUnit, unit, shelfLifeDays);
    }

    @Override
    public String getCategory(){
        return "Beef";
    }
   
    @Override
    public String getDetails(){
        return getDescription();
    }
    

    @Override
    public String toFileString() {
        //calls the method basefilstring from product then returns with the objects Chickens attributes
    	return baseFileString();
    }
}
