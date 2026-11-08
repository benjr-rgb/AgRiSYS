package objects;

public class Chicken extends Product {

    public Chicken(String id, String name, String description,
            double pricePerUnit, String unit, int shelfLifeDays
            ) {
    super(id, name, description, pricePerUnit, unit, shelfLifeDays);
    }

    @Override
    public String getCategory() {
        return "Chicken";
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

