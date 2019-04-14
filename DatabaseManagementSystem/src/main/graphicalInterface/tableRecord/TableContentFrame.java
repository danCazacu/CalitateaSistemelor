package main.graphicalInterface.tableRecord;

import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;
import main.model.DatabaseManagementSystem;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static main.graphicalInterface.GIConstants.ENABLE_BUTTON_ToolTipText;
import static main.graphicalInterface.GIConstants.RECORDS_TITLE;

public class TableContentFrame  extends JPanel {

    private static TableContentFrame tableContentFrame;
    private String selectedTable;
    private DatabaseManagementSystem databaseManagementSystem;

    private JLabel titleLabel;
    private JButton btnSelect;
    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnDelete;

    public static TableContentFrame getInstance(){

         if(tableContentFrame == null){

            tableContentFrame = new TableContentFrame();
        }

        return tableContentFrame;
    }

    private TableContentFrame(){

        this.setLayout(null);
        this.setBounds(0, 40, 350, 750);

        databaseManagementSystem = DatabaseManagementSystem.getInstance();

        /*
        TITLE
         */
        titleLabel = new JLabel(RECORDS_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBounds(0, 20, 350, 20);

        /*
        TABLE RECORDS
         */


        /*
        BUTTONS
         */
        btnSelect = new JButton();
        btnSelect.setText("SELECT");
        btnSelect.setBounds(90, 490, 200, 50);
        btnSelect.addActionListener(new SelectListener());

        btnInsert = new JButton();
        btnInsert.setText("INSERT");
        btnInsert.setBounds(90, 550, 200, 50);
        btnInsert.addActionListener(new InsertListener());

        btnUpdate = new JButton();
        btnUpdate.setText("UPDATE");
        btnUpdate.setBounds(90, 610, 200, 50);
        btnUpdate.addActionListener(new UpdateListener());

        btnDelete = new JButton();
        btnDelete.setText("DELETE");
        btnDelete.setBounds(90, 670, 200, 50);
        btnDelete.addActionListener(new DeleteListener());

        //default Update and Delete Buttons are disabled
        disableUpdateDeleteButtons();

        addPanelObjects();
    }

    private void addPanelObjects() {

        this.add(titleLabel);
        this.add(btnSelect);
        this.add(btnInsert);
        this.add(btnUpdate);
        this.add(btnDelete);
    }

    private void enableDeleteUpdateButtons() {

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void disableUpdateDeleteButtons() {

        btnUpdate.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_ToolTipText);

        btnDelete.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_ToolTipText);

    }

    class SelectListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }

    class InsertListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }

    class UpdateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }

    class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }


    private void populateList() {


    }

    public void setSelectedTable(String selectedTable) {

        this.selectedTable = selectedTable;
        String title = "";
        if (this.selectedTable != null) {

            title = this.selectedTable + "'s Records";
        } else {

            title = RECORDS_TITLE;
        }

        this.titleLabel.setText(title);
        populateList();
    }
}
