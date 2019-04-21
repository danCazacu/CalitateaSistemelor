package main.graphicalInterface.tableRecord;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;
import main.exception.*;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.PersistenceActionListener;
import main.model.*;
import main.service.FilteringService;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static main.graphicalInterface.GIConstants.*;
import static main.service.FilteringService.validate;

public class TableContentFrame extends JPanel {

    private static TableContentFrame tableContentFrame;
    private String selectedTable;
    private String selectedDatabase;
    private DatabaseManagementSystem databaseManagementSystem;

    private JLabel titleLabel;
    private JButton btnSelect;
    private JButton btnInsertColumn;
    private JButton btnInsertRecord;
    private JButton btnUpdateColumnName;
    private JButton btnUpdateFieldValue;
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

        btnUpdateColumnName = new JButton();
        btnUpdateColumnName.setText("UPDATE COLUMN NAME");
        btnUpdateColumnName.setBounds(20, 610, 150, 50);
        btnUpdateColumnName.addActionListener(new UpdateColumnListener());

        btnUpdateFieldValue = new JButton();
        btnUpdateFieldValue.setText("UPDATE RECORD");
        btnUpdateFieldValue.setBounds(185, 610, 150, 50);
        btnUpdateFieldValue.addActionListener(new UpdateFieldListener());

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
        this.add(btnUpdateColumnName);
        this.add(btnUpdateFieldValue);
        this.add(btnDelete);
    }

    private void enableDeleteButton() {

        btnDelete.setEnabled(true);
    }

    private void disableDeleteButton() {

        btnDelete.setEnabled(false);
        btnDelete.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);

    }

    class SelectListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            String title = "Please select columns and optionally add where clause";
            SelectPanel selectPanel = null;

            try {
                selectPanel = new SelectPanel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {
                doesNotExist.printStackTrace();
            }

            Object result = selectPanel.openPopUp(title, false);
            while (!result.equals(JOptionPane.CANCEL_OPTION) && selectPanel != null) {
                try {

                    if (selectPanel.getLstColumns().getSelectedValuesList().size() == 0) {

                        throw new InvalidColumnSelection();
                    }

                    Table originalTable = databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable);
                    List<Column> lstSelectedColumns = new ArrayList<>();
                    List<SelectPanel.CheckListItem> selectedItems = selectPanel.getLstColumns().getSelectedValuesList();

                    for (int i = 0; i < selectedItems.size(); i++) {

                        Column column = originalTable.getColumn(String.valueOf(selectedItems.get(i)));
                        lstSelectedColumns.add(column);
                    }

                    if (selectPanel.getWhereCheckBox().isSelected()) {

                        FieldComparator.Sign operator = (FieldComparator.Sign) selectPanel.getCbOperators().getSelectedItem();
                        //System.out.println("operator: " + operator);

                        String matchValue = selectPanel.getTextMatchValue().getText().trim();
                        //System.out.println("value: " + matchValue);

                        if (matchValue.isEmpty()) {
                            throw new InvalidEmptyName("Can't do match with empty value");
                        }

                        if (lstSelectedColumns.get(0).getType().equals(Column.Type.INT)) {

                            try {
                                Integer.parseInt(matchValue);
                            } catch (NumberFormatException exceptionNumber) {

                                throw new TypeMismatchException(Column.Type.STRING, Column.Type.INT, lstSelectedColumns.get(0).getName());
                            }
                        }

                        Table filteredTable = new Table(originalTable.getName(), new ArrayList<>(originalTable.getData().keySet()));

                        Field field = new Field();
                        if (lstSelectedColumns.get(0).getType().equals(Column.Type.INT)) {

                            field.isIntValueSet();
                            field.setValue(Integer.parseInt(matchValue));
                        } else {

                            field.isStringValueSet();
                            field.setValue(matchValue);
                        }

                        Map<Column, List<Field>> filteredRows = originalTable.where(lstSelectedColumns.get(0).getName(), operator, field);

                        for (Column col : filteredTable.getData().keySet()) {
                            filteredTable.getData().put(col, filteredRows.get(col));
                        }

                        new ShowTableAfterSelect(filteredTable);
                    } else {

                        Table filteredTable = new Table(originalTable.getName(), lstSelectedColumns);
                        Map<Column, List<Field>> selectResult = originalTable.select(originalTable.getData(), lstSelectedColumns);
                        for (Column col : filteredTable.getData().keySet()) {
                            filteredTable.getData().put(col, selectResult.get(col));
                        }
                        new ShowTableAfterSelect(filteredTable);
                    }

                    break;
                } catch (InvalidEmptyName | InvalidColumnSelection | InvalidValue | TypeMismatchException exception) {

                    result = selectPanel.openPopUp(exception.getMessage(), true);
                } catch (FieldValueNotSet | DoesNotExist ignored) {
                    //fieldValueNotSet.printStackTrace();
                }
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

                        if (columnName.getText().trim().isEmpty())
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
                } else {

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

    class UpdateColumnListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            String title = "Please select columns and optionally add where clause";
            UpdateColumnNamePanel updateColumnNamePanel = null;

            try {
                updateColumnNamePanel = new UpdateColumnNamePanel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {
                //doesNotExist.printStackTrace();
            }

            Object result = updateColumnNamePanel.openPopUp(title, false);
            while (!result.equals(JOptionPane.CANCEL_OPTION) && updateColumnNamePanel != null) {

                try {

                    Column columnToBeRenamed = (Column) updateColumnNamePanel.lstColumns.getSelectedItem();
                    String newName = updateColumnNamePanel.txtNewColumnName.getText().trim();

                    if(newName.isEmpty())
                        throw new InvalidEmptyName("You can not rename with empty!");

                    Table selectedTableDB =  databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable);
                    if(selectedTableDB.getColumnNames().contains(newName))
                        throw new AlreadyExists(newName);

                    FilteringService.validate(newName);

                    selectedTableDB.getColumn(columnToBeRenamed.getName()).setName(newName);
                    setSelectedTable(selectedTable);
                    break; // all worked fine; can go further
                } catch (InvalidEmptyName | AlreadyExists | InvalidValue exception) {

                    result = updateColumnNamePanel.openPopUp(exception.getMessage(), true);
                } catch (DoesNotExist ignored) {

                    //ignored.printStackTrace();
                }
            }
        }
    }

    class UpdateFieldListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {


            String title = "Update fields values using WHERE clause";
            UpdateFieldPanel updateFieldPanel = null;

            try {
                updateFieldPanel = new UpdateFieldPanel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {
                //doesNotExist.printStackTrace();
            }

            Object result = updateFieldPanel.openPopUp(title, false);
            while (!result.equals(JOptionPane.CANCEL_OPTION) && updateFieldPanel != null) {

                try {

                    String setNewValue = updateFieldPanel.txtNewValue.getText().trim();
                    if(setNewValue.isEmpty())
                        throw new InvalidEmptyName("Empty new value to be set");
                    FilteringService.validate(setNewValue);

                    String matchValue = updateFieldPanel.textMatchValue.getText().trim();
                    if(matchValue.isEmpty())
                        throw new InvalidEmptyName("Empty new value to do the match");
                    FilteringService.validate(matchValue);

                    Column selectedColumn = (Column) updateFieldPanel.lstColumns.getSelectedItem();
                    FieldComparator.Sign selectedOperator = (FieldComparator.Sign) updateFieldPanel.cbOperators.getSelectedItem();

                    Field matchValueField = new Field();
                    Field newValueField = new Field();

                    if(selectedColumn.getType().equals(Column.Type.INT)){

                        matchValueField.isIntValueSet();
                        newValueField.isIntValueSet();

                        try {
                            matchValueField.setValue(Integer.parseInt(matchValue));
                            newValueField.setValue(Integer.parseInt(setNewValue));
                        }catch (NumberFormatException exceptionNumber) {

                            throw new TypeMismatchException(Column.Type.STRING, Column.Type.INT, selectedColumn.getName());
                        }

                    }else{

                        matchValueField.isStringValueSet();
                        newValueField.isStringValueSet();

                        matchValueField.setValue(matchValue);
                        newValueField.setValue(setNewValue);
                    }

                    Table selectedTableDB =  databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable);


                    selectedTableDB.updateWhere(selectedColumn.getName(), selectedOperator, matchValueField, newValueField );
                    setSelectedTable(selectedTable);

                    break; // all worked fine; can go further
                } catch (InvalidEmptyName | InvalidValue | TypeMismatchException exception) {

                    result = updateFieldPanel.openPopUp(exception.getMessage(), true);
                } catch (DoesNotExist ignored) {

                }
            }
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
                myTableModel = new TableModel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable), false);
            } catch (FieldValueNotSet | DoesNotExist fieldValueNotSet) {

                //fieldValueNotSet.printStackTrace();
            }

            tableContent = new JTable(myTableModel);
            tableContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    public void disableButtonsWithoutDelete() {

        btnSelect.setEnabled(false);
        btnSelect.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnUpdateColumnName.setEnabled(false);
        btnUpdateColumnName.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnUpdateFieldValue.setEnabled(false);
        btnUpdateFieldValue.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnInsertColumn.setEnabled(false);
        btnInsertColumn.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
        btnInsertRecord.setEnabled(false);
        btnInsertRecord.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
    }


    public void enableButtonsWithoutDelete() {

        btnSelect.setEnabled(true);
        btnUpdateColumnName.setEnabled(true);
        btnUpdateFieldValue.setEnabled(true);
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
        this.selectedTable = null;
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

