package main.graphicalInterface.database;

import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.InputTextPopUp;
import main.graphicalInterface.PersistenceActionListener;
import main.graphicalInterface.table.TableFrame;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.util.DataBuilder;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static main.graphicalInterface.GIConstants.*;

/**
 * The frame for Database(s)
 * will have a title: JLabel titleLabel
 * a scroll pane with the existing database(s)
 * The policies for both scroll bars (vertical/horizontal) will be set to "AS_NEEDED"
 * 3 buttons for Database operations
 * CREATE
 * UPDATE (disabled default; enabled when user select something from Database(s) list)
 * DELETE (disabled default; enabled when user select something from Database(s) list)
 */
public class DatabaseFrame extends JPanel implements ListSelectionListener {

    private DatabaseManagementSystem databaseManagementSystem;
    private TableFrame tableFrame;

    private JLabel titleLabel;
    private JList databasesList;
    private DefaultListModel listModel;
    private JScrollPane scrollDatabasesPanel;
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;

    public DatabaseFrame() {

        this.setLayout(null);
        this.setBounds(0, 20, 350, 750);

        databaseManagementSystem = DatabaseManagementSystem.getInstance();
        tableFrame = TableFrame.getInstance();

        /*
        TITLE
         */
        titleLabel = new JLabel(DATABASES_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBounds(0, 20, 350, 20);

        /*
        Database(s) List
         */
        listModel = new DefaultListModel();
        populateList();

        databasesList = new JList(listModel);
        databasesList.setFont(new Font("Serif", Font.PLAIN, 17));
        databasesList.setFixedCellHeight(28); // set the dimension for list's elements
        databasesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        databasesList.addListSelectionListener(this);
        databasesList.setVisibleRowCount(-1); //display maximum number of items possible in the available space

        scrollDatabasesPanel = new JScrollPane(databasesList);
        scrollDatabasesPanel.setBounds(20, 45, 330, 400);

        scrollDatabasesPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollDatabasesPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        /*
        BUTTONS
         */
        btnCreate = new JButton();
        btnCreate.setText("Create Database");
        btnCreate.setBounds(90, 480, 200, 50);
        btnCreate.addActionListener(new CreateListener());

        btnUpdate = new JButton();
        btnUpdate.setText("Update Database");
        btnUpdate.setBounds(90, 560, 200, 50);
        btnUpdate.addActionListener(new UpdateListener());

        btnDelete = new JButton();
        btnDelete.setText("Delete Database");
        btnDelete.setBounds(90, 640, 200, 50);
        btnDelete.addActionListener(new DeleteListener());

        //default Update and Delete Buttons are disabled
        disableUpdateDeleteButtons();

        addPanelObjects();
    }

    private void populateList() {

        listModel.clear();
        for (Database database : databaseManagementSystem.getDatabases()) {

            listModel.addElement(database.getName());
        }
    }

    private void addPanelObjects() {

        this.add(titleLabel);
        this.add(scrollDatabasesPanel);
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

        if (databasesList.getSelectedIndex() > -1) {

            tableFrame.setSelectedDatabase(listModel.get(databasesList.getSelectedIndex()).toString());
            enableDeleteUpdateButtons();
        } else {

            tableFrame.setSelectedDatabase(null);
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

    class CreateListener extends PersistenceActionListener {

        @Override
        public void beforePersist(ActionEvent e) {

            InputTextPopUp inputTextPopUp = new InputTextPopUp(CREATE_NEW_DATABASE_TITLE );
            Object input = inputTextPopUp.openPopUp(ENTER_DATABASE_MESSAGE, false);

            while( input != null){
                // user didn't pressed Cancel
                if(input.toString().trim().equals("")){

                    // can't add table with empty name
                    input = inputTextPopUp.openPopUp(CREATE_NEW_DATABASE_EMPTY_NAME, true);
                }
                else if (databaseManagementSystem.getDatabase(input.toString().trim()) != null) {

                    //already exist a database with this name, reopen popup with proper message
                    input = inputTextPopUp.openPopUp(WRONG_DATABASE_NAME_ALREADY_EXISTS, true);
                } else if(input.toString().trim().contains("\"")) {

                    input = inputTextPopUp.openPopUp(WRONG_DATABASE_NAME_ESCAPE_CHARACTER, true);
                }else{

                    databaseManagementSystem.createDatabase(input.toString().trim());
                    populateList();
                    break;
                }
            }
        }
    }

    class UpdateListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            int index = databasesList.getSelectedIndex();
            String currentName = listModel.get(index).toString();

            InputTextPopUp inputTextPopUp = new InputTextPopUp(UPDATE_DATABASE_TITLE );
            Object input = inputTextPopUp.openPopUp(ENTER_NEW_DATABASE_MESSAGE, false);

            while( input != null){
                // user didn't pressed Cancel
                if(input.toString().trim().equals("")){

                    // can't rename database to empty name
                    input = inputTextPopUp.openPopUp(UPDATE_DATABASE_EMPTY_NAME, true);
                }
                else if (databaseManagementSystem.getDatabase(input.toString().trim()) != null) {

                    //already exist a database with this name, reopen popup with proper message
                    input = inputTextPopUp.openPopUp(WRONG_DATABASE_NAME_ALREADY_EXISTS, true);
                } else if(input.toString().trim().contains("\"")) {

                    input = inputTextPopUp.openPopUp(WRONG_DATABASE_NAME_ESCAPE_CHARACTER, true);
                }else{

                    String newName = input.toString().trim();

                    String titleConfirmDelete = "Confirm Update Database";
                    String msgConfirmDelete =  "Are you sure you want to change database name from \"" + currentName + "\" to \"" + newName + "\" ?";

                    ConfirmDialog updateDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
                    boolean update = updateDialog.confirm();

                    if(update) {

                        databaseManagementSystem.getDatabase(currentName).setName(newName);
                        populateList();
                    }

                    break;
                }
            }
        }
    }

    class DeleteListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            int index = databasesList.getSelectedIndex();

            String titleConfirmDelete = "Confirm Delete Database";
            String msgConfirmDelete =  "Are you sure you want to delete \"" + listModel.get(index).toString() + "\" Database?";

            ConfirmDialog deleteDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
            boolean delete = deleteDialog.confirm();

            if(delete) {

                databaseManagementSystem.deleteDatabase(listModel.get(index).toString());
                populateList();
            }

            if (listModel.getSize() == 0) { //No database left, disable update,delete buttons

                disableUpdateDeleteButtons();
            }
        }
    }
}
