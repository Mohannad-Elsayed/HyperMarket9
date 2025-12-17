package gui;

import controllers.*;
import models.*;
import util.IdManager;
import util.Range;

import javax.swing.*;
import java.awt.Cursor;
import java.time.LocalDateTime;
import java.util.ArrayList;

// mohannad: implementing card layout, explained here: https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html
public class MainPage extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainPage.class.getName());

    /**
     * Creates new form MainPage
     */
    public MainPage() {
        initComponents();
        setResizable(false);
        pack();
        setLocationRelativeTo(null); // middle of the screen
        logoutLable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Employee user = SystemManager.getInstance().getCurrentUser();
        welcomLable.setText(String.format("Welcome, %s (%s)", user.getName(), user.getRole().toString()));
        setupTabVisibility();
        setupProfile();
        setupProductsTab();
        setupEmployeeTabs();
        setupOrdersTab();
        showInventoryNotifications();
    }

    void setupTabVisibility() {
        Employee user = SystemManager.getInstance().getCurrentUser();
        EmployeeRole role = user.getRole();

        // Manage Orders (index 5) - visible to sales only
        if (role != EmployeeRole.SALES) {
            mainTappedPanel.remove(orderManager);
        }

        // Add New Product (index 4) - visible to inventory only
        if (role != EmployeeRole.INVENTORY) {
            mainTappedPanel.remove(addproduct);
        }

        // Manage Products (index 3) - visible to all except admin
        if (role == EmployeeRole.ADMIN) {
            mainTappedPanel.remove(searchUpdateProducts);
        }

        // Manage Employees (index 2) - admin only
        if (role != EmployeeRole.ADMIN) {
            mainTappedPanel.remove(jPanel1);
        }

        // Add new employee (index 1) - admin only
        if (role != EmployeeRole.ADMIN) {
            mainTappedPanel.remove(addemp);
        }

        // Profile (index 0) - always visible, no removal needed
    }

    void setupProfile() {
        Employee user = SystemManager.getInstance().getCurrentUser();
        if (user.getRole() == EmployeeRole.ADMIN) {
            usernamefield.setEditable(true);
            passwordfield.setEditable(true);
        }
        idfield.setText(Integer.toString(user.getId()));
        namefield.setText(user.getName());
        emailfield.setText(user.getEmail());
        phonefield.setText(user.getPhone());
        datefield.setText(user.getRegisterDate().split("T")[0]);
        rolefield.setText(user.getRole().toString());
        usernamefield.setText(user.getUserName());
        passwordfield.setText(user.getPassword());
    }
    
    void messageDialog(String title, String message, int type) {
        JOptionPane.showMessageDialog(this,
                    message,
                    title,
                    type);
    }
    
    void logOutUtil() {
        SystemManager.getInstance().logout();
        this.dispose();
        new LoginPage().setVisible(true);
    }

    // Product currently being viewed/edited
    private Product resultProduct;

    // Order currently being viewed in Search & Return Order tab
    private Order resultOrder;

    // Current order cart for Make New Order tab
    private ArrayList<OrderItem> cart = new ArrayList<>();

    void setupProductsTab() {
        Employee user = SystemManager.getInstance().getCurrentUser();
        EmployeeRole role = user.getRole();

        // Everyone who can access products can search and list
        boolean canAccessProducts = (role == EmployeeRole.INVENTORY || role == EmployeeRole.MARKETER || role == EmployeeRole.SALES);
        
        // Inventory: Add, Delete, Update, List, Search products + manage damages/returns
        boolean isInventory = (role == EmployeeRole.INVENTORY);
        
        // Marketer: can make reports (queries) and special offers
        boolean isMarketer = (role == EmployeeRole.MARKETER);
        
        // Sales: can search and list products
        boolean isSales = (role == EmployeeRole.SALES);

        // Search and list buttons - available to Inventory, Marketer, Sales
        productsearchidButton.setEnabled(canAccessProducts);
        listallproductsButton.setEnabled(canAccessProducts);
        resetButton1.setEnabled(canAccessProducts);
        productidsearch1.setEditable(canAccessProducts);

        // Product fields - editable only by Inventory
        productname.setEditable(isInventory);
        productstock.setEditable(isInventory);
        description.setEditable(isInventory);
        pricetextbox.setEditable(isInventory);
        minstocktextbox.setEditable(isInventory);
        maxstocktextbox.setEditable(isInventory);
        productionday.setEditable(isInventory);
        productionmonth.setEditable(isInventory);
        productonyear.setEditable(isInventory);
        expiryday.setEditable(isInventory);
        expirymonth.setEditable(isInventory);
        expiryyear.setEditable(isInventory);
        
        // Returned and damaged fields - only viewable (not editable) 
        productstockreturned.setEditable(false);
        productstockdamaged.setEditable(false);
        
        // Added date and actual price are always read-only
        addeddatetextbox.setEditable(false);
        actualpricetextbox1.setEditable(false);

        // Update and Delete buttons - Inventory only
        updateProductButton.setEnabled(isInventory);
        deleteProductButton.setEnabled(isInventory);

        // Resolve damaged and returned - Inventory only
        resolveDamagedButton.setEnabled(isInventory);
        resolveReturnedButton1.setEnabled(isInventory);

        // Deal textbox - editable only by Marketer
        dealtextbox.setEditable(isMarketer);
        
        // Make special offer button - Marketer only
        makespecialofferbutton.setEnabled(isMarketer);

        // Add Product tab - Inventory only
        addproductname.setEditable(isInventory);
        addproductstock.setEditable(isInventory);
        addproductdescription1.setEditable(isInventory);
        addproductpricetextbox1.setEditable(isInventory);
        addproductminstocktextbox1.setEditable(isInventory);
        addproductmaxstocktextbox1.setEditable(isInventory);
        addproductproductionday1.setEditable(isInventory);
        addproductproductionmonth1.setEditable(isInventory);
        addproductproductonyear1.setEditable(isInventory);
        addproductexpiryday1.setEditable(isInventory);
        addproductexpirymonthh1.setEditable(isInventory);
        addproductexpiryyear1.setEditable(isInventory);
        addproductButton.setEnabled(isInventory);
    }

    void setupEmployeeTabs() {
        Employee user = SystemManager.getInstance().getCurrentUser();
        boolean isAdmin = (user.getRole() == EmployeeRole.ADMIN);

        // Add Employee Tab - Admin only
        namefield1.setEditable(isAdmin);
        emailfield1.setEditable(isAdmin);
        phonefield1.setEditable(isAdmin);
        usernamefield1.setEditable(isAdmin);
        passwordfield1.setEditable(isAdmin);
        roleselector.setEnabled(isAdmin);
        addempButton.setEnabled(isAdmin);
        showpasswordButton1.setEnabled(isAdmin);

        // Search & Update Employees Tab - Admin only
        idsearch.setEditable(isAdmin);
        usernamesearch.setEditable(isAdmin);
        searchidButton.setEnabled(isAdmin);
        searchusernameButton.setEnabled(isAdmin);
        resultname.setEditable(isAdmin);
        resultemail.setEditable(isAdmin);
        resultphone.setEditable(isAdmin);
        resultusername.setEditable(isAdmin);
        resultpasswordfield2.setEditable(isAdmin);
        resultdatefield1.setEditable(isAdmin);
        resultrolefield1.setEditable(isAdmin);
        updatempButton.setEnabled(isAdmin);
        resetButton.setEnabled(isAdmin);
        listallemployeesButton.setEnabled(isAdmin);
        deleteempButton.setEnabled(isAdmin);
        showpasswordButton2.setEnabled(isAdmin);
    }

    void setupOrdersTab() {
        Employee user = SystemManager.getInstance().getCurrentUser();
        boolean isSales = (user.getRole() == EmployeeRole.SALES);

        // Add tab change listener to populate orders table when "View All Orders" is selected
        jTabbedPane1.addChangeListener(e -> {
            int selectedIndex = jTabbedPane1.getSelectedIndex();
            String tabTitle = jTabbedPane1.getTitleAt(selectedIndex);
            if ("View All Orders".equals(tabTitle) && isSales) {
                populateOrdersTable();
            }
        });
    }

    void populateOrdersTable() {
        try {
            ArrayList<Order> orders = SystemManager.getInstance().listOrders();
            String[] columnNames = {"Order ID", "Items Count", "Total Price"};
            Object[][] data = new Object[orders.size()][3];

            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                data[i][0] = order.getId();
                data[i][1] = order.getOrderItems().size();
                data[i][2] = String.format("%.2f", order.getTotalPrice());
            }

            jTable1.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } catch (Exception e) {
            messageDialog("Error", "Unable to load orders: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    // ====== Make New Order Tab Helper Methods ======

    void resetNewOrderProductView() {
        productname1.setText("");
        productstock1.setText("");
        description1.setText("");
        actualpricetextbox2.setText("");
    }

    void showNewOrderProductView(Product product) {
        productname1.setText(product.getName());
        productstock1.setText(String.valueOf(product.getStock()));
        description1.setText(product.getDescription());
        actualpricetextbox2.setText(String.format("%.2f", product.getRealPrice()));
    }

    void resetNewOrderForm() {
        productidsearch2.setText("");
        productidsearch4.setText("");
        resetNewOrderProductView();
        cart.clear();
        updateCartTable();
    }

    void updateCartTable() {
        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Total Price"};
        Object[][] data = new Object[cart.size()][4];

        for (int i = 0; i < cart.size(); i++) {
            OrderItem item = cart.get(i);
            Product product = SystemManager.getInstance().searchProductById(item.getProductId());
            String productName = (product != null) ? product.getName() : "Unknown";
            data[i][0] = item.getProductId();
            data[i][1] = productName;
            data[i][2] = item.getQuantity();
            data[i][3] = String.format("%.2f", item.getPrice());
        }

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only quantity column (index 2) is editable
                return column == 2;
            }
        };

        jTable2.setModel(model);

        // Add table model listener to handle quantity edits
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 2) {
                int row = e.getFirstRow();
                handleCartQuantityEdit(row);
            }
        });
    }

    void handleCartQuantityEdit(int row) {
        if (row < 0 || row >= cart.size()) return;

        try {
            Object value = jTable2.getValueAt(row, 2);
            int newQuantity = Integer.parseInt(value.toString().trim());

            if (newQuantity <= 0) {
                messageDialog("Invalid Quantity", "Quantity must be a positive number.", JOptionPane.ERROR_MESSAGE);
                updateCartTable();
                return;
            }

            OrderItem oldItem = cart.get(row);
            // Check if the new quantity is available in stock
            Product product = SystemManager.getInstance().searchProductById(oldItem.getProductId());
            if (product == null) {
                messageDialog("Error", "Product no longer exists.", JOptionPane.ERROR_MESSAGE);
                cart.remove(row);
                updateCartTable();
                return;
            }

            if (newQuantity > product.getStock()) {
                messageDialog("Insufficient Stock", 
                    String.format("Only %d units available for %s.", product.getStock(), product.getName()), 
                    JOptionPane.ERROR_MESSAGE);
                updateCartTable();
                return;
            }

            // Replace with new OrderItem with updated quantity
            cart.set(row, new OrderItem(product, newQuantity));
            updateCartTable();

        } catch (NumberFormatException ex) {
            messageDialog("Invalid Input", "Please enter a valid number for quantity.", JOptionPane.ERROR_MESSAGE);
            updateCartTable();
        } catch (Exception ex) {
            messageDialog("Error", "An error occurred: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            updateCartTable();
        }
    }

    int findCartItemByProductId(int productId) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProductId() == productId) {
                return i;
            }
        }
        return -1;
    }

    // ====== Search & Return Order Tab Helper Methods ======

    void displayOrderInTable(Order order) {
        ArrayList<OrderItem> items = order.getOrderItems();
        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Total Price"};
        Object[][] data = new Object[items.size()][4];

        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            Product product = SystemManager.getInstance().searchProductById(item.getProductId());
            String productName = (product != null) ? product.getName() : "Unknown";
            data[i][0] = item.getProductId();
            data[i][1] = productName;
            data[i][2] = item.getQuantity();
            data[i][3] = String.format("%.2f", item.getPrice());
        }

        jTable3.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are read-only
            }
        });
    }

    void clearOrderSearchTable() {
        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Total Price"};
        Object[][] data = new Object[0][4];
        jTable3.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    void showInventoryNotifications() {
        Employee user = SystemManager.getInstance().getCurrentUser();
        if (user.getRole() != EmployeeRole.INVENTORY) {
            return;
        }

        try {
            ArrayList<String> notifications = SystemManager.getInstance().getInventoryNotifications();
            if (notifications.isEmpty()) {
                return;
            }

            StringBuilder message = new StringBuilder("Inventory Alerts:\n\n");
            for (String notification : notifications) {
                message.append("• ").append(notification).append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "Inventory Notifications",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            // Silently ignore if there's an issue getting notifications
        }
    }

    void resetProductSearchResult() {
        resultProduct = null;
        productname.setText("");
        productstock.setText("");
        productstockreturned.setText("");
        productstockdamaged.setText("");
        description.setText("");
        pricetextbox.setText("");
        dealtextbox.setText("");
        actualpricetextbox1.setText("");
        minstocktextbox.setText("");
        maxstocktextbox.setText("");
        addeddatetextbox.setText("");
        productionday.setText("");
        productionmonth.setText("");
        productonyear.setText("");
        expiryday.setText("");
        expirymonth.setText("");
        expiryyear.setText("");
    }

    void showProductSearchResult(Product product) {
        resultProduct = product;
        productname.setText(product.getName());
        productstock.setText(String.valueOf(product.getStock()));
        productstockreturned.setText(String.valueOf(product.getReturnedCounter()));
        productstockdamaged.setText(String.valueOf(product.getDamagedCounter()));
        description.setText(product.getDescription());
        pricetextbox.setText(String.format("%.2f", product.getPrice()));
        dealtextbox.setText(String.valueOf(product.getDeal()));
        actualpricetextbox1.setText(String.format("%.2f", product.getRealPrice()));
        minstocktextbox.setText(String.valueOf(product.getRecommendedQuantityRange().getMin()));
        maxstocktextbox.setText(String.valueOf(product.getRecommendedQuantityRange().getMax()));
        addeddatetextbox.setText(product.getAddedDate().toLocalDate().toString());
        
        // Production date
        productionday.setText(String.valueOf(product.getProductionDate().getDayOfMonth()));
        productionmonth.setText(String.valueOf(product.getProductionDate().getMonthValue()));
        productonyear.setText(String.valueOf(product.getProductionDate().getYear()));
        
        // Expiry date
        expiryday.setText(String.valueOf(product.getExpiryDate().getDayOfMonth()));
        expirymonth.setText(String.valueOf(product.getExpiryDate().getMonthValue()));
        expiryyear.setText(String.valueOf(product.getExpiryDate().getYear()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoutLable = new javax.swing.JLabel();
        welcomLable = new javax.swing.JLabel();
        mainTappedPanel = new javax.swing.JTabbedPane();
        profilePanel = new javax.swing.JPanel();
        idfield = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        namefield = new javax.swing.JTextField();
        emailfield = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        phonefield = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        datefield = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        rolefield = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        usernamefield = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        passwordfield = new javax.swing.JPasswordField();
        updateButton = new javax.swing.JButton();
        showpasswordButton = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        addemp = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        namefield1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        emailfield1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        phonefield1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        usernamefield1 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        passwordfield1 = new javax.swing.JPasswordField();
        addempButton = new javax.swing.JButton();
        roleselector = new javax.swing.JComboBox<>();
        showpasswordButton1 = new javax.swing.JButton();
        jLabel54 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        idsearch = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        usernamesearch = new javax.swing.JTextField();
        searchidButton = new javax.swing.JButton();
        searchusernameButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        resultname = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        resultemail = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        resultphone = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        resultusername = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        resultpasswordfield2 = new javax.swing.JPasswordField();
        updatempButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        resultdatefield1 = new javax.swing.JTextField();
        resultrolefield1 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        listallemployeesButton = new javax.swing.JButton();
        deleteempButton = new javax.swing.JButton();
        showpasswordButton2 = new javax.swing.JButton();
        jLabel55 = new javax.swing.JLabel();
        searchUpdateProducts = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        productidsearch1 = new javax.swing.JTextField();
        productsearchidButton = new javax.swing.JButton();
        resetButton1 = new javax.swing.JButton();
        listallproductsButton = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        productstock = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        productname = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        productstockreturned = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        productstockdamaged = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jLabel33 = new javax.swing.JLabel();
        pricetextbox = new javax.swing.JTextField();
        dealtextbox = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        minstocktextbox = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        maxstocktextbox = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        addeddatetextbox = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        productionday = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        updateProductButton = new javax.swing.JButton();
        deleteProductButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        productionmonth = new javax.swing.JTextField();
        productonyear = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        expiryday = new javax.swing.JTextField();
        expirymonth = new javax.swing.JTextField();
        expiryyear = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        actualpricetextbox1 = new javax.swing.JTextField();
        resolveDamagedButton = new javax.swing.JButton();
        makespecialofferbutton = new javax.swing.JButton();
        resolveReturnedButton1 = new javax.swing.JButton();
        addproduct = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        addproductname = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        addproductstock = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        addproductdescription1 = new javax.swing.JTextArea();
        jLabel45 = new javax.swing.JLabel();
        addproductpricetextbox1 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        addproductminstocktextbox1 = new javax.swing.JTextField();
        addproductmaxstocktextbox1 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        addproductproductionday1 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        addproductproductionmonth1 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        addproductproductonyear1 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        addproductexpiryday1 = new javax.swing.JTextField();
        addproductexpirymonthh1 = new javax.swing.JTextField();
        addproductexpiryyear1 = new javax.swing.JTextField();
        addproductButton = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        orderManager = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        productidsearch3 = new javax.swing.JTextField();
        productsearchidButton6 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        productsearchidButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        productidsearch2 = new javax.swing.JTextField();
        productsearchidButton1 = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        productname1 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        productstock1 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        description1 = new javax.swing.JTextArea();
        jLabel60 = new javax.swing.JLabel();
        actualpricetextbox2 = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        productidsearch4 = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        productsearchidButton2 = new javax.swing.JButton();
        productsearchidButton3 = new javax.swing.JButton();
        productsearchidButton4 = new javax.swing.JButton();
        productsearchidButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Hyper Market System v1.0");
        setBackground(new java.awt.Color(204, 204, 255));
        setForeground(new java.awt.Color(204, 204, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                MainPageClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoutLable.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        logoutLable.setForeground(new java.awt.Color(51, 51, 255));
        logoutLable.setText("<html><u>Log Out</u></html>");
        logoutLable.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        logoutLable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutLableMouseClicked(evt);
            }
        });
        getContentPane().add(logoutLable, new org.netbeans.lib.awtextra.AbsoluteConstraints(1086, 18, -1, -1));

        welcomLable.setFont(new java.awt.Font("Noto Sans", 3, 18)); // NOI18N
        welcomLable.setText("Welcome, ");
        getContentPane().add(welcomLable, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 18, -1, -1));

        mainTappedPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        mainTappedPanel.setToolTipText("");
        mainTappedPanel.setName(""); // NOI18N

        profilePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        profilePanel.setFont(new java.awt.Font("Noto Sans", 0, 18)); // NOI18N
        profilePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        idfield.setEditable(false);
        idfield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        idfield.setText("Id");
        idfield.setEnabled(false);
        idfield.setFocusCycleRoot(true);
        idfield.addActionListener(this::idfieldActionPerformed);
        profilePanel.add(idfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 300, -1));

        jLabel4.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel4.setText("Employee Id:");
        profilePanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        jLabel5.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel5.setText("Name:");
        profilePanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, -1, -1));

        namefield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        namefield.addActionListener(this::namefieldActionPerformed);
        profilePanel.add(namefield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 300, -1));

        emailfield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        emailfield.addActionListener(this::emailfieldActionPerformed);
        profilePanel.add(emailfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, 300, -1));

        jLabel6.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel6.setText("Email:");
        profilePanel.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, -1, -1));

        jLabel7.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel7.setText("Phone:");
        profilePanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, -1, -1));

        phonefield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        phonefield.addActionListener(this::phonefieldActionPerformed);
        profilePanel.add(phonefield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 180, 300, -1));

        jLabel8.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel8.setText("Register Date:");
        profilePanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, -1, -1));

        datefield.setEditable(false);
        datefield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        datefield.addActionListener(this::datefieldActionPerformed);
        profilePanel.add(datefield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 300, -1));

        jLabel9.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel9.setText("Role:");
        profilePanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, -1, -1));

        rolefield.setEditable(false);
        rolefield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        rolefield.addActionListener(this::rolefieldActionPerformed);
        profilePanel.add(rolefield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 280, 300, -1));

        jLabel10.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel10.setText("Username:");
        profilePanel.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 360, -1, -1));

        usernamefield.setEditable(false);
        usernamefield.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        usernamefield.addActionListener(this::usernamefieldActionPerformed);
        profilePanel.add(usernamefield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 360, 300, -1));

        jLabel11.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel11.setText("Password:");
        profilePanel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 410, -1, -1));

        passwordfield.setEditable(false);
        profilePanel.add(passwordfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 410, 300, 36));

        updateButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        updateButton.setText("Update Profile");
        updateButton.addActionListener(this::updateButtonActionPerformed);
        profilePanel.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 460, 200, 50));

        showpasswordButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showpasswordButtonshowpasswordhandler(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showpasswordButtonhidepasswordhandler(evt);
            }
        });
        showpasswordButton.addActionListener(this::showpasswordButtonActionPerformed);
        profilePanel.add(showpasswordButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 420, 20, 20));

        jLabel53.setText("Show Password");
        profilePanel.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 420, -1, -1));

        mainTappedPanel.addTab("Profile", new javax.swing.ImageIcon(getClass().getResource("/gui/media/man.png")), profilePanel, ""); // NOI18N

        jLabel12.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel12.setText("Name:");

        namefield1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        namefield1.addActionListener(this::namefield1ActionPerformed);

        jLabel13.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel13.setText("Email:");

        emailfield1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        emailfield1.addActionListener(this::emailfield1ActionPerformed);

        jLabel14.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel14.setText("Phone:");

        phonefield1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        phonefield1.addActionListener(this::phonefield1ActionPerformed);

        jLabel15.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel15.setText("Role:");

        jLabel16.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel16.setText("Username:");

        usernamefield1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        usernamefield1.addActionListener(this::usernamefield1ActionPerformed);

        jLabel17.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel17.setText("Password:");

        addempButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        addempButton.setText("Add Employee");
        addempButton.addActionListener(this::addempButtonActionPerformed);

        roleselector.setFont(new java.awt.Font("Noto Sans", 0, 18)); // NOI18N
        roleselector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "INVENTORY", "SALES", "MARKETER" }));

        showpasswordButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showpasswordButton1showpasswordhandler(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showpasswordButton1hidepasswordhandler(evt);
            }
        });
        showpasswordButton1.addActionListener(this::showpasswordButton1ActionPerformed);

        jLabel54.setText("Show Password");

        javax.swing.GroupLayout addempLayout = new javax.swing.GroupLayout(addemp);
        addemp.setLayout(addempLayout);
        addempLayout.setHorizontalGroup(
            addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addempLayout.createSequentialGroup()
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addempLayout.createSequentialGroup()
                        .addGap(294, 294, 294)
                        .addComponent(addempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addempLayout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addempLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(107, 107, 107)
                                .addComponent(namefield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(addempLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(110, 110, 110)
                                .addComponent(emailfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(addempLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(106, 106, 106)
                                .addComponent(phonefield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addempLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(121, 121, 121)
                                .addComponent(roleselector, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(addempLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(74, 74, 74)
                                .addComponent(usernamefield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(addempLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(79, 79, 79)
                                .addComponent(passwordfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(showpasswordButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel54)))
                .addContainerGap(460, Short.MAX_VALUE))
        );
        addempLayout.setVerticalGroup(
            addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addempLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(namefield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(emailfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(phonefield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(roleselector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(usernamefield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addempLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(passwordfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addempLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel54)
                            .addComponent(showpasswordButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)))
                .addComponent(addempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainTappedPanel.addTab("Add new employee", new javax.swing.ImageIcon(getClass().getResource("/gui/media/add-user.png")), addemp); // NOI18N

        jLabel18.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel18.setText("Employee Id:");

        idsearch.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        idsearch.setFocusCycleRoot(true);
        idsearch.addActionListener(this::idsearchActionPerformed);

        jLabel19.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel19.setText("Username:");

        usernamesearch.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        usernamesearch.addActionListener(this::usernamesearchActionPerformed);

        searchidButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        searchidButton.setText("Search by ID");
        searchidButton.addActionListener(this::searchidButtonActionPerformed);

        searchusernameButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        searchusernameButton.setText("Search by Username");
        searchusernameButton.addActionListener(this::searchusernameButtonActionPerformed);

        jLabel20.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel20.setText("Name:");

        resultname.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        resultname.addActionListener(this::resultnameActionPerformed);

        jLabel21.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel21.setText("Email:");

        resultemail.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        resultemail.addActionListener(this::resultemailActionPerformed);

        jLabel22.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel22.setText("Phone:");

        resultphone.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        resultphone.addActionListener(this::resultphoneActionPerformed);

        jLabel23.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel23.setText("Username:");

        resultusername.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        resultusername.addActionListener(this::resultusernameActionPerformed);

        jLabel24.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel24.setText("Password:");

        updatempButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        updatempButton.setText("Update Employee");
        updatempButton.addActionListener(this::updatempButtonActionPerformed);

        jLabel25.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel25.setText("Register Date:");

        resultdatefield1.setEditable(false);
        resultdatefield1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        resultdatefield1.addActionListener(this::resultdatefield1ActionPerformed);

        resultrolefield1.setEditable(false);
        resultrolefield1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        resultrolefield1.addActionListener(this::resultrolefield1ActionPerformed);

        jLabel26.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel26.setText("Role:");

        resetButton.setText("Reset");
        resetButton.addActionListener(this::resetButtonActionPerformed);

        listallemployeesButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        listallemployeesButton.setText("List All Employees");
        listallemployeesButton.addActionListener(this::listallemployeesButtonActionPerformed);

        deleteempButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        deleteempButton.setText("Delete Employee");
        deleteempButton.addActionListener(this::deleteempButtonActionPerformed);

        showpasswordButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showpasswordButton2showpasswordhandler(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showpasswordButton2hidepasswordhandler(evt);
            }
        });
        showpasswordButton2.addActionListener(this::showpasswordButton2ActionPerformed);

        jLabel55.setText("Show");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel25)
                        .addGap(49, 49, 49)
                        .addComponent(resultdatefield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(225, 225, 225)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel26)
                        .addGap(121, 121, 121)
                        .addComponent(resultrolefield1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(searchidButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(searchusernameButton))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel18)
                                            .addGap(58, 58, 58)
                                            .addComponent(idsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel19)
                                            .addGap(74, 74, 74)
                                            .addComponent(usernamesearch, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(154, 154, 154)
                                .addComponent(listallemployeesButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(107, 107, 107)
                                .addComponent(resultname, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(110, 110, 110)
                                .addComponent(resultemail, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(106, 106, 106)
                                .addComponent(resultphone, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(74, 74, 74)
                                .addComponent(resultusername, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(79, 79, 79)
                                .addComponent(resultpasswordfield2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showpasswordButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel55)
                .addGap(16, 16, 16))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(deleteempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(updatempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(resultname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(resultemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(resultphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(resultdatefield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26)
                                .addComponent(resetButton))
                            .addComponent(resultrolefield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel23)
                                .addComponent(listallemployeesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(resultusername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(resultpasswordfield2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel55)
                                    .addComponent(showpasswordButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updatempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteempButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(idsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(usernamesearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchidButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchusernameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        mainTappedPanel.addTab("Manage Employees", new javax.swing.ImageIcon(getClass().getResource("/gui/media/find-my-friend.png")), jPanel1); // NOI18N

        searchUpdateProducts.setToolTipText("");
        searchUpdateProducts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel27.setText("Product Id:");
        searchUpdateProducts.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 81, -1, -1));

        productidsearch1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productidsearch1.setFocusCycleRoot(true);
        productidsearch1.addActionListener(this::productidsearch1ActionPerformed);
        searchUpdateProducts.add(productidsearch1, new org.netbeans.lib.awtextra.AbsoluteConstraints(188, 81, 116, -1));

        productsearchidButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton.setText("Search");
        productsearchidButton.addActionListener(this::productsearchidButtonActionPerformed);
        searchUpdateProducts.add(productsearchidButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 131, 261, 50));

        resetButton1.setText("Reset");
        resetButton1.addActionListener(this::resetButton1ActionPerformed);
        searchUpdateProducts.add(resetButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 216, -1, -1));

        listallproductsButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        listallproductsButton.setText("List All Products");
        listallproductsButton.addActionListener(this::listallproductsButtonActionPerformed);
        searchUpdateProducts.add(listallproductsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 274, 261, 50));

        jLabel28.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel28.setText("Stock:");
        searchUpdateProducts.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 70, -1, -1));

        productstock.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productstock.addActionListener(this::productstockActionPerformed);
        searchUpdateProducts.add(productstock, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 70, 300, -1));

        jLabel29.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel29.setText("Name:");
        searchUpdateProducts.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));

        productname.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productname.addActionListener(this::productnameActionPerformed);
        searchUpdateProducts.add(productname, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 300, -1));

        jLabel30.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel30.setText("Returned:");
        searchUpdateProducts.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 68, -1, -1));

        productstockreturned.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productstockreturned.addActionListener(this::productstockreturnedActionPerformed);
        searchUpdateProducts.add(productstockreturned, new org.netbeans.lib.awtextra.AbsoluteConstraints(884, 65, 65, -1));

        jLabel31.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel31.setText("Damaged:");
        searchUpdateProducts.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 68, -1, -1));

        productstockdamaged.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productstockdamaged.addActionListener(this::productstockdamagedActionPerformed);
        searchUpdateProducts.add(productstockdamaged, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 65, 65, -1));

        jLabel32.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel32.setText("Description:");
        searchUpdateProducts.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, -1, -1));

        description.setColumns(20);
        description.setRows(5);
        jScrollPane1.setViewportView(description);

        searchUpdateProducts.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 120, 300, -1));

        jLabel33.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel33.setText("Price:");
        searchUpdateProducts.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 280, -1, -1));

        pricetextbox.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        pricetextbox.addActionListener(this::pricetextboxActionPerformed);
        searchUpdateProducts.add(pricetextbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 280, 86, -1));

        dealtextbox.setEditable(false);
        dealtextbox.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        dealtextbox.addActionListener(this::dealtextboxActionPerformed);
        searchUpdateProducts.add(dealtextbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 280, 45, -1));

        jLabel34.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel34.setText("Deal:");
        searchUpdateProducts.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 280, -1, -1));

        jLabel35.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel35.setText("%");
        searchUpdateProducts.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 280, -1, -1));

        jLabel36.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel36.setText("Min Stock:");
        searchUpdateProducts.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 330, -1, -1));

        minstocktextbox.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        minstocktextbox.addActionListener(this::minstocktextboxActionPerformed);
        searchUpdateProducts.add(minstocktextbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 330, 86, -1));

        jLabel37.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel37.setText("Max Stock:");
        searchUpdateProducts.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 330, -1, -1));

        maxstocktextbox.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        maxstocktextbox.addActionListener(this::maxstocktextboxActionPerformed);
        searchUpdateProducts.add(maxstocktextbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 330, 86, -1));

        jLabel38.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel38.setText("Added Date:");
        searchUpdateProducts.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 380, -1, -1));

        addeddatetextbox.setEditable(false);
        addeddatetextbox.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addeddatetextbox.addActionListener(this::addeddatetextboxActionPerformed);
        searchUpdateProducts.add(addeddatetextbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 380, 300, -1));

        jLabel39.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel39.setText("Production Date:");
        searchUpdateProducts.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 440, -1, -1));

        productionday.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productionday.addActionListener(this::productiondayActionPerformed);
        searchUpdateProducts.add(productionday, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 440, 49, -1));

        jLabel40.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel40.setText("Expiry Date:");
        searchUpdateProducts.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 490, -1, -1));

        updateProductButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        updateProductButton.setText("Update Product");
        updateProductButton.addActionListener(this::updateProductButtonActionPerformed);
        searchUpdateProducts.add(updateProductButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 380, 200, 50));

        deleteProductButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        deleteProductButton.setText("Delete Product");
        deleteProductButton.addActionListener(this::deleteProductButtonActionPerformed);
        searchUpdateProducts.add(deleteProductButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 460, 200, 50));

        jLabel1.setText("DD");
        searchUpdateProducts.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, -1, -1));

        productionmonth.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productionmonth.addActionListener(this::productionmonthActionPerformed);
        searchUpdateProducts.add(productionmonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 440, 49, -1));

        productonyear.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productonyear.addActionListener(this::productonyearActionPerformed);
        searchUpdateProducts.add(productonyear, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 440, 137, -1));

        jLabel2.setText("MM");
        searchUpdateProducts.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 420, -1, -1));

        jLabel3.setText("YYYY");
        searchUpdateProducts.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 420, -1, -1));

        expiryday.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        expiryday.addActionListener(this::expirydayActionPerformed);
        searchUpdateProducts.add(expiryday, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 490, 49, -1));

        expirymonth.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        expirymonth.addActionListener(this::expirymonthActionPerformed);
        searchUpdateProducts.add(expirymonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 490, 49, -1));

        expiryyear.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        expiryyear.addActionListener(this::expiryyearActionPerformed);
        searchUpdateProducts.add(expiryyear, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 490, 137, -1));

        jLabel41.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel41.setText("Actual Price:");
        searchUpdateProducts.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 240, -1, -1));

        actualpricetextbox1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        actualpricetextbox1.addActionListener(this::actualpricetextbox1ActionPerformed);
        searchUpdateProducts.add(actualpricetextbox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 230, 86, -1));

        resolveDamagedButton.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        resolveDamagedButton.setText("Resolve Damaged");
        resolveDamagedButton.setToolTipText("");
        resolveDamagedButton.addActionListener(this::resolveDamagedButtonActionPerformed);
        searchUpdateProducts.add(resolveDamagedButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 110, 160, 40));

        makespecialofferbutton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        makespecialofferbutton.setText("Make Special Offer");
        makespecialofferbutton.addActionListener(this::makespecialofferbuttonActionPerformed);
        searchUpdateProducts.add(makespecialofferbutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 270, 200, 40));

        resolveReturnedButton1.setFont(new java.awt.Font("Noto Sans", 1, 14)); // NOI18N
        resolveReturnedButton1.setText("Resolve Returned");
        resolveReturnedButton1.addActionListener(this::resolveReturnedButton1ActionPerformed);
        searchUpdateProducts.add(resolveReturnedButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 110, 160, 40));

        mainTappedPanel.addTab("Manage Products", new javax.swing.ImageIcon(getClass().getResource("/gui/media/loupe.png")), searchUpdateProducts); // NOI18N

        jLabel42.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel42.setText("Name:");

        addproductname.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductname.addActionListener(this::addproductnameActionPerformed);

        jLabel43.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel43.setText("Stock:");

        addproductstock.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductstock.addActionListener(this::addproductstockActionPerformed);

        jLabel44.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel44.setText("Description:");

        addproductdescription1.setColumns(20);
        addproductdescription1.setRows(5);
        jScrollPane2.setViewportView(addproductdescription1);

        jLabel45.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel45.setText("Price:");

        addproductpricetextbox1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductpricetextbox1.addActionListener(this::addproductpricetextbox1ActionPerformed);

        jLabel46.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel46.setText("Max Stock:");

        addproductminstocktextbox1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductminstocktextbox1.addActionListener(this::addproductminstocktextbox1ActionPerformed);

        addproductmaxstocktextbox1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductmaxstocktextbox1.addActionListener(this::addproductmaxstocktextbox1ActionPerformed);

        jLabel47.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel47.setText("Production Date:");

        jLabel48.setText("DD");

        addproductproductionday1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductproductionday1.addActionListener(this::addproductproductionday1ActionPerformed);

        jLabel49.setText("MM");

        addproductproductionmonth1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductproductionmonth1.addActionListener(this::addproductproductionmonth1ActionPerformed);

        jLabel50.setText("YYYY");

        addproductproductonyear1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductproductonyear1.addActionListener(this::addproductproductonyear1ActionPerformed);

        jLabel51.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel51.setText("Expiry Date:");

        addproductexpiryday1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductexpiryday1.addActionListener(this::addproductexpiryday1ActionPerformed);

        addproductexpirymonthh1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductexpirymonthh1.addActionListener(this::addproductexpirymonthh1ActionPerformed);

        addproductexpiryyear1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        addproductexpiryyear1.addActionListener(this::addproductexpiryyear1ActionPerformed);

        addproductButton.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        addproductButton.setText("Add Product");
        addproductButton.addActionListener(this::addproductButtonActionPerformed);

        jLabel52.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel52.setText("Min Stock:");

        javax.swing.GroupLayout addproductLayout = new javax.swing.GroupLayout(addproduct);
        addproduct.setLayout(addproductLayout);
        addproductLayout.setHorizontalGroup(
            addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addproductLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel52, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(28, 28, 28)
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addproductLayout.createSequentialGroup()
                                .addComponent(addproductminstocktextbox1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addComponent(jLabel46)
                                .addGap(34, 34, 34)
                                .addComponent(addproductmaxstocktextbox1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(addproductLayout.createSequentialGroup()
                                .addComponent(addproductpricetextbox1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addGap(57, 57, 57)
                        .addComponent(addproductname, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addComponent(jLabel43)
                        .addGap(63, 63, 63)
                        .addComponent(addproductstock, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(jLabel48)
                        .addGap(72, 72, 72)
                        .addComponent(jLabel49)
                        .addGap(96, 96, 96)
                        .addComponent(jLabel50))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addGap(7, 7, 7)
                        .addComponent(addproductproductionday1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(addproductproductionmonth1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(addproductproductonyear1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addGap(45, 45, 45)
                        .addComponent(addproductexpiryday1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(addproductexpirymonthh1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(addproductexpiryyear1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addComponent(addproductButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(167, Short.MAX_VALUE))
        );
        addproductLayout.setVerticalGroup(
            addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addproductLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel42)
                            .addComponent(addproductname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43)
                            .addComponent(addproductstock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addproductLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel44))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(addproductLayout.createSequentialGroup()
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48)
                            .addComponent(jLabel49)
                            .addComponent(jLabel50))
                        .addGap(2, 2, 2)
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addComponent(addproductproductionday1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addproductproductionmonth1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addproductproductonyear1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addComponent(addproductexpiryday1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addproductexpirymonthh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addproductexpiryyear1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addproductButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(addproductpricetextbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(addproductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(addproductminstocktextbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46)
                    .addComponent(addproductmaxstocktextbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(147, Short.MAX_VALUE))
        );

        mainTappedPanel.addTab("Add New Product", new javax.swing.ImageIcon(getClass().getResource("/gui/media/add-document.png")), addproduct); // NOI18N

        jLabel63.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel63.setText("Order Id:");

        productidsearch3.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productidsearch3.setFocusCycleRoot(true);
        productidsearch3.addActionListener(this::productidsearch3ActionPerformed);

        productsearchidButton6.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton6.setText("Search Order");
        productsearchidButton6.addActionListener(this::productsearchidButton6ActionPerformed);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(jTable3);

        productsearchidButton7.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton7.setText("Return Order");
        productsearchidButton7.addActionListener(this::productsearchidButton7ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addGap(30, 30, 30)
                        .addComponent(productidsearch3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(productsearchidButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(93, 93, 93)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productsearchidButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(productsearchidButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel63)
                                .addComponent(productidsearch3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(37, 37, 37)
                            .addComponent(productsearchidButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Search & Return Order", new javax.swing.ImageIcon(getClass().getResource("/gui/media/loupe.png")), jPanel3); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1138, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("View All Orders", new javax.swing.ImageIcon(getClass().getResource("/gui/media/sheets.png")), jPanel4); // NOI18N

        jLabel56.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel56.setText("Product Id:");

        productidsearch2.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productidsearch2.setFocusCycleRoot(true);
        productidsearch2.addActionListener(this::productidsearch2ActionPerformed);

        productsearchidButton1.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton1.setText("Add");
        productsearchidButton1.addActionListener(this::productsearchidButton1ActionPerformed);

        jLabel57.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel57.setText("Name:");

        productname1.setEditable(false);
        productname1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productname1.addActionListener(this::productname1ActionPerformed);

        jLabel58.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel58.setText("Stock:");

        productstock1.setEditable(false);
        productstock1.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productstock1.addActionListener(this::productstock1ActionPerformed);

        jLabel59.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel59.setText("Description:");

        description1.setEditable(false);
        description1.setColumns(20);
        description1.setRows(5);
        jScrollPane4.setViewportView(description1);

        jLabel60.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel60.setText("Price:");

        actualpricetextbox2.setEditable(false);
        actualpricetextbox2.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        actualpricetextbox2.addActionListener(this::actualpricetextbox2ActionPerformed);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(jTable2);

        jLabel61.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel61.setText("Quantity:");

        productidsearch4.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        productidsearch4.setFocusCycleRoot(true);
        productidsearch4.addActionListener(this::productidsearch4ActionPerformed);

        jLabel62.setFont(new java.awt.Font("Noto Sans", 2, 18)); // NOI18N
        jLabel62.setText("Current Order:");

        productsearchidButton2.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton2.setText("Cancel Order");
        productsearchidButton2.addActionListener(this::productsearchidButton2ActionPerformed);

        productsearchidButton3.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton3.setText("Place Order");
        productsearchidButton3.addActionListener(this::productsearchidButton3ActionPerformed);

        productsearchidButton4.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton4.setText("Remove Last");
        productsearchidButton4.addActionListener(this::productsearchidButton4ActionPerformed);

        productsearchidButton5.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        productsearchidButton5.setText("View");
        productsearchidButton5.addActionListener(this::productsearchidButton5ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addGap(63, 63, 63)
                                .addComponent(productstock1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel57)
                                .addGap(57, 57, 57)
                                .addComponent(productname1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel59)
                                    .addComponent(jLabel60))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(actualpricetextbox2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(91, 91, 91))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel56)
                            .addComponent(jLabel61))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productidsearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productidsearch4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(productsearchidButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(productsearchidButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(productsearchidButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(42, 42, 42)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel62)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(productsearchidButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(productsearchidButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 571, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel61)
                                .addComponent(productidsearch4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(productsearchidButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(productsearchidButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(productsearchidButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57)
                            .addComponent(productname1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel58)
                            .addComponent(productstock1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel59))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel62)
                        .addGap(3, 3, 3)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productsearchidButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productsearchidButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(actualpricetextbox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(productidsearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Make New Order", new javax.swing.ImageIcon(getClass().getResource("/gui/media/order-2.png")), jPanel2); // NOI18N

        javax.swing.GroupLayout orderManagerLayout = new javax.swing.GroupLayout(orderManager);
        orderManager.setLayout(orderManagerLayout);
        orderManagerLayout.setHorizontalGroup(
            orderManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        orderManagerLayout.setVerticalGroup(
            orderManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        mainTappedPanel.addTab("Manage Orders", new javax.swing.ImageIcon(getClass().getResource("/gui/media/order.png")), orderManager); // NOI18N

        getContentPane().add(mainTappedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 1140, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void logoutLableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutLableMouseClicked
        this.logOutUtil();
    }//GEN-LAST:event_logoutLableMouseClicked

    private void usernamefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernamefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernamefieldActionPerformed

    private void rolefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rolefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rolefieldActionPerformed

    private void datefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_datefieldActionPerformed

    private void phonefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phonefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phonefieldActionPerformed

    private void emailfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailfieldActionPerformed

    private void namefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namefieldActionPerformed

    private void idfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idfieldActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        Employee user = SystemManager.getInstance().getCurrentUser();
        String username = usernamefield.getText();
        String pass     = new String(passwordfield.getPassword());
        String newName  = namefield.getText();
        String newEmail = emailfield.getText();
        String newPhone = phonefield.getText();

        // only admin can change username and password
        if (user.getRole() != EmployeeRole.ADMIN) {
            username = user.getUserName();
            pass = user.getPassword();
        }

        if (username.isEmpty() || pass.isEmpty() || newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            messageDialog("Empty Fields" , "Please fill any empty fields.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Employee newDate = new Employee(
                user.getId(),
                newName,
                newEmail,
                newPhone,
                username,
                pass,
                user.getRegisterDate(),
                user.getRole().toString()
        );

        int opt = javax.swing.JOptionPane.showConfirmDialog(this,
                "You'll be logged out from this session.",
                "Logout Notice",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (opt == JOptionPane.YES_OPTION) {
            try {
                if (user.getRole() == EmployeeRole.ADMIN)
                    SystemManager.getInstance().updateEmployee(user.getId(), newDate);
                else {
                    SystemManager.getInstance().updateMyInfo(newName, newEmail, newPhone);
                }
                logOutUtil();
            } catch (Exception e) {
                messageDialog("Something Wrong!", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void namefield1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namefield1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namefield1ActionPerformed

    private void emailfield1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailfield1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailfield1ActionPerformed

    private void phonefield1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phonefield1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phonefield1ActionPerformed

    private void usernamefield1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernamefield1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernamefield1ActionPerformed

    private void addempButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addempButtonActionPerformed
        Employee user = SystemManager.getInstance().getCurrentUser();
        if (user.getRole() != EmployeeRole.ADMIN)
            return;
        String username = usernamefield1.getText();
        String pass     = new String(passwordfield1.getPassword());
        String newName  = namefield1.getText();
        String newEmail = emailfield1.getText();
        String newPhone = phonefield1.getText();
        String newRole  = roleselector.getSelectedItem().toString();

        if (username.isEmpty() || pass.isEmpty() || newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty() || newRole.isEmpty()) {
            messageDialog("Empty Fields" , "Please fill any empty fields.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Employee newDate = new Employee(
                IdManager.nextId(),
                newName,
                newEmail,
                newPhone,
                username,
                pass,
                LocalDateTime.now().toString(),
                newRole
        );

        try {
            SystemManager.getInstance().addEmployee(newDate);
            messageDialog("Success!", String.format("Employee added with id %d.", newDate.getId()), JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            messageDialog("Something Wrong!", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_addempButtonActionPerformed

    private void MainPageClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_MainPageClosing
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to Exit? There may be unsaved data!",
                "Exit?",
                JOptionPane.YES_NO_OPTION
        );

        if (opt == JOptionPane.YES_OPTION) {
            SystemManager.getInstance().logout();
            this.dispose();
        }
    }//GEN-LAST:event_MainPageClosing

    private void idsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idsearchActionPerformed

    private void usernamesearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernamesearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernamesearchActionPerformed

    private Employee resultUser;

    private void resetSearchResult() {
        resultUser = null;
        resultemail.setText("");
        resultusername.setText("");
        resultname.setText("");
        resultphone.setText("");
        resultpasswordfield2.setText("");
        resultdatefield1.setText("");
        resultrolefield1.setText("");
    }

    private void showSearchResult(Employee emp) {
        messageDialog("Success!", "User found!", JOptionPane.INFORMATION_MESSAGE);
        resultemail.setText(emp.getEmail());
        resultusername.setText(emp.getUserName());
        resultname.setText(emp.getName());
        resultphone.setText(emp.getPhone());
        resultpasswordfield2.setText(emp.getPassword());
        resultdatefield1.setText(emp.getRegisterDate().split("T")[0]);
        resultrolefield1.setText(emp.getRole().toString());
        resultUser = emp;
    }

    private void searchidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchidButtonActionPerformed
        resetSearchResult();

        int id;

        try {
            id = Integer.parseInt(idsearch.getText());
        } catch (Exception e) {
            messageDialog("Something Wrong!", "Error: Please enter a valid Id.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp = SystemManager.getInstance().searchEmployeeById(id);
        if (emp == null) {
            messageDialog("Something Wrong!", String.format("Error: No employee with Id %d.", id), JOptionPane.ERROR_MESSAGE);
            return;
        }

        showSearchResult(emp);
    }//GEN-LAST:event_searchidButtonActionPerformed

    private void searchusernameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchusernameButtonActionPerformed
        resetSearchResult();
        String username = usernamesearch.getText();
        username = username.trim();

        if (username.isEmpty()) {
            messageDialog("Something Wrong!", "Please enter a username.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp = SystemManager.getInstance().searchEmployeeByUsername(username);
        if (emp == null) {
            messageDialog("Something Wrong!", String.format("Error: No employee with username (%s).", username), JOptionPane.ERROR_MESSAGE);
            return;
        }

        showSearchResult(emp);
    }//GEN-LAST:event_searchusernameButtonActionPerformed

    private void resultnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultnameActionPerformed

    private void resultemailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultemailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultemailActionPerformed

    private void resultphoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultphoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultphoneActionPerformed

    private void resultusernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultusernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultusernameActionPerformed

    private void updatempButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatempButtonActionPerformed
        Employee currentUser = SystemManager.getInstance().getCurrentUser();
        if (resultUser == null) {
            messageDialog("Something Wrong!", "Please search for a user first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentUser.getId() == resultUser.getId()) {
            messageDialog("Something Wrong!", "Please, Use Profile tab instead.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = resultusername.getText();
        String pass     = new String(resultpasswordfield2.getPassword());
        String newName  = resultname.getText();
        String newEmail = resultemail.getText();
        String newPhone = resultphone.getText();

        if (username.isEmpty() || pass.isEmpty() || newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            messageDialog("Empty Fields" , "Please fill any empty fields to update.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Employee newDate = new Employee(
                resultUser.getId(),
                newName,
                newEmail,
                newPhone,
                username,
                pass,
                resultUser.getRegisterDate(),
                resultUser.getRole().toString()
        );

        int opt = javax.swing.JOptionPane.showConfirmDialog(this,
                "Confirm updating? Operation cannot be reverted.",
                "Update Notice",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                SystemManager.getInstance().updateEmployee(resultUser.getId(), newDate);
                messageDialog("Success!", "Employee updated successfully", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                messageDialog("Something Wrong!", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_updatempButtonActionPerformed

    private void resultdatefield1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultdatefield1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultdatefield1ActionPerformed

    private void resultrolefield1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultrolefield1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultrolefield1ActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        resetSearchResult();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void listallemployeesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listallemployeesButtonActionPerformed
        new EmployeeListDialog().setVisible(true);
    }//GEN-LAST:event_listallemployeesButtonActionPerformed

    private void deleteempButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteempButtonActionPerformed
        Employee currentUser = SystemManager.getInstance().getCurrentUser();
        if (resultUser == null) {
            messageDialog("Something Wrong!", "Please search for a user first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentUser.getId() == resultUser.getId()) {
            messageDialog("Something Wrong!", "You cannot delete your own account.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opt = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee? This operation cannot be reverted.",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                SystemManager.getInstance().removeEmployee(resultUser.getId());
                messageDialog("Success!", "Employee deleted successfully", JOptionPane.INFORMATION_MESSAGE);
                resetSearchResult();
            } catch (Exception e) {
                messageDialog("Something Wrong!", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteempButtonActionPerformed

    private void productidsearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productidsearch1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productidsearch1ActionPerformed

    private void productsearchidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButtonActionPerformed
        resetProductSearchResult();

        int id;
        try {
            id = Integer.parseInt(productidsearch1.getText().trim());
        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Please enter a valid product ID.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Product product = SystemManager.getInstance().searchProductById(id);
            if (product == null) {
                messageDialog("Not Found", String.format("No product with ID %d found.", id), JOptionPane.ERROR_MESSAGE);
                return;
            }
            messageDialog("Success!", "Product found!", JOptionPane.INFORMATION_MESSAGE);
            showProductSearchResult(product);
        } catch (SecurityException e) {
            messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_productsearchidButtonActionPerformed

    private void resetButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButton1ActionPerformed
        resetProductSearchResult();
        productidsearch1.setText("");
    }//GEN-LAST:event_resetButton1ActionPerformed

    private void listallproductsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listallproductsButtonActionPerformed
        try {
            new ProductListDialog().setVisible(true);
        } catch (SecurityException e) {
            messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_listallproductsButtonActionPerformed

    private void productstockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productstockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productstockActionPerformed

    private void productnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productnameActionPerformed

    private void productstockreturnedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productstockreturnedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productstockreturnedActionPerformed

    private void productstockdamagedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productstockdamagedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productstockdamagedActionPerformed

    private void pricetextboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pricetextboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pricetextboxActionPerformed

    private void dealtextboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dealtextboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dealtextboxActionPerformed

    private void minstocktextboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minstocktextboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minstocktextboxActionPerformed

    private void maxstocktextboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxstocktextboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxstocktextboxActionPerformed

    private void addeddatetextboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addeddatetextboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addeddatetextboxActionPerformed

    private void productiondayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productiondayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productiondayActionPerformed

    private void updateProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProductButtonActionPerformed
        if (resultProduct == null) {
            messageDialog("No Product Selected", "Please search for a product first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String name = productname.getText().trim();
            String desc = description.getText().trim();
            int stock = Integer.parseInt(productstock.getText().trim());
            double price = Double.parseDouble(pricetextbox.getText().trim());
            long minStock = Long.parseLong(minstocktextbox.getText().trim());
            long maxStock = Long.parseLong(maxstocktextbox.getText().trim());
            
            int prodDay = Integer.parseInt(productionday.getText().trim());
            int prodMonth = Integer.parseInt(productionmonth.getText().trim());
            int prodYear = Integer.parseInt(productonyear.getText().trim());
            
            int expDay = Integer.parseInt(expiryday.getText().trim());
            int expMonth = Integer.parseInt(expirymonth.getText().trim());
            int expYear = Integer.parseInt(expiryyear.getText().trim());

            if (name.isEmpty() || desc.isEmpty()) {
                messageDialog("Empty Fields", "Please fill all required fields.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Range range = new Range(minStock, maxStock);
            if (!range.valid()) {
                messageDialog("Invalid Range", "Min stock must be less than max stock and both must be non-negative.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDateTime productionDate = LocalDateTime.of(prodYear, prodMonth, prodDay, 0, 0);
            LocalDateTime expiryDate = LocalDateTime.of(expYear, expMonth, expDay, 0, 0);

            // Date sanity checks
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime hundredYearsAgo = now.minusYears(100);
            
            if (productionDate.isBefore(hundredYearsAgo)) {
                messageDialog("Invalid Date", "Production date cannot be more than 100 years in the past.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (productionDate.isAfter(now.plusDays(1))) {
                messageDialog("Invalid Date", "Production date cannot be in the future.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (expiryDate.isBefore(productionDate)) {
                messageDialog("Invalid Dates", "Expiry date cannot be before production date.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Product updatedProduct = new Product(
                resultProduct.getId(),
                stock,
                resultProduct.getReturnedCounter(),
                resultProduct.getDamagedCounter(),
                name,
                desc,
                price,
                resultProduct.getDeal(),
                range,
                resultProduct.getAddedDate(),
                productionDate,
                expiryDate
            );

            int opt = JOptionPane.showConfirmDialog(this,
                    "Confirm updating this product?",
                    "Update Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                SystemManager.getInstance().updateProduct(resultProduct.getId(), updatedProduct);
                messageDialog("Success!", "Product updated successfully.", JOptionPane.INFORMATION_MESSAGE);
                showProductSearchResult(updatedProduct);
            }
        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Please enter valid numeric values.", JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException e) {
            messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_updateProductButtonActionPerformed

    private void deleteProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProductButtonActionPerformed
        if (resultProduct == null) {
            messageDialog("No Product Selected", "Please search for a product first.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (SystemManager.getInstance().productInActiveOrder(resultProduct.getId())) {
            messageDialog("Couldn't delete!", "Product currently in an active order. You may delete order first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                String.format("Are you sure you want to delete product '%s' (ID: %d)?\nThis action cannot be undone.", 
                    resultProduct.getName(), resultProduct.getId()),
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                SystemManager.getInstance().removeProduct(resultProduct.getId());
                messageDialog("Success!", "Product deleted successfully.", JOptionPane.INFORMATION_MESSAGE);
                resetProductSearchResult();
                productidsearch1.setText("");
            } catch (SecurityException e) {
                messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteProductButtonActionPerformed

    private void productionmonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productionmonthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productionmonthActionPerformed

    private void productonyearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productonyearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productonyearActionPerformed

    private void expirydayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expirydayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expirydayActionPerformed

    private void expirymonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expirymonthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expirymonthActionPerformed

    private void expiryyearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expiryyearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expiryyearActionPerformed

    private void actualpricetextbox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualpricetextbox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_actualpricetextbox1ActionPerformed

    private void resolveDamagedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resolveDamagedButtonActionPerformed
        if (resultProduct == null) {
            messageDialog("No Product Selected", "Please search for a product first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (resultProduct.getDamagedCounter() == 0) {
            messageDialog("No Damaged Items", "This product has no damaged items to resolve.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                String.format("Resolve all %d damaged items for '%s'?\nThis will remove them from the damaged count.", 
                    resultProduct.getDamagedCounter(), resultProduct.getName()),
                "Resolve Damaged Items",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                SystemManager.getInstance().resolveDamagedStock(resultProduct.getId());
                messageDialog("Success!", "Damaged items resolved successfully.", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the product display
                Product refreshed = SystemManager.getInstance().searchProductById(resultProduct.getId());
                if (refreshed != null) {
                    showProductSearchResult(refreshed);
                }
            } catch (SecurityException e) {
                messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_resolveDamagedButtonActionPerformed

    private void makespecialofferbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makespecialofferbuttonActionPerformed
        if (resultProduct == null) {
            messageDialog("No Product Selected", "Please search for a product first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double deal = Double.parseDouble(dealtextbox.getText().trim());
            
            if (deal < 0 || deal > 100) {
                messageDialog("Invalid Deal", "Deal percentage must be between 0 and 100.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int opt = JOptionPane.showConfirmDialog(this,
                    String.format("Set %.1f%% discount on '%s'?\nNew price will be: %.2f", 
                        deal, resultProduct.getName(), resultProduct.getPrice() * (1 - deal/100)),
                    "Confirm Special Offer",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                SystemManager.getInstance().setProductDeal(resultProduct.getId(), deal);
                messageDialog("Success!", "Special offer applied successfully.", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the product display
                Product refreshed = SystemManager.getInstance().searchProductById(resultProduct.getId());
                if (refreshed != null) {
                    showProductSearchResult(refreshed);
                }
            }
        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Please enter a valid deal percentage.", JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException e) {
            messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_makespecialofferbuttonActionPerformed

    private void resolveReturnedButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resolveReturnedButton1ActionPerformed
        if (resultProduct == null) {
            messageDialog("No Product Selected", "Please search for a product first.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (resultProduct.getReturnedCounter() == 0) {
            messageDialog("No Returned Items", "This product has no returned items to resolve.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                String.format("Resolve all %d returned items for '%s'?\nThis will add them back to stock.", 
                    resultProduct.getReturnedCounter(), resultProduct.getName()),
                "Resolve Returned Items",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                SystemManager.getInstance().resolveReturnedStock(resultProduct.getId());
                messageDialog("Success!", "Returned items resolved and added back to stock.", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the product display
                Product refreshed = SystemManager.getInstance().searchProductById(resultProduct.getId());
                if (refreshed != null) {
                    showProductSearchResult(refreshed);
                }
            } catch (SecurityException e) {
                messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                messageDialog("Error", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_resolveReturnedButton1ActionPerformed

    private void addproductnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductnameActionPerformed

    private void addproductstockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductstockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductstockActionPerformed

    private void addproductpricetextbox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductpricetextbox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductpricetextbox1ActionPerformed

    private void addproductminstocktextbox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductminstocktextbox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductminstocktextbox1ActionPerformed

    private void addproductmaxstocktextbox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductmaxstocktextbox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductmaxstocktextbox1ActionPerformed

    private void addproductproductionday1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductproductionday1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductproductionday1ActionPerformed

    private void addproductproductionmonth1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductproductionmonth1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductproductionmonth1ActionPerformed

    private void addproductproductonyear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductproductonyear1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductproductonyear1ActionPerformed

    private void addproductexpiryday1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductexpiryday1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductexpiryday1ActionPerformed

    private void addproductexpirymonthh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductexpirymonthh1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductexpirymonthh1ActionPerformed

    private void addproductexpiryyear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductexpiryyear1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addproductexpiryyear1ActionPerformed

    private void addproductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addproductButtonActionPerformed
        Employee user = SystemManager.getInstance().getCurrentUser();
        if (user.getRole() != EmployeeRole.INVENTORY)
            return;

        String name = addproductname.getText().trim();
        String stockStr = addproductstock.getText().trim();
        String desc = addproductdescription1.getText().trim();
        String priceStr = addproductpricetextbox1.getText().trim();
        String minStockStr = addproductminstocktextbox1.getText().trim();
        String maxStockStr = addproductmaxstocktextbox1.getText().trim();
        String prodDayStr = addproductproductionday1.getText().trim();
        String prodMonthStr = addproductproductionmonth1.getText().trim();
        String prodYearStr = addproductproductonyear1.getText().trim();
        String expDayStr = addproductexpiryday1.getText().trim();
        String expMonthStr = addproductexpirymonthh1.getText().trim();
        String expYearStr = addproductexpiryyear1.getText().trim();

        if (name.isEmpty() || stockStr.isEmpty() || desc.isEmpty() || priceStr.isEmpty() ||
            minStockStr.isEmpty() || maxStockStr.isEmpty() ||
            prodDayStr.isEmpty() || prodMonthStr.isEmpty() || prodYearStr.isEmpty() ||
            expDayStr.isEmpty() || expMonthStr.isEmpty() || expYearStr.isEmpty()) {
            messageDialog("Empty Fields", "Please fill all required fields.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            double price = Double.parseDouble(priceStr);
            long minStock = Long.parseLong(minStockStr);
            long maxStock = Long.parseLong(maxStockStr);
            int prodDay = Integer.parseInt(prodDayStr);
            int prodMonth = Integer.parseInt(prodMonthStr);
            int prodYear = Integer.parseInt(prodYearStr);
            int expDay = Integer.parseInt(expDayStr);
            int expMonth = Integer.parseInt(expMonthStr);
            int expYear = Integer.parseInt(expYearStr);

            if (stock < 0) {
                messageDialog("Invalid Input", "Stock cannot be negative.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (price < 0) {
                messageDialog("Invalid Input", "Price cannot be negative.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Range range = new Range(minStock, maxStock);
            if (!range.valid()) {
                messageDialog("Invalid Range", "Min stock must be less than max stock and both must be non-negative.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDateTime productionDate = LocalDateTime.of(prodYear, prodMonth, prodDay, 0, 0);
            LocalDateTime expiryDate = LocalDateTime.of(expYear, expMonth, expDay, 0, 0);

            // Date sanity checks
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime hundredYearsAgo = now.minusYears(100);
            
            if (productionDate.isBefore(hundredYearsAgo)) {
                messageDialog("Invalid Date", "Production date cannot be more than 100 years in the past.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (productionDate.isAfter(now.plusDays(1))) {
                messageDialog("Invalid Date", "Production date cannot be in the future.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (expiryDate.isBefore(productionDate)) {
                messageDialog("Invalid Dates", "Expiry date cannot be before production date.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (expiryDate.isBefore(LocalDateTime.now())) {
                messageDialog("Invalid Dates", "Product already expired.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Product newProduct = new Product(
                    stock,
                    name,
                    desc,
                    price,
                    range,
                    productionDate,
                    expiryDate
            );

            SystemManager.getInstance().addProduct(newProduct);
            messageDialog("Success!", String.format("Product added with ID %d.", newProduct.getId()), JOptionPane.INFORMATION_MESSAGE);
            
            // Clear the form after successful addition
            addproductname.setText("");
            addproductstock.setText("");
            addproductdescription1.setText("");
            addproductpricetextbox1.setText("");
            addproductminstocktextbox1.setText("");
            addproductmaxstocktextbox1.setText("");
            addproductproductionday1.setText("");
            addproductproductionmonth1.setText("");
            addproductproductonyear1.setText("");
            addproductexpiryday1.setText("");
            addproductexpirymonthh1.setText("");
            addproductexpiryyear1.setText("");

        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Please enter valid numeric values for stock, price, and dates.", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.DateTimeException e) {
            messageDialog("Invalid Date", "Please enter valid date values.", JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException e) {
            messageDialog("Access Denied", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            messageDialog("Something Wrong!", "Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addproductButtonActionPerformed

    private void showpasswordButtonshowpasswordhandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpasswordButtonshowpasswordhandler
        passwordfield.setEchoChar((char) 0);
    }//GEN-LAST:event_showpasswordButtonshowpasswordhandler

    private void showpasswordButtonhidepasswordhandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpasswordButtonhidepasswordhandler
        passwordfield.setEchoChar('*');
    }//GEN-LAST:event_showpasswordButtonhidepasswordhandler

    private void showpasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showpasswordButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showpasswordButtonActionPerformed

    private void showpasswordButton1showpasswordhandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpasswordButton1showpasswordhandler
        passwordfield1.setEchoChar((char) 0);
    }//GEN-LAST:event_showpasswordButton1showpasswordhandler

    private void showpasswordButton1hidepasswordhandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpasswordButton1hidepasswordhandler
        passwordfield1.setEchoChar('*');
    }//GEN-LAST:event_showpasswordButton1hidepasswordhandler

    private void showpasswordButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showpasswordButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showpasswordButton1ActionPerformed

    private void showpasswordButton2showpasswordhandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpasswordButton2showpasswordhandler
        resultpasswordfield2.setEchoChar((char) 0);
    }//GEN-LAST:event_showpasswordButton2showpasswordhandler

    private void showpasswordButton2hidepasswordhandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpasswordButton2hidepasswordhandler
        resultpasswordfield2.setEchoChar('*');
    }//GEN-LAST:event_showpasswordButton2hidepasswordhandler

    private void showpasswordButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showpasswordButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showpasswordButton2ActionPerformed

    private void productidsearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productidsearch2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productidsearch2ActionPerformed

    private void productsearchidButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton1ActionPerformed
        // Add button - add item to cart with validation
        String idText = productidsearch2.getText().trim();
        String quantityText = productidsearch4.getText().trim();
        
        if (idText.isEmpty()) {
            messageDialog("Missing Input", "Please enter a Product ID.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (quantityText.isEmpty()) {
            messageDialog("Missing Input", "Please enter a quantity.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int productId = Integer.parseInt(idText);
            int quantity = Integer.parseInt(quantityText);
            
            if (quantity <= 0) {
                messageDialog("Invalid Quantity", "Quantity must be a positive number.", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Product product = SystemManager.getInstance().searchProductById(productId);
            
            if (product == null) {
                messageDialog("Product Not Found", "No product found with ID: " + productId, JOptionPane.ERROR_MESSAGE);
                resetNewOrderProductView();
                return;
            }
            
            // Show product details
            showNewOrderProductView(product);
            
            // Check if product already in cart
            int existingIndex = findCartItemByProductId(productId);
            if (existingIndex >= 0) {
                messageDialog("Item Already in Cart", 
                    "This product is already in your cart. Please edit the quantity in the table instead.", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Check stock availability
            if (quantity > product.getStock()) {
                messageDialog("Insufficient Stock", 
                    String.format("Only %d units available for %s.", product.getStock(), product.getName()), 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Add to cart
            cart.add(new OrderItem(product, quantity));
            updateCartTable();
            
            // Clear input fields for next item
            productidsearch2.setText("");
            productidsearch4.setText("");
            resetNewOrderProductView();
            
        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Product ID and Quantity must be valid numbers.", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            messageDialog("Error", "An error occurred: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_productsearchidButton1ActionPerformed

    private void productname1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productname1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productname1ActionPerformed

    private void productstock1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productstock1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productstock1ActionPerformed

    private void actualpricetextbox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualpricetextbox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_actualpricetextbox2ActionPerformed

    private void productidsearch4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productidsearch4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productidsearch4ActionPerformed

    private void productsearchidButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton2ActionPerformed
        // Cancel Order button - reset everything
        resetNewOrderForm();
    }//GEN-LAST:event_productsearchidButton2ActionPerformed

    private void productsearchidButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton3ActionPerformed
        // Place Order button - create the order
        if (cart.isEmpty()) {
            messageDialog("Empty Cart", "Please add items to your order before placing it.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Validate all items before placing order
            for (OrderItem item : cart) {
                Product product = SystemManager.getInstance().searchProductById(item.getProductId());
                if (product == null) {
                    messageDialog("Error", "A product in your cart no longer exists.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Re-check stock before placing order
                if (item.getQuantity() > product.getStock()) {
                    messageDialog("Insufficient Stock", 
                        String.format("Only %d units available for %s. Please adjust the quantity.", 
                            product.getStock(), product.getName()), 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Create the order using cart directly
            Order order = SystemManager.getInstance().createOrder(cart);
            
            messageDialog("Order Placed", 
                String.format("Order #%d has been placed successfully!\nTotal: %.2f", 
                    order.getId(), order.getTotalPrice()), 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Reset the form
            resetNewOrderForm();
            
        } catch (Exception e) {
            messageDialog("Error", "Failed to place order: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_productsearchidButton3ActionPerformed

    private void productsearchidButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton4ActionPerformed
        // Remove Last button - remove the last item from cart
        if (!cart.isEmpty()) {
            cart.remove(cart.size() - 1);
            updateCartTable();
        }
        // If cart is empty, do nothing (as per requirements)
    }//GEN-LAST:event_productsearchidButton4ActionPerformed

    private void productsearchidButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton5ActionPerformed
        // View button - show product details without checking quantity
        String idText = productidsearch2.getText().trim();
        
        if (idText.isEmpty()) {
            messageDialog("Missing Input", "Please enter a Product ID.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int productId = Integer.parseInt(idText);
            Product product = SystemManager.getInstance().searchProductById(productId);
            
            if (product == null) {
                resetNewOrderProductView();
                // No popup for view - just clear the fields
            } else {
                showNewOrderProductView(product);
            }
        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Product ID must be a valid number.", JOptionPane.ERROR_MESSAGE);
            resetNewOrderProductView();
        } catch (Exception e) {
            messageDialog("Error", "An error occurred: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            resetNewOrderProductView();
        }
    }//GEN-LAST:event_productsearchidButton5ActionPerformed

    private void productidsearch3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productidsearch3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productidsearch3ActionPerformed

    private void productsearchidButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton6ActionPerformed
        // Search Order button
        String idText = productidsearch3.getText().trim();
        
        if (idText.isEmpty()) {
            messageDialog("Missing Input", "Please enter an Order ID.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int orderId = Integer.parseInt(idText);
            Order order = SystemManager.getInstance().searchOrderById(orderId);
            
            if (order == null) {
                messageDialog("Order Not Found", "No order found with ID: " + orderId, JOptionPane.ERROR_MESSAGE);
                resultOrder = null;
                clearOrderSearchTable();
                return;
            }
            
            // Store the found order
            resultOrder = order;
            
            // Populate the table with order items
            displayOrderInTable(order);
            
            messageDialog("Order Found", 
                String.format("Order #%d found with %d item(s).\nTotal: %.2f", 
                    order.getId(), order.getOrderItems().size(), order.getTotalPrice()), 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            messageDialog("Invalid Input", "Order ID must be a valid number.", JOptionPane.ERROR_MESSAGE);
            resultOrder = null;
            clearOrderSearchTable();
        } catch (Exception e) {
            messageDialog("Error", "An error occurred: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            resultOrder = null;
            clearOrderSearchTable();
        }
    }//GEN-LAST:event_productsearchidButton6ActionPerformed

    private void productsearchidButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsearchidButton7ActionPerformed
        // Return Order button
        if (resultOrder == null) {
            messageDialog("No Order Selected", "Please search for an order first.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int orderId = resultOrder.getId();
            SystemManager.getInstance().returnOrder(orderId);
            
            messageDialog("Order Returned", 
                String.format("Order #%d has been returned successfully.", orderId), 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Reset the search
            resultOrder = null;
            productidsearch3.setText("");
            clearOrderSearchTable();
            
        } catch (Exception e) {
            messageDialog("Error", "Failed to return order: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_productsearchidButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField actualpricetextbox1;
    private javax.swing.JTextField actualpricetextbox2;
    private javax.swing.JTextField addeddatetextbox;
    private javax.swing.JPanel addemp;
    private javax.swing.JButton addempButton;
    private javax.swing.JPanel addproduct;
    private javax.swing.JButton addproductButton;
    private javax.swing.JTextArea addproductdescription1;
    private javax.swing.JTextField addproductexpiryday1;
    private javax.swing.JTextField addproductexpirymonthh1;
    private javax.swing.JTextField addproductexpiryyear1;
    private javax.swing.JTextField addproductmaxstocktextbox1;
    private javax.swing.JTextField addproductminstocktextbox1;
    private javax.swing.JTextField addproductname;
    private javax.swing.JTextField addproductpricetextbox1;
    private javax.swing.JTextField addproductproductionday1;
    private javax.swing.JTextField addproductproductionmonth1;
    private javax.swing.JTextField addproductproductonyear1;
    private javax.swing.JTextField addproductstock;
    private javax.swing.JTextField datefield;
    private javax.swing.JTextField dealtextbox;
    private javax.swing.JButton deleteProductButton;
    private javax.swing.JButton deleteempButton;
    private javax.swing.JTextArea description;
    private javax.swing.JTextArea description1;
    private javax.swing.JTextField emailfield;
    private javax.swing.JTextField emailfield1;
    private javax.swing.JTextField expiryday;
    private javax.swing.JTextField expirymonth;
    private javax.swing.JTextField expiryyear;
    private javax.swing.JTextField idfield;
    private javax.swing.JTextField idsearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JButton listallemployeesButton;
    private javax.swing.JButton listallproductsButton;
    private javax.swing.JLabel logoutLable;
    private javax.swing.JTabbedPane mainTappedPanel;
    private javax.swing.JButton makespecialofferbutton;
    private javax.swing.JTextField maxstocktextbox;
    private javax.swing.JTextField minstocktextbox;
    private javax.swing.JTextField namefield;
    private javax.swing.JTextField namefield1;
    private javax.swing.JPanel orderManager;
    private javax.swing.JPasswordField passwordfield;
    private javax.swing.JPasswordField passwordfield1;
    private javax.swing.JTextField phonefield;
    private javax.swing.JTextField phonefield1;
    private javax.swing.JTextField pricetextbox;
    private javax.swing.JTextField productidsearch1;
    private javax.swing.JTextField productidsearch2;
    private javax.swing.JTextField productidsearch3;
    private javax.swing.JTextField productidsearch4;
    private javax.swing.JTextField productionday;
    private javax.swing.JTextField productionmonth;
    private javax.swing.JTextField productname;
    private javax.swing.JTextField productname1;
    private javax.swing.JTextField productonyear;
    private javax.swing.JButton productsearchidButton;
    private javax.swing.JButton productsearchidButton1;
    private javax.swing.JButton productsearchidButton2;
    private javax.swing.JButton productsearchidButton3;
    private javax.swing.JButton productsearchidButton4;
    private javax.swing.JButton productsearchidButton5;
    private javax.swing.JButton productsearchidButton6;
    private javax.swing.JButton productsearchidButton7;
    private javax.swing.JTextField productstock;
    private javax.swing.JTextField productstock1;
    private javax.swing.JTextField productstockdamaged;
    private javax.swing.JTextField productstockreturned;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton resetButton1;
    private javax.swing.JButton resolveDamagedButton;
    private javax.swing.JButton resolveReturnedButton1;
    private javax.swing.JTextField resultdatefield1;
    private javax.swing.JTextField resultemail;
    private javax.swing.JTextField resultname;
    private javax.swing.JPasswordField resultpasswordfield2;
    private javax.swing.JTextField resultphone;
    private javax.swing.JTextField resultrolefield1;
    private javax.swing.JTextField resultusername;
    private javax.swing.JTextField rolefield;
    private javax.swing.JComboBox<String> roleselector;
    private javax.swing.JPanel searchUpdateProducts;
    private javax.swing.JButton searchidButton;
    private javax.swing.JButton searchusernameButton;
    private javax.swing.JButton showpasswordButton;
    private javax.swing.JButton showpasswordButton1;
    private javax.swing.JButton showpasswordButton2;
    private javax.swing.JButton updateButton;
    private javax.swing.JButton updateProductButton;
    private javax.swing.JButton updatempButton;
    private javax.swing.JTextField usernamefield;
    private javax.swing.JTextField usernamefield1;
    private javax.swing.JTextField usernamesearch;
    private javax.swing.JLabel welcomLable;
    // End of variables declaration//GEN-END:variables
}
