package persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import exception.DataLoadException;
import model.Beef;
import model.Chicken;
import model.Egg;
import model.Pork;
import model.Product;

/**
 * CsvImporter reads the product list exported from your Google Sheet
 * (File > Download > Comma Separated Values) and turns each row into a Product.
 *
 * EXPECTED COLUMNS (in this order):
 *     category, name, description, price, unit
 * Example row:   Pork,Pork Belly,Fresh sliced belly,380,kg
 *
 * IDs are generated automatically (P001, P002, ...).
 * If your sheet has different columns, change ONLY the parseRow() method.
 *
 * Because the sheet has no cut/size/shelf-life columns, we fill in simple
 * defaults (see parseRow). The Manager can edit them later in the program.
 */
public class CsvImporter {

    private ArrayList<String> warnings;

    public CsvImporter() {
        warnings = new ArrayList<String>();
    }

    public ArrayList<String> getWarnings() { return warnings; }

    public ArrayList<Product> importProducts(String path) throws DataLoadException {
        ArrayList<Product> result = new ArrayList<Product>();
        int nextNumber = 1;   // used to build the IDs P001, P002, ...

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            int lineNumber = 1;
            while (line != null) {
                String trimmed = line.trim();
                boolean isHeader = trimmed.toLowerCase().startsWith("category");

                if (!trimmed.isEmpty() && !isHeader) {
                    try {
                        String id = String.format("P%03d", nextNumber);   // P001, P002 ...
                        result.add(parseRow(trimmed, id));
                        nextNumber++;
                    } catch (Exception e) {
                        // Bad row: skip it, remember why, keep going.
                        warnings.add("CSV line " + lineNumber + " skipped: " + e.getMessage());
                    }
                }
                line = reader.readLine();
                lineNumber++;
            }
        } catch (IOException e) {
            throw new DataLoadException("Could not read " + path + ": " + e.getMessage());
        }
        return result;
    }

    /** Turns ONE csv row into a Product. Change this if your columns differ. */
    private Product parseRow(String line, String id) {
        String[] tokens = line.split(",");
        if (tokens.length < 5) {
            throw new IllegalArgumentException("needs at least 5 columns but found " + tokens.length);
        }

        // First two columns and last two columns are fixed.
        // Everything in between is the description (it may contain commas).
        String category = tokens[0].trim().toLowerCase();
        String name = tokens[1].trim();
        String unit = normalizeUnit(tokens[tokens.length - 1]);
        // Keep only digits and "." so things like "PHP 380.00" still work.
        String priceText = tokens[tokens.length - 2].replaceAll("[^0-9.]", "");
        double price = Double.parseDouble(priceText);

        String description = "";
        for (int i = 2; i < tokens.length - 2; i++) {
            description = description + tokens[i];
            if (i < tokens.length - 3) {
                description = description + ",";
            }
        }
        description = description.replace("\"", "").trim();
        if (description.isEmpty()) {
            description = name;   // never leave the description empty
        }

        // Defaults for the fields our sheet does not have:
        //   meat -> cut = product name, fresh, shelf life 5 days
        //   egg  -> medium size, 30 pieces per tray, shelf life 30 days
        if (category.startsWith("pork")) {
            return new Pork(id, name, description, price, unit, 5);
        } else if (category.startsWith("beef")) {
            return new Beef(id, name, description, price, unit, 5);
        } else if (category.startsWith("chicken")) {
            return new Chicken(id, name, description, price, unit, 5);
        } else if (category.startsWith("egg")) {
            return new Egg(id, name, description, price, unit, 30, "Medium", 30);
        } else {
            throw new IllegalArgumentException("unknown category \"" + tokens[0].trim() + "\"");
        }
    }

    /** Turns "KG", "kilo", "pc", "pcs", "piece" ... into our two allowed units. */
    private String normalizeUnit(String text) {
        String u = text.trim().toLowerCase();
        if (u.startsWith("kg") || u.startsWith("kilo")) {
            return "kg";
        }
        if (u.startsWith("pc") || u.startsWith("piece") || u.startsWith("tray")) {
            return "pieces";
        }
        return u;   // unknown: ProductManager will reject it with a clear message
    }
}
