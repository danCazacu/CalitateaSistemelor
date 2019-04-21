package main.graphicalInterface.tableRecord;

import main.exception.ColumnAlreadyExists;
import main.exception.DoesNotExist;
import main.exception.FieldValueNotSet;
import main.exception.InvalidValue;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.PersistenceActionListener;
import main.model.Column;
import main.model.DatabaseManagementSystem;
import main.model.FieldComparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
    private JButton btnInsertColumn;
    private JButton btnInsertRecord;
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

        btnInsertColumn = new JButton();
        btnInsertColumn.setText("INSERT COLUMN");
        btnInsertColumn.setBounds(20, 550, 150, 50);
        btnInsertColumn.addActionListener(new InsertColumnListener());

        btnInsertRecord = new JButton();
        btnInsertRecord.setText("INSERT RECORD");
        btnInsertRecord.setBounds(185, 550, 150, 50);
        btnInsertRecord.addActionListener(new InsertRecordListener());

        btnUpdate = new JButton();
        btnUpdate.setText("UPDATE");
        btnUpdate.setBounds(90, 610, 200, 50);
        btnUpdate.addActionListener(new UpdateListener());

        btnDelete = new JButton();
        btnDelete.setText("DELETE");
        btnDelete.setBounds(90, 670, 200, 50);
        btnDelete.addActionListener(new DeleteListener());

        //default all buttons are disabled
        disableButtonsWithoutDelete();
        disableDeleteButton();

        addPanelObjects();
    }

    private void addPanelObjects() {

        this.add(titleLabel);
        this.add(scrollPane);
        this.add(btnSelect);
        this.add(btnInsertColumn);
        this.add(btnInsertRecord);
        this.add(btnUpdate);
        this.add(btnDelete);
    }

    private void enableDeleteButton() {

        btnDelete.setEnabled(true);
    }

    private void disableDeleteButton() {

        btnDelete.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);

    }

    class SelectListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            SelectPanel selectPanel = null;
            try {
                selectPanel = new SelectPanel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist ignored) {
               // doesNotExist.printStackTrace();
            }

            String title = "Please select columns and optionally add where clause";
            int messageType = JOptionPane.QUESTION_MESSAGE;

            int result = JOptionPane.showConfirmDialog(null, selectPanel,
                    title, JOptionPane.OK_CANCEL_OPTION, messageType);
            if (result == JOptionPane.OK_OPTION) {


            }
        }
    }

    class InsertColumnListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            // show popup with column name TextArea && column types DropBox && ok/cancel buttons
            JComboBox<String> combo = new JComboBox(Column.Type.values());
            JTextField columnName = new JTextField(20);

            JPanel insertColumnPanel = new JPanel();
            //insertColumnPanel.add(new JLabel("Please enter the column name and select the column type")); //message
            insertColumnPanel.add(new JLabel("Column name:"));
            insertColumnPanel.add(columnName);
            insertColumnPanel.add(Box.createHorizontalBox());
            insertColumnPanel.add(Box.createHorizontalStrut(10)); // a spacer
            insertColumnPanel.add(new JLabel("Column type:"));
            insertColumnPanel.add(combo);
            String title = "Please enter the column name and select the column type";
            int messageType = JOptionPane.QUESTION_MESSAGE;

            boolean invalid = true;
            boolean cancelPressed = false;
            while (invalid && !cancelPressed) {

                int result = JOptionPane.showConfirmDialog(null, insertColumnPanel,
                        title, JOptionPane.OK_CANCEL_OPTION, messageType);
                if (result == JOptionPane.OK_OPTION) {

                    //System.out.println("column name: " + columnName.getText());
                    // System.out.println("column type: " + combo.getSelectedItem() + combo.getSelectedItem().getClass());

                    try {

                        if(columnName.getText().trim().isEmpty())
                            throw new InvalidEmptyName();
                        Column newColumn = new Column(columnName.getText().trim(), (Column.Type) combo.getSelectedItem());

                        databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable).insertColumn(newColumn);
                        invalid = false;
                        setSelectedTable(selectedTable); //refresh table content

                    } catch (ColumnAlreadyExists | InvalidEmptyName | InvalidValue | DoesNotExist exception) {

                        invalid = true;
                        title = exception.getMessage();
                        messageType = JOptionPane.ERROR_MESSAGE;
                    }
                }else{

                    cancelPressed = true;
                }
            }
        }
    }

    class InsertRecordListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

        }
    }

    class UpdateListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

        }
    }

    class DeleteListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

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

                // go through selected rows list
                // remove row from table model - first element from selected rows list
                // decrement list values (because values had changed, one value was deleted)
                while (selectedRows.size() > 0) {

                    try {
                        databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable).deleteRow(selectedRows.get(0));
                    } catch (DoesNotExist ignored) {
                        //doesNotExist.printStackTrace();
                    }
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

                disableDeleteButton();
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
            } catch (DoesNotExist ignored) {
                //doesNotExist.printStackTrace();
            }

            tableContent = new JTable(myTableModel);
            tableContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    public void disableButtonsWithoutDelete() {

        btnSelect.setEnabled(false);
        btnSelect.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnUpdate.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnInsertColumn.setEnabled(false);
        btnInsertColumn.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnInsertRecord.setEnabled(false);
        btnInsertRecord.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
    }


    public void enableButtonsWithoutDelete() {

        btnSelect.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnInsertColumn.setEnabled(true);
        btnInsertRecord.setEnabled(true);
    }

    public void setSelectedTable(String selectedTable) {

        this.selectedTable = selectedTable;
        enableButtonsWithoutDelete();

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
        disableButtonsWithoutDelete();
    }

    public void setAreRowsSelected(boolean areRowsSelected) {

        if (areRowsSelected) {

            enableDeleteButton();
        } else {

            disableDeleteButton();
        }
    }
}

