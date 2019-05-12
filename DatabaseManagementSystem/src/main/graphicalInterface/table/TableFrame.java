package main.graphicalInterface.table;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.InputTextPopUp;
import main.graphicalInterface.PersistenceActionListener;
import main.graphicalInterface.tableRecord.InvalidEmptyName;
import main.graphicalInterface.tableRecord.TableContentFrame;
import main.model.CsvService;
import main.model.DatabaseManagementSystem;
import main.model.Table;
import main.persistance.DatabasePersistance;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static main.graphicalInterface.GIConstants.*;

/**
 * The frame for Table(s)
 * will have a title: JLabel titleLabel
 * a scroll pane with the existing table(s); the tables will be from the selected Database
 * The policies for both scroll bars (vertical/horizontal) will be set to "AS_NEEDED"
 * 3 buttons for Table operations
 * CREATE
 * UPDATE (disabled default; enabled when user select something from Table(s) list)
 * DELETE (disabled default; enabled when user select something from Table(s) list)
 */
public class TableFrame extends JPanel implements ListSelectionListener {

    private static TableFrame tableFrame;
    private DatabaseManagementSystem databaseManagementSystem;
    private TableContentFrame tableContentFrame;

    private JLabel titleLabel;
    private JList tablesList;
    private DefaultListModel listModel;
    private JScrollPane scrollTablesPanel;
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnExportTable;

    private String selectedDatabase;

    private DatabasePersistance databasePersistance;
    private DeleteListener deleteListener;
    private CreateListener createListener;
    private UpdateListener updateListener ;

    public static TableFrame getInstance() {

        if (tableFrame == null) {

            tableFrame = new TableFrame();
        }

        return tableFrame;
    }

    private TableFrame() {

        this.setLayout(null);
        this.setBounds(0, 40, 350, 750);

        databaseManagementSystem = DatabaseManagementSystem.getInstance();
        tableContentFrame = TableContentFrame.getInstance();

        /*
        TITLE
         */
        String title = "";
        if (selectedDatabase != null) {

            title = selectedDatabase + "'s Table(s)";
        } else {

            title = TABLES_TITLE;
        }
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBounds(0, 20, 350, 25);

        /*
        Table(s) List
         */
        listModel = new DefaultListModel();
        populateList();

        tablesList = new JList(listModel);
        tablesList.setFont(new Font("Serif", Font.PLAIN, 17));
        tablesList.setFixedCellHeight(28); // set the dimension for list's elements
        tablesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablesList.addListSelectionListener(this);
        tablesList.setVisibleRowCount(-1); //display maximum number of items possible in the available space

        scrollTablesPanel = new JScrollPane(tablesList);
        scrollTablesPanel.setBounds(0, 45, 330, 400);

        scrollTablesPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollTablesPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

          /*
         Listeners for buttons
         */

        databasePersistance = new DatabasePersistance();
        deleteListener = new DeleteListener();
        deleteListener.setDatabasePersistence(databasePersistance);
        createListener = new CreateListener();
        createListener.setDatabasePersistence(databasePersistance);
        updateListener = new UpdateListener();
        updateListener.setDatabasePersistence(databasePersistance);

        /*
        BUTTONS
         */
        btnCreate = new JButton();
        btnCreate.setText("Create Table");
        btnCreate.setBounds(90, 490, 200, 50);
        btnCreate.addActionListener(createListener);

        btnUpdate = new JButton();
        btnUpdate.setText("Update Table");
        btnUpdate.setBounds(90, 550, 200, 50);
        btnUpdate.addActionListener(updateListener);

        btnDelete = new JButton();
        btnDelete.setText("Delete Table");
        btnDelete.setBounds(90, 610, 200, 50);
        btnDelete.addActionListener(deleteListener);

        btnExportTable = new JButton();
        btnExportTable.setText("Export Table");
        btnExportTable.setBounds(90, 670, 200, 50);
        btnExportTable.addActionListener(new ExportListener());

        //by default, Update and Delete Buttons are disabled
        disableUpdateDeleteExportButtons();

        addPanelObjects();
    }

    private void populateList() {

        listModel.clear();

        if (selectedDatabase != null) {
            try {
                for (Table table : databaseManagementSystem.getDatabase(selectedDatabase).getTables()) {

                    listModel.addElement(table.getName());
                }
            } catch (DoesNotExist ignored) {
                //doesNotExist.printStackTrace();
            }
        }
    }

    private void addPanelObjects() {

        this.add(titleLabel);
        this.add(scrollTablesPanel);
        this.add(btnCreate);
        this.add(btnUpdate);
        this.add(btnDelete);
        this.add(btnExportTable);
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        if (tablesList.getSelectedIndex() > -1) {

            enableDeleteUpdateExportButtons();
            tableContentFrame.setSelectedDatabase(selectedDatabase);
            tableContentFrame.setSelectedTable(listModel.get(tablesList.getSelectedIndex()).toString());
        } else {

            tableContentFrame.setSelectedTable(null);
            disableUpdateDeleteExportButtons();
        }
    }

    private void enableDeleteUpdateExportButtons() {

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnExportTable.setEnabled(true);
        btnExportTable.setToolTipText("Export the selected table into .csv file format.");
    }

    private void disableUpdateDeleteExportButtons() {

        btnUpdate.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_DATABASE_ToolTipText);

        btnDelete.setEnabled(false);
        btnDelete.setToolTipText(ENABLE_BUTTON_DATABASE_ToolTipText);

