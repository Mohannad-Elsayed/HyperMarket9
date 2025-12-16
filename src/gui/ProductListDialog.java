package gui;

import controllers.SystemManager;
import models.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ProductListDialog extends javax.swing.JFrame {

    public ProductListDialog() {
        initComponents();
        setLocationRelativeTo(null);
        loadProducts();
    }

    private void initComponents() {
        jScrollPane = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("All Products");
        setResizable(false);

        productTable.setFont(new java.awt.Font("Noto Sans", 0, 14));
        productTable.setRowHeight(25);
        jScrollPane.setViewportView(productTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }

    private void loadProducts() {
        String[] columnNames = {
            "ID", "Name", "Stock", "Returned", "Damaged", 
            "Price", "Deal %", "Real Price", "Min Stock", "Max Stock",
            "Added Date", "Production Date", "Expiry Date"
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        ArrayList<Product> products = SystemManager.getInstance().listProducts();
        for (Product p : products) {
            Object[] row = {
                p.getId(),
                p.getName(),
                p.getStock(),
                p.getReturnedCounter(),
                p.getDamagedCounter(),
                String.format("%.2f", p.getPrice()),
                String.format("%.1f%%", p.getDeal()),
                String.format("%.2f", p.getRealPrice()),
                p.getRecommendedQuantityRange().getMin(),
                p.getRecommendedQuantityRange().getMax(),
                p.getAddedDate().toLocalDate().toString(),
                p.getProductionDate().toLocalDate().toString(),
                p.getExpiryDate().toLocalDate().toString()
            };
            model.addRow(row);
        }

        productTable.setModel(model);
    }

    // Variables declaration
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable productTable;
}
