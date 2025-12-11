import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;

public class BillingSystem extends JFrame implements ActionListener, Printable {

    JLabel l1, l2, l3, title, totalLabel;
    JTextField t1, t2, t3;
    JTextArea area;
    JButton addBtn, printBtn, clearBtn, removeBtn, saveBtn;
    JScrollPane scroll;

    HashMap<String, Double> billData = new HashMap<>();
    double grandTotal = 0;

    BillingSystem() {

        title = new JLabel("D-MART Billing System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(0, 10, 700, 40);
        title.setForeground(new Color(0, 102, 204));
        add(title);

        l1 = new JLabel("Item Name:");
        l2 = new JLabel("Quantity:");
        l3 = new JLabel("Price:");

        JLabel[] labels = {l1, l2, l3};
        int y = 80;

        for (JLabel lb : labels) {
            lb.setBounds(40, y, 120, 30);
            lb.setFont(new Font("Arial", Font.BOLD, 16));
            add(lb);
            y += 50;
        }
        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        JTextField[] fields = {t1, t2, t3};

        y = 80;
        for (JTextField tf : fields) {
            tf.setBounds(160, y, 180, 30);
            tf.setBorder(new LineBorder(Color.GRAY, 1));
            add(tf);
            y += 50;
        }
        area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);

        scroll = new JScrollPane(area);
        scroll.setBounds(360, 60, 310, 330);
        scroll.setBorder(new LineBorder(Color.BLACK, 2));
        add(scroll);

        addBtn = new JButton("ADD ITEM");
        removeBtn = new JButton("REMOVE ITEM");
        clearBtn = new JButton("CLEAR ALL");
        saveBtn = new JButton("SAVE BILL");
        printBtn = new JButton("PRINT");

        JButton[] btns = {addBtn, removeBtn, clearBtn, saveBtn, printBtn};

        y = 240;
        for (JButton b : btns) {
            b.setBounds(40, y, 300, 35);
            b.setFont(new Font("Arial", Font.BOLD, 15));
            b.setBackground(new Color(0, 153, 153));
            b.setForeground(Color.WHITE);
            b.addActionListener(this);
            add(b);
            y += 45;
        }

       
        totalLabel = new JLabel("TOTAL : ₹0.00", JLabel.CENTER);
        totalLabel.setBounds(360, 400, 310, 40);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setForeground(Color.RED);
        add(totalLabel);

        resetBillHeader();
        setLayout(null);
        setTitle("Billing System");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void resetBillHeader() {
        grandTotal = 0;
        billData.clear();
        area.setText(
                "==============================\n" +
                "         D-MART STORE\n" +
                "     Contact: 1236547890\n" +
                "     Address: Mumbai, India\n" +
                "==============================\n" +
                String.format("%-15s %-10s %-10s\n", "Item", "Qty", "Amount") +
                "----------------------------------------------\n"
        );
        updateTotal();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) addItem();
        else if (e.getSource() == removeBtn) removeItem();
        else if (e.getSource() == clearBtn) resetBillHeader();
        else if (e.getSource() == saveBtn) saveBill();
        else if (e.getSource() == printBtn) {
            try {
                area.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print Error!");
            }
        }
    }

    void addItem() {
        try {
            String item = t1.getText();
            int qty = Integer.parseInt(t2.getText());
            double price = Double.parseDouble(t3.getText());
            double amount = qty * price;

            billData.put(item, amount);
            grandTotal += amount;

            area.append(String.format("%-15s %-10d %-10.2f\n", item, qty, amount));
            updateTotal();

            t1.setText("");
            t2.setText("");
            t3.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter valid details!");
        }
    }
    void removeItem() {
        String item = JOptionPane.showInputDialog(this, "Enter item name to remove:");
        if (billData.containsKey(item)) {
            double amt = billData.get(item);
            grandTotal -= amt;
            billData.remove(item);
            refreshBillArea();
            JOptionPane.showMessageDialog(this, "Item removed!");
        } else {
            JOptionPane.showMessageDialog(this, "Item not found!");
        }
    }

    void refreshBillArea() {
        resetBillHeader();
        for (String key : billData.keySet()) {
            double amt = billData.get(key);
            area.append(String.format("%-15s %-10s %-10.2f\n", key, "-", amt));
        }
        updateTotal();
    }

    void updateTotal() {
        DecimalFormat df = new DecimalFormat("0.00");
        totalLabel.setText("TOTAL : ₹" + df.format(grandTotal));
    }

    void saveBill() {
        try {
            FileWriter fw = new FileWriter("Bill.txt");
            fw.write(area.getText() +
                    "\n----------------------------------------------\n" +
                    "GRAND TOTAL: ₹" + grandTotal);
            fw.close();
            JOptionPane.showMessageDialog(this, "Bill saved successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving bill!");
        }
    }
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        area.printAll(g);

        return PAGE_EXISTS;
    }

    public static void main(String[] args) {
        new BillingSystem();
    }
}