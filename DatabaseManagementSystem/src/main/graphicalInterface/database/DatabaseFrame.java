package main.graphicalInterface.database;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.InputTextPopUp;
import main.graphicalInterface.PersistenceActionListener;
import main.graphicalInterface.table.TableFrame;
import main.graphicalInterface.tableRecord.InvalidEmptyName;
import main.model.CsvService;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Table;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;

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
    private JButton btnImportTable;

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
        titleLabel.setBounds(0, 20, 350, 25);

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
        btnCreate.setBounds(90, 490, 200, 50);
        btnCreate.addActionListener(new CreateListener());

        btnUpdate = new JButton();
        btnUpdate.setText("Update Database");
        btnUpdate.setBounds(90, 550, 200, 50);
        btnUpdate.addActionListener(new UpdateListener());

        btnDelete = new JButton();
        btnDelete.setText("Delete Database");
        btnDelete.setBounds(90, 610, 200, 50);
        btnDelete.addActionListener(new DeleteListener());

        btnImportTable = new JButton();
        btnImportTable.setText("Import Table");
        btnImportTable.setBounds(90, 670, 200, 50);
        btnImportTable.addActionListener(new ImportListener());

        //default Update and Delete Buttons are disabled
        disableUpdateDeleteImportButtons();

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
        this.add(btnImportTable);
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
            enableDeleteUpdateImportButtons();
        } else {

            tableFrame.setSelectedDatabase(null);
            disableUpdateDeleteImportButtons();
        }
    }

    private void enableDeleteUpdateImportButtons() {

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnImportTable.setEnabled(true);
        btnImportTable.setToolTipText("Import table in the selected database");
    }

    private void disableUpdateDeleteImportButtons() {

        btnUpdate.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_DATABASE_ToolTipText);

        btnDelete.setEnabled(false);
        btnUpdate.setToolTipText(ENABLE_BUTTON_DATABASE_ToolTipText);

        btnImportTable.setEnabled(false);
        btnImportTable.setToolTipText(ENABLE_BUTTON_DATABASE_ToolTipText);
    }

    class CreateListener extends PersistenceActionListener {

        @Override
        public void beforePersist(ActionEvent e) {

            InputTextPopUp inputTextPopUp = new InputTextPopUp(CREATE_NEW_DATABASE_TITLE);
            Object input = inputTextPopUp.openPopUp(ENTER_DATABASE_MESSAGE, false);

            while (input != null) {

                try {
                    // user didn't pressed Cancel
                    if (input.toString().trim().equals("")) {

                        // can't add table with empty name
                        throw new InvalidEmptyName();
                    }

                    databaseManagementSystem.createDatabase(input.toString());
                    populateList();
                    break;

                } catch (AlreadyExists | InvalidEmptyName | InvalidValue exception) {
                    //exception.printStackTrace();
                    input = inputTextPopUp.openPopUp(exception.getMessage(), true);
                }
            }
        }
    }

    class UpdateListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            int index = databasesList.getSelectedIndex();
            String currentName = listModel.get(index).toString();

            InputTextPopUp inputTextPopUp = new InputTextPopUp(UPDATE_DATABASE_TITLE);
            Object input = inputTextPopUp.openPopUp(ENTER_NEW_DATABASE_MESSAGE, false);

            while (input != null) {
                // user didn't pressed Cancel

                try {

                    if (input.toString().trim().equals("")) {

                        // can't rename database to empty name
                        throw new InvalidEmptyName();
                    }
                    String newName = input.toString().trim();

                    String titleConfirmDelete = "Confirm Update Database";
                    String msgConfirmDelete = "Are you sure you want to change database name from \"" + currentName + "\" to \"" + newName + "\" ?";

                    ConfirmDialog updateDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
                    boolean update = updateDialog.confirm();

                    if (update) {

                        Database existDB = null;
                        try {
                            existDB = databaseManagementSystem.getDatabase(newName);

                        }catch(DoesNotExist ignored){

                        }
                        if (existDB != null) {

                            throw new AlreadyExists(newName);
                        }
                        databaseManagementSystem.getDatabase(currentName).setName(newName);
                        populateList();
                        tableFrame.setSelectedDatabase(null);
                        disableUpdateDeleteImportButtons();
                    }
                    break;

                } catch (InvalidValue | InvalidEmptyName | DoesNotExist | AlreadyExists exception) {

                    input = inputTextPopUp.openPopUp(exception.getMessage(), true);
                }
            }
        }
    }

    class DeleteListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {

            int index = databasesList.getSelectedIndex();

            String titleConfirmDelete = "Confirm Delete Database";
            String msgConfirmDelete = "Are you sure you want to delete \"" + listModel.get(index).toString() + "\" Database?";

            ConfirmDialog deleteDialog = new ConfirmDialog(titleConfirmDelete, msgConfirmDelete);
            boolean delete = deleteDialog.confirm();

            if (delete) {

                try {
                    databaseManagementSystem.deleteDatabase(listModel.get(index).toString());
                    populateList();
                } catch (DoesNotExist ignored) {
                    //doesNotExist.printStackTrace();
                }
            }

            if (listModel.getSize() == 0) { //No database left, disable update,delete buttons

                disableUpdateDeleteImportButtons();
            }
        }
    }

    class ImportListener extends PersistenceActionListener {
        @Override
        public void beforePersist(ActionEvent e) {
            int index = databasesList.getSelectedIndex();
            String currentName = listModel.get(index).toString();


            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CSV Files", "csv");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(btnImportTable);
            String absPath = chooser.getSelectedFile().getAbsolutePath();
            boolean result = CsvService.importDataLineByLine(absPath, currentName);
            if (!result) {
                JOptionPane.showMessageDialog(null, "This table name does not exist.");
            } else {
                JOptionPane.showMessageDialog(null, "The file was successfully imported.");
            }
        }
    }
}
