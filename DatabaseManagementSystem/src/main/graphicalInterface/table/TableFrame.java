package main.graphicalInterface.table;

import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.InputTextPopUp;
import main.model.DatabaseManagementSystem;
import main.model.Table;

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

    private JLabel titleLabel;
    private JList tablesList;
    private DefaultListModel listModel;
    private JScrollPane scrollTablesPanel;
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;

    private String selectedDatabase;

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
        titleLabel.setBounds(0, 20, 350, 20);

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
        BUTTONS
         */
        btnCreate = new JButton();
        btnCreate.setText("Create Table");
        btnCreate.setBounds(90, 480, 200, 50);
        btnCreate.addActionListener(new CreateListener());

        btnUpdate = new JButton();
        btnUpdate.setText("Update Table");
        btnUpdate.setBounds(90, 560, 200, 50);
        btnUpdate.addActionListener(new UpdateListener());

        btnDelete = new JButton();
        btnDelete.setText("Delete Table");
        btnDelete.setBounds(90, 640, 200, 50);
        btnDelete.addActionListener(new DeleteListener());

        //by default, Update and Delete Buttons are disabled
        disableUpdateDeleteButtons();

        addPanelObjects();
    }

    private void populateList() {

        listModel.clear();

        if (selectedDatabase != null) {
            for (Table table : databaseManagementSystem.getDatabase(selectedDatabase).getTables()) {

                listModel.addElement(table.getName());
            }
        }
    }

    private void addPanelObjects() {

        this.add(titleLabel);
        this.add(scrollTablesPanel);
        this.add(btnCreate);
        this.add(btnUpdate);
        this.add(btnDelete);
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        if (tablesList.getSelectedIndex() > -1) {

            enableDeleteUpdateButtons();
        } else {

            disableUpdateDeleteButtons();
        }
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

    class CreateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            InputTextPopUp inputTextPopUp = new InputTextPopUp(CREATE_NEW_TABLE_TITLE);
            Object input = inputTextPopUp.openPopUp(ENTER_TABLE_MESSAGE, false);

            while (input != null) {
                // user didn't pressed Cancel
                if (input.toString().trim().equals("")) {

                    // can't add table with empty name
                    input = inputTextPopUp.openPopUp(CREATE_NEW_TABLE_EMPTY_NAME, true);
                } else if (databaseManagementSystem.getDatabase(selectedDatabase).getTable(input.toString().trim()) != null) {

                    //already exist a database with this name, reopen popup with proper message
                    input = inputTextPopUp.openPopUp(WRONG_TABLE_NAME_ALREADY_EXISTS, true);
                } else {

                    databaseManagementSystem.getDatabase(selectedDatabase).createTable(input.toString(), new ArrayList<>());
                    populateList();
                    break;
                }
            }
        }
    }

    class UpdateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            int index = tablesList.getSelectedIndex();
            String currentName = listModel.get(index).toString();

            InputTextPopUp inputTextPopUp = new InputTextPopUp(UPDATE_TABLE_TITLE);
            Object input = inputTextPopUp.openPopUp(ENTER_NEW_TABLE_MESSAGE, false);

            while (input != null) {
                // user didn't pressed Cancel
                if (input.toString().trim().equals("")) {

                    // can't rename table to empty name
                    input = inputTextPopUp.openPopUp(UPDATE_TABLE_EMPTY_NAME, true);
                } else if (databaseManagementSystem.getDatabase(selectedDatabase).getTable(input.toString().trim()) != null) {

                    //already exist a table in the selected database with this name, reopen popup with proper message
                    input = inputTextPopUp.openPopUp(WRONG_TABLE_NAME_ALREADY_EXISTS, true);
                } else {

                    String newName = input.toString().trim();

                    String titleConfirmDelete = "Confirm Update Table";
                    String msgConfirmDelete = "Are you sure you want to change table name from \"" + currentName + "\" to \"" + newName + "\" ?";

                    ConfirmDialog updateDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
                    boolean update = updateDialog.confirm();

                    if (update) {

                        databaseManagementSystem.getDatabase(selectedDatabase).getTable(currentName).setName(newName);
                        populateList();
                    }

                    break;
                }
            }
        }
    }

    class DeleteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            int index = tablesList.getSelectedIndex();

            String titleConfirmDelete = "Confirm Delete Table";
            String msgConfirmDelete = "Are you sure you want to delete \"" + listModel.get(index).toString() + "\" Table from \"" + selectedDatabase + "\" Database ?";

            ConfirmDialog deleteDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
            boolean delete = deleteDialog.confirm();

            if (delete) {

                databaseManagementSystem.getDatabase(selectedDatabase).deleteTable(listModel.get(index).toString());
                listModel.remove(index);
            }

            if (listModel.getSize() == 0) { //No table left, disable update,delete buttons

                disableUpdateDeleteButtons();
            }
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
}
