package cashregister;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    static Scanner scan = new Scanner(System.in);
    static ArrayList<User> users = new ArrayList<>();

    static class Product {
        String name;
        double price;
        int quantity;

        public Product(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return price * quantity;
        }
    }

    static class User {
        String username;
        String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static void signup() {
        String username, password;
        while (true) {
            System.out.println("\n-------------------- Sign Up --------------------");
            System.out.println(" ");
            System.out.print("Enter a username (5-15 alphanumeric characters): ");
            username = scan.nextLine();
            if (!Pattern.matches("^[a-zA-Z0-9]{5,15}$", username)) {
                System.out.println("Invalid username. Please try again.");
                continue;
            }
            System.out.println(" ");
            System.out.print("Enter a password (8-20 characters, at least one uppercase letter and one number): ");
            password = scan.nextLine();
            if (!Pattern.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$", password)) {
                System.out.println("Invalid password. Please try again.");
                continue;
            }

            users.add(new User(username, password));
            System.out.println("Signup successful!");
            break;
        }
    }

    public static User login() {
        String username, password;
        while (true) {
            System.out.println("\n-------------------- Log In --------------------");
            System.out.println(" ");
            System.out.print("Enter your username: ");
            username = scan.nextLine();
            System.out.println(" ");
            System.out.print("Enter your password: ");
            password = scan.nextLine();

            for (User  user : users) {
                if (user.username.equals(username) && user.password.equals(password)) {
                    System.out.println("Login successful!");
                    return user;
                }
            }
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    public static void displayMenu() {
        System.out.println("\n|----------------------- Planes and Brews! -------------------|");
        System.out.println("                              Menu:                ");
        System.out.println("                             Drinks:              ");
        System.out.println("                     Caramel Machiato | $5 ");
        System.out.println("                       Salted Caramel | $6 ");
        System.out.println("                 Strawberry Cheesecake Frappe | $8");
        System.out.println("                      Iced Americano | $4 ");
        System.out.println("                            Sides:              ");
        System.out.println("                        Cookies | $4");
        System.out.println("                      Carrot Cake | $9       ");
        System.out.println("                 Blueberry Cheesecake | $10");
        System.out.println("                       Cupcake | $7   ");
        System.out.println("|-------------------------------------------------------------|");
    }

    public static void addProductOrder(ArrayList<Product> menuOrder) {
        System.out.print("Enter the product name to add: ");
        String name = scan.nextLine();
        System.out.print("Enter price: ");
        double price = scan.nextDouble();
        System.out.print("Enter quantity: ");
        int quantity = scan.nextInt();
        scan.nextLine();

        boolean found = false;
        for (Product p : menuOrder) {
            if (p.name.equalsIgnoreCase(name)) {
                p.quantity += quantity;
                p.price = price;
                found = true;
                break;
            }
        }

        if (!found) {
            menuOrder.add(new Product(name, price, quantity));
        }
        System.out.println("Product added/updated successfully!");
    }

    public static void updateOrderQuantity(ArrayList<Product> menuOrder) {
        if (menuOrder.isEmpty()) {
            System.out.println("No orders to update.");
            return;
        }
        displayOrders(menuOrder);
        System.out.print("Enter the product name to update quantity: ");
        String name = scan.nextLine();

        for (Product p : menuOrder) {
            if (p.name.equalsIgnoreCase(name)) {
                System.out.print("Enter new quantity: ");
                int newQty = scan.nextInt();
                scan.nextLine();
                if (newQty > 0) {
                    p.quantity = newQty;
                    System.out.println("Quantity updated successfully.");
                } else {
                    System.out.println("Quantity must be positive.");
                }
                return;
            }
        }
        System.out.println("Product not found in orders.");
    }

    public static void removeOrder(ArrayList<Product> menuOrder) {
        if (menuOrder.isEmpty()) {
            System.out.println("No orders to remove.");
            return;
        }
        displayOrders(menuOrder);
        System.out.print("Enter the product name to remove: ");
        String name = scan.nextLine();

        Product toRemove = null;
        for (Product p : menuOrder) {
            if (p.name.equalsIgnoreCase(name)) {
                toRemove = p;
                break;
            }
        }
        if (toRemove != null) {
            menuOrder.remove(toRemove);
            System.out.println("Product removed successfully.");
        } else {
            System.out.println("Product not found in orders.");
        }
    }

    public static void displayOrders(ArrayList<Product> menuOrder) {
        if (menuOrder.isEmpty()) {
            System.out.println("No orders currently.");
            return;
        }
        System.out.println("Current orders:");
        System.out.println("--------------------------------------------");
        System.out.printf("%-25s %8s %10s%n", "Product", "Quantity", "Price");
        System.out.println("--------------------------------------------");
        for (Product p : menuOrder) {
            System.out.printf("%-25s %8d $%9.2f%n", p.name, p.quantity, p.getTotalPrice());
        }
        System.out.println("--------------------------------------------");
    }

    public static double totalProduct(ArrayList<Product> menuOrder) {
        double total = 0;
        for (Product product : menuOrder) {
            total += product.getTotalPrice();
        }
        return total;
    }

    public static void logTransaction(User user, ArrayList<Product> menuOrder, double total) {
        StringBuilder transactionDetails = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        transactionDetails.append("Date: ").append(formatter.format(date)).append("\n");
        transactionDetails.append("Username: ").append(user.username).append("\n");
        transactionDetails.append("Items Purchased:\n");

        for (Product product : menuOrder) {
            transactionDetails.append(product.name)
                    .append(" | Quantity: ").append(product.quantity)
                    .append(" | Price: $").append(String.format("%.2f", product.getTotalPrice())).append("\n");
        }

        transactionDetails.append("Total Amount: $").append(String.format("%.2f", total)).append("\n");
        transactionDetails.append("---------------------------------------------------\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write(transactionDetails.toString());
        } catch (IOException e) {
            System.out.println("An error occurred while logging the transaction.");
            e.printStackTrace();
        }
    }

    public static void showTransactionHistory() {
        System.out.println("\nTransaction History (from transactions.txt):");
        System.out.println("---------------------------------------------------");
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            boolean emptyFile = true;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                emptyFile = false;
            }
            if (emptyFile) {
                System.out.println("[No transactions found]");
            }
        } catch (IOException e) {
            System.out.println("No transaction history file found or unable to read it.");
        }
        System.out.println("---------------------------------------------------\n");
    }

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n------------------- System ------------------- ");
            System.out.println("1. Sign Up");
            System.out.println("2. Log In");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");
            String option = scan.nextLine();

            switch (option) {
                case "1":
                    signup();
                    break;
                case "2":
                    User loggedInUser  = login();
                    System.out.println("-----------------------------------------------------------------");
                    System.out.println("Welcome, " + loggedInUser .username + " to Planes and Brews!");
                    System.out.println("-----------------------------------------------------------------");

                    ArrayList<Product> menuOrder = new ArrayList<>();
                    boolean ordering = true;
                    while (ordering) {
                        displayMenu();
                        System.out.println("\n --------------- Select an Option ---------------");
                        System.out.println("1. Add a product");
                        System.out.println("2. Update quantity of a product");
                        System.out.println("3. Remove a product");
                        System.out.println("4. Display current orders");
                        System.out.println("5. Checkout");
                        System.out.print("Kindly enter your choice (1-5): ");
                        String choice = scan.nextLine();

                        switch (choice) {
                            case "1":
                                addProductOrder(menuOrder);
                                break;
                            case "2":
                                updateOrderQuantity(menuOrder);
                                break;
                            case "3":
                                removeOrder(menuOrder);
                                break;
                            case "4":
                                displayOrders(menuOrder);
                                break;
                            case "5":
                                if (menuOrder.size() < 4) {
                                    System.out.println("You need to add at least 4 products before checkout. Please add more.");
                                } else {
                                    ordering = false;
                                }
                                break;
                            default:
                                System.out.println("Invalid choice. Please select from 1-5.");
                        }
                    }

                    if (menuOrder.isEmpty()) {
                        System.out.println("No orders to checkout. Exiting.");
                        break;
                    }

                    displayOrders(menuOrder);
                    double total = totalProduct(menuOrder);
                    System.out.println("-----------------------------------------------------------------");
                    System.out.printf("Total price: $%.2f%n", total);

                    System.out.print("Enter payment amount: ");
                    double payment;
                    while (true) {
                        if (scan.hasNextDouble()) {
                            payment = scan.nextDouble();
                            scan.nextLine();
                            break;
                        } else {
                            System.out.print("Please enter a valid numeric payment amount: ");
                            scan.nextLine();
                        }
                    }

                    if (payment >= total) {
                        double change = payment - total;
                        System.out.printf("Change: $%.2f%n", change);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        System.out.println("Transaction date and time: " + formatter.format(date));
                        logTransaction(loggedInUser , menuOrder, total);
                    } else {
                        System.out.println("Insufficient payment. Your order will be cancelled.");
                    }
                    System.out.println("Thank you for coming to Planes and Brews! See you again soon!");
                    System.out.println("-----------------------------------------------------------------");
                    break;
                case "3":
                    running = false;
                    System.out.println("Exiting the application. Goodbye!");
                    showTransactionHistory();
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-3.");
            }
        }
    }
}
