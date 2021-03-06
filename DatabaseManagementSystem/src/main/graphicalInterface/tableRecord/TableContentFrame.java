package main.graphicalInterface.tableRecord;

import main.exception.*;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.PersistenceActionListener;
import main.model.*;
import main.persistance.DatabasePersistance;
import main.service.FilteringService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.graphicalInterface.GIConstants.*;

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

    private DatabasePersistance databasePersistance;
    private DeleteListener deleteListener;
    private  SelectListener selectListener;
    private InsertColumnListener insertColumnListener;
    private InsertRecordListener insertRecordListener;
    private UpdateColumnListener updateColumnListener;
    private UpdateFieldListener updateFieldListener;

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
        Listeners for buttons
         */

        databasePersistance = new DatabasePersistance();
        deleteListener = new DeleteListener();
        deleteListener.setDatabasePersistence(databasePersistance);
        selectListener = new SelectListener();
        selectListener.setDatabasePersistence(databasePersistance);
        insertColumnListener = new InsertColumnListener();
        insertColumnListener.setDatabasePersistence(databasePersistance);
        insertRecordListener = new InsertRecordListener();
        insertRecordListener.setDatabasePersistence(databasePersistance);
        updateColumnListener = new UpdateColumnListener();
        insertRecordListener.setDatabasePersistence(databasePersistance);
        updateFieldListener = new UpdateFieldListener();
        updateFieldListener.setDatabasePersistence(databasePersistance);

        /*
        BUTTONS
         */
        btnSelect = new JButton();
        btnSelect.setText("SELECT");
        btnSelect.setBounds(90, 490, 200, 50);
        btnSelect.addActionListener(selectListener);

        btnInsertColumn = new JButton();
        btnInsertColumn.setText("INSERT COLUMN");
        btnInsertColumn.setBounds(20, 550, 150, 50);
        btnInsertColumn.addActionListener(insertColumnListener);

        btnInsertRecord = new JButton();
        btnInsertRecord.setText("INSERT RECORD");
        btnInsertRecord.setBounds(185, 550, 150, 50);
        btnInsertRecord.addActionListener(insertRecordListener);

        btnUpdateColumnName = new JButton();
        btnUpdateColumnName.setText("UPDATE COLUMN NAME");
        btnUpdateColumnName.setBounds(20, 610, 150, 50);
        btnUpdateColumnName.addActionListener(updateColumnListener);

        btnUpdateFieldValue = new JButton();
        btnUpdateFieldValue.setText("UPDATE RECORD");
        btnUpdateFieldValue.setBounds(185, 610, 150, 50);
        btnUpdateFieldValue.addActionListener(updateFieldListener);

        btnDelete = new JButton();
        btnDelete.setText("DELETE");
        btnDelete.setBounds(90, 670, 200, 50);
        btnDelete.addActionListener(deleteListener);

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

    public class SelectListener extends PersistenceActionListener {

        SelectPanel selectPanel = new SelectPanel();
        ShowTableAfterSelect showTableAfterSelect;
        @Override
        public void beforePersist(ActionEvent e) {

            String title = "Please select columns and optionally add where clause";

            try {
                selectPanel.setSelectedTable(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {

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

                        showTableAfterSelect = new ShowTableAfterSelect(filteredTable);
                    } else {

                        Table filteredTable = new Table(originalTable.getName(), lstSelectedColumns);
                        Map<Column, List<Field>> selectResult = originalTable.select(originalTable.getData(), lstSelectedColumns);
                        for (Column col : filteredTable.getData().keySet()) {
                            filteredTable.getData().put(col, selectResult.get(col));
                        }
                        showTableAfterSelect = new ShowTableAfterSelect(filteredTable);
                    }

                    break;
                } catch (InvalidEmptyName | InvalidColumnSelection | InvalidValue | TypeMismatchException exception) {

                    result = selectPanel.openPopUp(exception.getMessage(), true);
                } catch (FieldValueNotSet | DoesNotExist ignored) {

                }
            }
        }

        public ShowTableAfterSelect getShowTableAfterSelect() {
            return showTableAfterSelect;
        }

        public void setSelectPanel(SelectPanel selectPanel) {

            this.selectPanel = selectPanel;
        }
    }

    public class InsertColumnListener extends PersistenceActionListener {

        JPanel insertColumnPanel = new JPanel();
        JOptionPane optionPane = new JOptionPane();

        @Override
        public void beforePersist(ActionEvent e) {

            // show popup with column name TextArea && column types DropBox && ok/cancel buttons
            JComboBox<String> combo = new JComboBox(Column.Type.values());
            JTextField columnName = new JTextField(20);

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

                int result = optionPane.showConfirmDialog(null, insertColumnPanel,
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

        public void setInsertColumnPanel(JPanel insertColumnPanel) {
            this.insertColumnPanel = insertColumnPanel;
        }

        public void setOptionPane(JOptionPane optionPane) {
            this.optionPane = optionPane;
        }
    }

    public class InsertRecordListener extends PersistenceActionListener {

        InsertRecordPanel insertRecordPanel = new InsertRecordPanel();

        @Override
        public void beforePersist(ActionEvent e) {

            String title = "Insert Record";

            try {
                insertRecordPanel.setInputTable(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {
            }

            Object result = insertRecordPanel.openPopUp(title, false);
            while (!result.equals(JOptionPane.CANCEL_OPTION) && insertRecordPanel != null) {

                try {

                    Table selectedTableDB = databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable);

                    Map<JLabel, JTextField> mapColumnValue = insertRecordPanel.getMapColumnNameValue();
                    Map<String, Field> row = new HashMap<>();

                    for (JLabel label : mapColumnValue.keySet()) {

                        JTextField value = mapColumnValue.get(label);

                        if (!value.getText().isEmpty()) {

                            Column column = selectedTableDB.getColumn(label.getText());
                            FilteringService.validate(value.getText());
                            Field newField = new Field();

                            if (column.getType().equals(Column.Type.INT)) {
                                try {

                                    newField.isIntValueSet();
                                    newField.setValue(Integer.parseInt(value.getText()));

                                } catch (NumberFormatException numberFormatException) {

                                    throw new TypeMismatchException(Column.Type.INT, Column.Type.STRING, label.getText());
                                }
                            } else {

                                newField.isStringValueSet();
                                newField.setValue(value.getText());
                            }

                            row.put(column.getName(), newField);
                        }
                    }

                    selectedTableDB.insert(row);
                    setSelectedTable(selectedTable);

                    break; // all worked fine; can go further
                } catch (InvalidValue | TypeMismatchException exception) {

                    result = insertRecordPanel.openPopUp(exception.getMessage(), true);
                } catch (DoesNotExist | InexistentColumn ignored) {
                    // not reachable
                }
            }
        }

        public void setInsertRecordPanel(InsertRecordPanel insertRecordPanel) {
            this.insertRecordPanel = insertRecordPanel;
        }
    }

    public class UpdateColumnListener extends PersistenceActionListener {

        UpdateColumnNamePanel updateColumnNamePanel = new UpdateColumnNamePanel();

        @Override
        public void beforePersist(ActionEvent e) {

            String title = "Please select columns and optionally add where clause";

            try {
                updateColumnNamePanel.setInputTable(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {
            }

            Object result = updateColumnNamePanel.openPopUp(title, false);
            while (!result.equals(JOptionPane.CANCEL_OPTION) && updateColumnNamePanel != null) {

                try {

                    Column columnToBeRenamed = (Column) updateColumnNamePanel.getLstColumns().getSelectedItem();
                    String newName = updateColumnNamePanel.getTxtNewColumnName().getText().trim();

                    if (newName.isEmpty())
                        throw new InvalidEmptyName("You can not rename with empty!");

                    Table selectedTableDB = databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable);
                    if (selectedTableDB.getColumnNames().contains(newName))
                        throw new AlreadyExists(newName);

                    FilteringService.validate(newName);

                    selectedTableDB.getColumn(columnToBeRenamed.getName()).setName(newName);
                    setSelectedTable(selectedTable);
                    break; // all worked fine; can go further
                } catch (InvalidEmptyName | AlreadyExists | InvalidValue exception) {

                    result = updateColumnNamePanel.openPopUp(exception.getMessage(), true);
                } catch (DoesNotExist ignored) {
                }
            }
        }

        public void setUpdateColumnNamePanel(UpdateColumnNamePanel updateColumnNamePanel) {
            this.updateColumnNamePanel = updateColumnNamePanel;
        }
    }

    public class UpdateFieldListener extends PersistenceActionListener {

        UpdateFieldPanel updateFieldPanel = new UpdateFieldPanel();
        @Override
        public void beforePersist(ActionEvent e) {

            String title = "Update fields values using WHERE clause";

            try {
                updateFieldPanel.setTable(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable));
            } catch (DoesNotExist doesNotExist) {
            }

            Object result = updateFieldPanel.openPopUp(title, false);
            while (!result.equals(JOptionPane.CANCEL_OPTION) && updateFieldPanel != null) {

                try {

                    String setNewValue = updateFieldPanel.getTxtNewValue().getText().trim();
                    if (setNewValue.isEmpty())
                        throw new InvalidEmptyName("Empty new value to be set");
                    FilteringService.validate(setNewValue);

                    String matchValue = updateFieldPanel.getTextMatchValue().getText().trim();
                    if (matchValue.isEmpty())
                        throw new InvalidEmptyName("Empty new value to do the match");
                    FilteringService.validate(matchValue);

                    Column selectedColumn = (Column) updateFieldPanel.getLstColumns().getSelectedItem();
                    FieldComparator.Sign selectedOperator = (FieldComparator.Sign) updateFieldPanel.getCbOperators().getSelectedItem();

                    Field matchValueField = new Field();
                    Field newValueField = new Field();

                    if (selectedColumn.getType().equals(Column.Type.INT)) {

                        matchValueField.isIntValueSet();
                        newValueField.isIntValueSet();

                        try {
                            matchValueField.setValue(Integer.parseInt(matchValue));
                            newValueField.setValue(Integer.parseInt(setNewValue));
                        } catch (NumberFormatException exceptionNumber) {

                            throw new TypeMismatchException(Column.Type.STRING, Column.Type.INT, selectedColumn.getName());
                        }

                    } else {

                        matchValueField.isStringValueSet();
                        newValueField.isStringValueSet();

                        matchValueField.setValue(matchValue);
                        newValueField.setValue(setNewValue);
                    }

                    Table selectedTableDB = databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable);


                    selectedTableDB.updateWhere(selectedColumn.getName(), selectedOperator, matchValueField, newValueField);
                    setSelectedTable(selectedTable);

                    break; // all worked fine; can go further
                } catch (InvalidEmptyName | InvalidValue | TypeMismatchException exception) {

                    result = updateFieldPanel.openPopUp(exception.getMessage(), true);
                } catch (DoesNotExist ignored) {

                }
            }
        }

        public void setUpdateFieldPanel(UpdateFieldPanel updateFieldPanel) {
            this.updateFieldPanel = updateFieldPanel;
        }
    }

    public class DeleteListener extends PersistenceActionListener {

        ConfirmDialog deleteDialog = new ConfirmDialog();

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

            deleteDialog.setTitle(titleConfirmDelete);
            deleteDialog.setMessage(msgConfirmDelete);
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

        public void setDeleteDialog(ConfirmDialog deleteDialog) {
            this.deleteDialog = deleteDialog;
        }
    }

    private void populateTable() {

        myTableModel.fireTableDataChanged();
        if (selectedDatabase != null && selectedTable != null) {

            try {
                myTableModel = new TableModel(databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable), false);
            } catch (FieldValueNotSet | DoesNotExist fieldValueNotSet) {
                // not reachable
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

        if(selectedTable != null){

            try {
                assert databaseManagementSystem.getDatabase(selectedDatabase).getTable(selectedTable) != null : "Precondition failed: input parameter is not null, but the table does not exist";
            } catch (DoesNotExist doesNotExist) {
                //doesNotExist.printStackTrace();

                assert true: "The selected database or table does not exist!";
            }
        }

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

        assert selectedDatabase != null: "Precondition failed: input parameter can not be null";
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

    public String getSelectedTable() {
        return selectedTable;
    }

    public JButton getBtnSelect() {
        return btnSelect;
    }

    public JButton getBtnInsertColumn() {
        return btnInsertColumn;
    }

    public JButton getBtnInsertRecord() {
        return btnInsertRecord;
    }

    public JButton getBtnUpdateColumnName() {
        return btnUpdateColumnName;
    }

    public JButton getBtnUpdateFieldValue() {
        return btnUpdateFieldValue;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public void setTableContent(JTable tableContent) {

        assert tableContent != null: "Precondition failed: input parameter can not be null";
        this.tableContent = tableContent;
    }

    public void setMyTableModel(TableModel myTableModel) {

        assert myTableModel != null: "Precondition failed: input parameter can not be null";
        this.myTableModel = myTableModel;
    }

    public JTable getTableContent() {
        return tableContent;
    }
}
