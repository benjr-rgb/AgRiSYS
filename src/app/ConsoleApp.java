package app;

import java.util.ArrayList;
import java.util.Scanner;
import exception.DataLoadException;
import exception.ValidationException;
import model.Beef;
import model.Chicken;
import model.Egg;
import model.InventoryItem;
import model.Pork;
import model.Product;
import model.User;

public class ConsoleApp {

    private static Scanner scanner = new Scanner(System.in);
    private static PorkingLotSystem system = new PorkingLotSystem();

        // MAIN: login loop

    public static void main(String[] args) {
        // Load saved data (or seed it) and show any messages.
        ArrayList<String> messages = system.startUp();
        for (String message : messages) {
            System.out.println("[INFO] " + message);
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== THE PORKING LOT ===");
            String username = readString("Username (type exit to quit): ");

            if (username.equalsIgnoreCase("exit")) {
                running = false;
            } else {
                String password = readString("Password: ");
                User user = system.login(username, password);
                if (user == null) {
                    System.out.println("Invalid username or password.");
                } else {
                    System.out.println("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
                    mainMenu();   // stays here until the user logs out
                }
            }
        }
        System.out.println("Goodbye!");
    }

        // MENU

    private static void mainMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            User user = system.getCurrentUser();
            boolean canEdit = user.canEditProducts();
            boolean canStock = user.canRestock();

       
            System.out.println("\n--- MENU ---");
            System.out.println(" 1  View all products");
            System.out.println(" 2  Search products by name");
            System.out.println(" 3  Filter products by category");
            System.out.println(" 4  View inventory");
            System.out.println(" 5  View low-stock items");
            System.out.println(" 6  View items expiring soon");
            System.out.println(" 7  Filter inventory by category");

            
            if (canEdit) {
                System.out.println(" 8  Add product");
                System.out.println(" 9  Edit product");
                System.out.println("10  Delete product");
            }
            if (canStock) {
                System.out.println("11  Restock product");
                System.out.println("12  Set reorder level");
            }
            System.out.println(" 0  Logout");
            int choice = readInt("Choose: ");

            
            if ((choice >= 8 && choice <= 10 && !canEdit)
                    || (choice >= 11 && choice <= 12 && !canStock)) {
                System.out.println("Please choose a number from the menu.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:  printProducts(system.getAllProducts()); break;
                    case 2:  searchProducts(); break;
                    case 3:  filterProducts(); break;
                    case 4:  printInventory(system.getInventoryItems()); break;
                    case 5:  printInventory(system.getLowStockItems()); break;
                    case 6:  viewExpiring(); break;
                    case 7:  filterInventory(); break;
                    case 8:  addProduct(); break;
                    case 9:  editProduct(); break;
                    case 10: deleteProduct(); break;
                    case 11: restock(); break;
                    case 12: setReorderLevel(); break;
                    case 0:
                        system.logout();
                        loggedIn = false;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Please choose a number from the menu.");
                }
            } catch (ValidationException e) {
                System.out.println("[ERROR] " + e.getMessage());
            } catch (DataLoadException e) {
                System.out.println("[FILE ERROR] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] That was not a valid number.");
            }
        }
    }

        // VIEW ACTIONS

    private static void searchProducts() {
        String keyword = readString("Search name: ");
        printProducts(system.searchProducts(keyword));
    }

    private static void filterProducts() {
        String category = readString("Category (Pork, Beef, Chicken, Egg): ");
        printProducts(system.filterProducts(category));
    }

    private static void filterInventory() {
        String category = readString("Category (Pork, Beef, Chicken, Egg): ");
        printInventory(system.filterInventory(category));
    }

    private static void viewExpiring() {
        int days = readInt("Show items expiring within how many days? ");
        printInventory(system.getExpiringSoon(days));
    }

        // MANAGER ACTIONS

    private static void addProduct() throws ValidationException, DataLoadException {
        // Check the category first so the manager does not type everything for nothing.
        String category = readString("Category (Pork, Beef, Chicken, Egg): ");
        if (!Product.isInList(category, Product.CATEGORIES)) {
            System.out.println("[ERROR] Category must be Pork, Beef, Chicken or Egg.");
            return;
        }

        String id = readString("Product ID: ");
        String name = readString("Name: ");
        String description = readString("Description: ");
        double price = readDouble("Price per unit: ");
        String unit = readString("Unit (kg or pieces): ");
        int shelfLife = readInt("Shelf life (days): ");

        // Build the right kind of object depending on the category.
        Product product;
        if (category.equalsIgnoreCase("Egg")) {
            String size = readString("Egg size (Small/Medium/Large/Jumbo): ");
            int perTray = readInt("Pieces per tray: ");
            product = new Egg(id, name, description, price, unit, shelfLife, size, perTray);
        } else if (category.equalsIgnoreCase("Pork")) {
            product = new Pork(id, name, description, price, unit, shelfLife);
        } else if (category.equalsIgnoreCase("Beef")) {
            product = new Beef(id, name, description, price, unit, shelfLife);
        } else {
            product = new Chicken(id, name, description, price, unit, shelfLife);
        }

        double quantity = readDouble("Initial stock quantity: ");
        double reorder = readDouble("Reorder level: ");

        system.addProduct(product, quantity, reorder);
        System.out.println("Product added.");
    }

    private static void editProduct() throws ValidationException, DataLoadException {
        String id = readString("Product ID to edit: ");
        Product p = system.findProduct(id);
        if (p == null) {
            System.out.println("[ERROR] Product not found.");
            return;
        }

        System.out.println("Editing " + p.getName() + " (press Enter to keep the current value)");
        String name = readKeep("Name", p.getName());
        String description = readKeep("Description", p.getDescription());
        double price = Double.parseDouble(readKeep("Price per unit", "" + p.getPricePerUnit()));
        String unit = readKeep("Unit", p.getUnit());
        int shelfLife = Integer.parseInt(readKeep("Shelf life (days)", "" + p.getShelfLifeDays()));
        system.editProduct(p.getId(), name, description, price, unit, shelfLife);

        // Only eggs have extra fields now.
        if (p instanceof Egg) {
            Egg egg = (Egg) p;
            String size = readKeep("Egg size", egg.getSize());
            int perTray = Integer.parseInt(readKeep("Pieces per tray", "" + egg.getPiecesPerTray()));
            system.editEggDetails(p.getId(), size, perTray);
        }
        System.out.println("Product updated.");
    }

    private static void deleteProduct() throws ValidationException, DataLoadException {
        String id = readString("Product ID to delete: ");
        if (readYesNo("Are you sure? (y/n): ")) {
            system.deleteProduct(id);
            System.out.println("Product deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private static void restock() throws ValidationException, DataLoadException {
        String id = readString("Product ID to restock: ");
        double amount = readDouble("Amount to add: ");
        system.restock(id, amount);
        System.out.println("Stock updated.");
    }

    private static void setReorderLevel() throws ValidationException, DataLoadException {
        String id = readString("Product ID: ");
        double level = readDouble("New reorder level: ");
        system.setReorderLevel(id, level);
        System.out.println("Reorder level updated.");
    }

        // PRINTING TABLES

    private static void printProducts(ArrayList<Product> list) {
        if (list.isEmpty()) {
            System.out.println("(no products found)");
            return;
        }
        // printf: %-6s = text, left-aligned, 6 wide.  %.2f = number with 2 decimals.
        System.out.printf("%-6s %-9s %-24s %9s %-7s %s%n",
                "ID", "Category", "Name", "Price", "Unit", "Details");
        for (Product p : list) {
            System.out.printf("%-6s %-9s %-24s %9.2f %-7s %s%n",
                    p.getId(), p.getCategory(), p.getName(),
                    p.getPricePerUnit(), p.getUnit(), p.getDetails());
        }
    }

    private static void printInventory(ArrayList<InventoryItem> list) {
        if (list.isEmpty()) {
            System.out.println("(no inventory items found)");
            return;
        }
        System.out.printf("%-6s %-24s %9s %-7s %8s %-11s %-11s %s%n",
                "ID", "Name", "Stock", "Unit", "Reorder", "Stocked", "Expires", "Status");
        for (InventoryItem item : list) {
            Product p = item.getProduct();
            System.out.printf("%-6s %-24s %9.2f %-7s %8.2f %-11s %-11s %s%n",
                    p.getId(), p.getName(), item.getStock(), p.getUnit(),
                    item.getReorderLevel(), item.getDateStocked(), item.getExpiryDate(),
                    getStatus(item));
        }
    }

    /** Builds the status text shown in the last column. */
    private static String getStatus(InventoryItem item) {
        String status = "";
        if (item.isExpired()) {
            status = status + "EXPIRED ";
        }
        if (item.isLowStock()) {
            status = status + "LOW-STOCK";
        }
        if (status.isEmpty()) {
            status = "OK";
        }
        return status.trim();
    }

    // INPUT HELPERS: keep asking until the user gives a valid value
    
    private static String readString(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {   // input ended (for example, a test file)
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    /** Shows the current value; if the user just presses Enter, keeps it. */
    private static String readKeep(String label, String current) {
        String text = readString(label + " [" + current + "]: ");
        if (text.isEmpty()) {
            return current;
        }
        return text;
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static boolean readYesNo(String prompt) {
        String answer = readString(prompt);
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }
}