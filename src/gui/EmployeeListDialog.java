package gui;

import controllers.SystemManager;
import models.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class EmployeeListDialog extends javax.swing.JFrame {

    public EmployeeListDialog() {
        initComponents();
        setLocationRelativeTo(null);
        loadEmployees();
    }

    private void initComponents() {
        jScrollPane = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("All Employees");
        setResizable(false);

        employeeTable.setFont(new java.awt.Font("Noto Sans", 0, 14));
        employeeTable.setRowHeight(25);
        jScrollPane.setViewportView(employeeTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }

    private void loadEmployees() {
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Username", "Password", "Register Date", "Role"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        ArrayList<Employee> employees = SystemManager.getInstance().listAllEmployees();
        for (Employee emp : employees) {
            Object[] row = {
                emp.getId(),
                emp.getName(),
                emp.getEmail(),
                emp.getPhone(),
                emp.getUserName(),
                emp.getPassword(),
                emp.getRegisterDate().split("T")[0], // Show only date part
                emp.getRole().toString()
            };
            model.addRow(row);
        }

        employeeTable.setModel(model);
    }

    // Variables declaration
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable employeeTable;
}