        btnExportTable.setEnabled(false);
        btnExportTable.setToolTipText(ENABLE_BUTTON_TABLE_ToolTipText);
    }

    public class CreateListener extends PersistenceActionListener {

        InputTextPopUp inputTextPopUp = new InputTextPopUp();

        @Override
        public void beforePersist(ActionEvent e) {

            inputTextPopUp.setTitle(CREATE_NEW_TABLE_TITLE);
            Object input = inputTextPopUp.openPopUp(ENTER_TABLE_MESSAGE, false);

            while (input != null) {

                try {
                    // user didn't pressed Cancel
                    if (input.toString().trim().equals("")) {

                        // can't add table with empty name
                        throw new InvalidEmptyName();
                    }
                    databaseManagementSystem.getDatabase(selectedDatabase).createTable(input.toString(), new ArrayList<>());
                    populateList();
                    break;

                } catch (AlreadyExists | InvalidEmptyName | DoesNotExist | InvalidValue exception) {

                    input = inputTextPopUp.openPopUp(exception.getMessage(), true);
                }
            }
        }

        public void setInputTextPopUp(InputTextPopUp inputTextPopUp) {
            this.inputTextPopUp = inputTextPopUp;
        }
    }

    public class UpdateListener extends PersistenceActionListener {

        ConfirmDialog updateDialog = new ConfirmDialog();
        InputTextPopUp inputTextPopUp = new InputTextPopUp();
        @Override
        public void beforePersist(ActionEvent e) {

            int index = tablesList.getSelectedIndex();
            String currentName = listModel.get(index).toString();

            inputTextPopUp.setTitle(UPDATE_TABLE_TITLE);
            Object input = inputTextPopUp.openPopUp(ENTER_NEW_TABLE_MESSAGE, false);

            while (input != null) {
                // user didn't pressed Cancel
                try {
                    if (input.toString().trim().equals("")) {

                        // can't rename table to empty name
                        throw new InvalidEmptyName();
                    }

                    String newName = input.toString().trim();

                    String titleConfirmUpdate = "Confirm Update Table";
                    String msgConfirmUpdate = "Are you sure you want to change table name from \"" + currentName + "\" to \"" + newName + "\" ?";

                    updateDialog.setTitle(titleConfirmUpdate);
                    updateDialog.setMessage(msgConfirmUpdate);
                    boolean update = updateDialog.confirm();

                    if (update) {

                        Table existTable = null;
                        try {
                            existTable = databaseManagementSystem.getDatabase(selectedDatabase).getTable(newName);

                        } catch (DoesNotExist ignored) {
                        }
                        if (existTable != null) {

                            throw new AlreadyExists(newName);
                        }
                        databaseManagementSystem.getDatabase(selectedDatabase).getTable(currentName).setName(newName);
                        populateList();
                        disableUpdateDeleteExportButtons();
                    }
                    break;
                } catch (InvalidEmptyName | DoesNotExist | InvalidValue | AlreadyExists exception) {

                    input = inputTextPopUp.openPopUp(exception.getMessage(), true);

                }
            }
        }

        public void setUpdateDialog(ConfirmDialog updateDialog) {
            this.updateDialog = updateDialog;
        }

        public void setInputTextPopUp(InputTextPopUp inputTextPopUp) {
            this.inputTextPopUp = inputTextPopUp;
        }
    }

    public class DeleteListener extends PersistenceActionListener {

        ConfirmDialog deleteDialog = new ConfirmDialog();

        @Override
        public void beforePersist(ActionEvent e) {

            int index = tablesList.getSelectedIndex();

            String titleConfirmDelete = "Confirm Delete Table";
            String msgConfirmDelete = "Are you sure you want to delete \"" + listModel.get(index).toString() + "\" Table from \"" + selectedDatabase + "\" Database ?";

            deleteDialog.setTitle(titleConfirmDelete);
            deleteDialog.setMessage(msgConfirmDelete);
            boolean delete = deleteDialog.confirm();

            if (delete) {

                try {
                    databaseManagementSystem.getDatabase(selectedDatabase).deleteTable(listModel.get(index).toString());
                } catch (DoesNotExist ignored) {
                    //doesNotExist.printStackTrace();
                }
                //listModel.remove(index);
                populateList();
            }

            if (listModel.getSize() == 0) { //No table left, disable update,delete buttons

                disableUpdateDeleteExportButtons();
            }
        }

        public void setDeleteDialog(ConfirmDialog deleteDialog) {
            this.deleteDialog = deleteDialog;
        }
    }

    public class ExportListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            int index = tablesList.getSelectedIndex();

            try {
               CsvService.writeDataLineByLine(selectedDatabase, listModel.get(index).toString());

            } catch (DoesNotExist doesNotExist) {
                doesNotExist.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "The file was successfully exported!");
        }
    }

    public void setSelectedDatabase(String selectedDatabase) {

        this.selectedDatabase = selectedDatabase;
        String title = "";
        if (this.selectedDatabase != null) {

            title = this.selectedDatabase + "'s Table(s)";
        } else {

            title = TABLES_TITLE;
        }

        this.titleLabel.setText(title);
        populateList();
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JList getTablesList() {
        return tablesList;
    }

    public JButton getBtnCreate() {
        return btnCreate;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnExportTable() {
        return btnExportTable;
    }

    public String getSelectedDatabase() {
        return selectedDatabase;
    }

    public void setTablesList(JList tablesList) {
        this.tablesList = tablesList;
    }

    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }
}
