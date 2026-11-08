package objects;

public class Pork extends Product {

    public Pork(String id, String name, String description,
                double pricePerUnit, String unit, int shelfLifeDays
                ) {
        super(id, name, description, pricePerUnit, unit, shelfLifeDays);
    }

    @Override
    public String getCategory() {
        return "Pork";
    }
    
    @Override
    public String getDetails(){
        return getDescription();
    }

    @Override
    public String toFileString() {
        //calls the method basefilstring from product then returns with the objects porks attributes
    	return baseFileString();
    }
}

