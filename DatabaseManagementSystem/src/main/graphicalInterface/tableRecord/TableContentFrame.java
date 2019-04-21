package main.graphicalInterface.tableRecord;

import main.exception.FieldValueNotSet;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.PersistenceActionListener;
import main.model.Column;
import main.model.DatabaseManagementSystem;
import main.model.Field;
import main.model.Table;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static main.graphicalInterface.GIConstants.ENABLE_BUTTON_TABLE_ToolTipText;
import static main.graphicalInterface.GIConstants.RECORDS_TITLE;

public class TableContentFrame extends JPanel {

    private static TableContentFrame tableContentFrame;
    private String selectedTable;
    private String selectedDatabase;
    private DatabaseManagementSystem databaseManagementSystem;

    private JLabel titleLabel;
    private JButton btnSelect;
    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JTable tableContent;
    private JScrollPane scrollPane;
    private TableModel myTableModel;

    public static TableContentFrame getInstance() {

        if (tableContentFrame == null) {

            tableContentFrame = new TableContentFrame();
        }

        return tableContentFrame;
    }

    private TableContentFrame() {

        this.setLayout(null);
        this.setBounds(0, 40, 350, 750);

        databaseManagementSystem = DatabaseManagementSystem.getInstance();

        /*
        TITLE
         */
        titleLabel = new JLabel(RECORDS_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBounds(0, 20, 350, 25);

        /*
        TABLE RECORDS
         */
        tableContent = new JTable();
        myTableModel = new TableModel();
        populateTable();

        scrollPane = new JScrollPane(tableContent);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 45, 350, 400);
        scrollPane.setViewportView(tableContent);

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
        this.add(scrollPane);
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
        btnUpdate.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);

        btnDelete.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);

    }

    class SelectListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

        }
    }

    class InsertListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

        }
    }

    class UpdateListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

        }
    }

    class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            //int index = tablesList.getSelectedIndex();
            boolean isSthSelected = false;
            for (int i = 0; i < tableContent.getRowCount() && !isSthSelected; i++) {

                if (tableContent.getValueAt(i, 0).equals(Boolean.TRUE)) {

                    isSthSelected = true;
                }
            }

            String titleConfirmDelete = "Confirm Delete Records";
            String msgConfirmDelete = "Are you sure you want to delete selected records from \"" + selectedTable + "\" Table from \"" + selectedDatabase + "\" Database ?";

            ConfirmDialog deleteDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
            boolean delete = deleteDialog.confirm();

            if (delete) {

                // save selected rows in a list
                List<Integer> selectedRows = new ArrayList<>();
                for (int i = 0; i < tableContent.getModel().getRowCount(); i++) {

                    if (tableContent.getValueAt(i, 0).equals(Boolean.TRUE)) {

                        selectedRows.add(Integer.valueOf(i));
                    }
                }

                //go through selected rows list
                // remove row from table model - first element from selected rows list
                // decrement list values (because values had changed, one value was deleted)
                while (selectedRows.size() > 0) {

                    databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable).deleteRow(selectedRows.get(0));
                    //decrement the rest of the index

                    for (int i = 0; i < selectedRows.size(); i++) {

                        selectedRows.set(i, selectedRows.get(i) - 1);
                    }
                    selectedRows.remove(0);

                }

                //refresh the table content
                setSelectedTable(selectedTable);
            }

            if (tableContent.getRowCount() == 0) { //No records left, disable update,delete buttons

                disableUpdateDeleteButtons();
            }
        }
    }


    private void populateTable() {

        myTableModel.fireTableDataChanged();
        if (selectedDatabase != null && selectedTable != null) {

            try {
                myTableModel = new TableModel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (FieldValueNotSet fieldValueNotSet) {

                fieldValueNotSet.printStackTrace();
            }

            tableContent = new JTable(myTableModel);
            tableContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
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

        populateTable();
        scrollPane.setViewportView(tableContent);
    }

    public void setSelectedDatabase(String selectedDatabase) {

        this.selectedDatabase = selectedDatabase;
    }

    public void setAreRowsSelected(boolean areRowsSelected) {

        if (areRowsSelected) {

            enableDeleteUpdateButtons();
        } else {

            disableUpdateDeleteButtons();
        }
    }
}
