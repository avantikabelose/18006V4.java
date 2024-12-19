import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class IceCreamClientGUI extends JFrame {
    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the JDBC driver
            connection = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com:3306/sql12752861", "sql12752861", "pLwAVlV8ZI");
            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    private ArrayList<String> selectedFlavors = new ArrayList<>();
    private ArrayList<Double> selectedFlavorPrices = new ArrayList<>();
    private ArrayList<String> selectedToppings = new ArrayList<>();
    private ArrayList<Double> selectedToppingPrices = new ArrayList<>();
    private int numScoops = 0, numCups = 0, numCones = 0, numWaffles = 0;

    private final String[] flavors = {"Vanilla", "Chocolate", "Strawberry", "Butterscotch", "Mint", "Mango", "Coffee", "Black Currant", "Bubble Gum", "Cookies & Cream"};
    private final double[] flavorPrices = {100, 200, 150, 200, 120, 180, 160, 220, 190, 250};

    private final String[] toppings = {"Nuts", "Sprinkles", "Chocolate Chips", "Caramel", "Whipped Cream", "Cherry Crush", "Oreo Crumbles", "Jelly", "Fruits", "Coconut Flakes"};
    private final double[] toppingPrices = {75, 55, 60, 45, 70, 75, 65, 50, 70, 55};

    public IceCreamClientGUI() {
        setTitle("Ice Cream Parlour");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
        JPanel buttonPanel = new JPanel();

        // Flavor Selection Panel
        JPanel flavorPanel = new JPanel(new GridLayout(flavors.length + 1, 1));
        flavorPanel.setBorder(BorderFactory.createTitledBorder("Select Flavors"));
        ButtonGroup flavorGroup = new ButtonGroup();

        for (int i = 0; i < flavors.length; i++) {
            JCheckBox flavorCheckbox = new JCheckBox(flavors[i] + " (" + flavorPrices[i] + " Rs)");
            int index = i;
            flavorCheckbox.addActionListener(e -> {
                if (flavorCheckbox.isSelected()) {
                    selectedFlavors.add(flavors[index]);
                    selectedFlavorPrices.add(flavorPrices[index]);
                } else {
                    selectedFlavors.remove(flavors[index]);
                    selectedFlavorPrices.remove((Double) flavorPrices[index]);
                }
            });
            flavorPanel.add(flavorCheckbox);
        }

        // Topping Selection Panel
        JPanel toppingPanel = new JPanel(new GridLayout(toppings.length + 1, 1));
        toppingPanel.setBorder(BorderFactory.createTitledBorder("Select Toppings"));

        for (int i = 0; i < toppings.length; i++) {
            JCheckBox toppingCheckbox = new JCheckBox(toppings[i] + " (" + toppingPrices[i] + " Rs)");
            int index = i;
            toppingCheckbox.addActionListener(e -> {
                if (toppingCheckbox.isSelected()) {
                    selectedToppings.add(toppings[index]);
                    selectedToppingPrices.add(toppingPrices[index]);
                } else {
                    selectedToppings.remove(toppings[index]);
                    selectedToppingPrices.remove((Double) toppingPrices[index]);
                }
            });
            toppingPanel.add(toppingCheckbox);
        }

        // Quantity Panel
        JPanel quantityPanel = new JPanel(new GridLayout(5, 2));
        quantityPanel.setBorder(BorderFactory.createTitledBorder("Set Quantities"));

        JTextField scoopField = new JTextField("0");
        JTextField cupField = new JTextField("0");
        JTextField coneField = new JTextField("0");
        JTextField waffleField = new JTextField("0");

        quantityPanel.add(new JLabel("Scoops (20 Rs each):"));
        quantityPanel.add(scoopField);
        quantityPanel.add(new JLabel("Cups (30 Rs each):"));
        quantityPanel.add(cupField);
        quantityPanel.add(new JLabel("Cones (40 Rs each):"));
        quantityPanel.add(coneField);
        quantityPanel.add(new JLabel("Waffles (50 Rs each):"));
        quantityPanel.add(waffleField);

        mainPanel.add(flavorPanel);
        mainPanel.add(quantityPanel);
        mainPanel.add(toppingPanel);

        // Button Panel
        JButton calculateButton = new JButton("Calculate Total");
        JButton resetButton = new JButton("Reset");

        calculateButton.addActionListener(e -> {
            try {
                // Parse quantities as integers
                numScoops = Integer.parseInt(scoopField.getText());
                numCups = Integer.parseInt(cupField.getText());
                numCones = Integer.parseInt(coneField.getText());
                numWaffles = Integer.parseInt(waffleField.getText());

                // Validation: Check for negative or decimal values
                if (numScoops < 0 || numCups < 0 || numCones < 0 || numWaffles < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop further processing
                }

                if (!isInteger(scoopField.getText()) || !isInteger(cupField.getText()) || !isInteger(coneField.getText()) || !isInteger(waffleField.getText())) {
                    JOptionPane.showMessageDialog(this, "Please enter valid integer values for quantities!", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop further processing
                }

                double total = 0;

                // Add the prices of selected flavors and toppings
                for (double price : selectedFlavorPrices) total += price;
                for (double price : selectedToppingPrices) total += price;

                // Add prices for scoops, cups, cones, and waffles
                total += numScoops * 20 + numCups * 30 + numCones * 40 + numWaffles * 50;

                // Show the receipt
                JOptionPane.showMessageDialog(this, generateReceipt(total), "Receipt", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity entered!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        resetButton.addActionListener(e -> {
            selectedFlavors.clear();
            selectedFlavorPrices.clear();
            selectedToppings.clear();
            selectedToppingPrices.clear();
            scoopField.setText("0");
            cupField.setText("0");
            coneField.setText("0");
            waffleField.setText("0");
        });

        buttonPanel.add(calculateButton);
        buttonPanel.add(resetButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper method to check if the input string is a valid integer
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String generateReceipt(double total) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("----- Receipt -----\n");
        receipt.append("Flavors:\n");
        for (int i = 0; i < selectedFlavors.size(); i++) {
            receipt.append(String.format("%d. %s (%.2f Rs)\n", i + 1, selectedFlavors.get(i), selectedFlavorPrices.get(i)));
        }
        receipt.append("Toppings:\n");
        for (int i = 0; i < selectedToppings.size(); i++) {
            receipt.append(String.format("%d. %s (%.2f Rs)\n", i + 1, selectedToppings.get(i), selectedToppingPrices.get(i)));
        }
        receipt.append(String.format("Scoops: %d (%.2f Rs)\n", numScoops, numScoops * 20.0));
        receipt.append(String.format("Cups: %d (%.2f Rs)\n", numCups, numCups * 30.0));
        receipt.append(String.format("Cones: %d (%.2f Rs)\n", numCones, numCones * 40.0));
        receipt.append(String.format("Waffles: %d (%.2f Rs)\n", numWaffles, numWaffles * 50.0));
        receipt.append(String.format("Total: %.2f Rs\n", total));
        receipt.append("-------------------\n");
        return receipt.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IceCreamClientGUI().setVisible(true));
    }
}
