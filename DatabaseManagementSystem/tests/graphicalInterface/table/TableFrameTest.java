package graphicalInterface.table;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.InputTextPopUp;
import main.graphicalInterface.MainWindow;
import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;
import main.graphicalInterface.tableRecord.TableContentFrame;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Table;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

import java.util.ArrayList;
import java.util.List;

import static main.graphicalInterface.GIConstants.TABLES_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Tests suit for {@link TableFrame}
 */
public class TableFrameTest {

    private static DatabaseManagementSystem databaseManagementSystem;
    private static MainWindow mainWindow;
    private static ConfirmDialog confirmDialogMock;
    private static DatabasePersistance databasePersistenceMock;
    private static InputTextPopUp inputTextPopUpMock;

    private static final String CREATE_NEW_TABLE_NAME = "newTableTest";

    private TableFrame tableFrame;
    private  JList tablesList;
    private DefaultListModel listModel;
    private static TableContentFrame tableContentFrame;

    @BeforeAll
    public static void setUp() {


        confirmDialogMock = mock(ConfirmDialog.class);
        databasePersistenceMock = mock(DatabasePersistance.class);
        inputTextPopUpMock = mock(InputTextPopUp.class);

        doNothing().when(databasePersistenceMock).persist();

        //not persist when close
        mainWindow = new MainWindow();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.setDatabasePersistance(databasePersistenceMock);

        databaseManagementSystem = DatabaseManagementSystem.getInstance();
        databaseManagementSystem.setDatabasePersistance(databasePersistenceMock);

        tableContentFrame = TableContentFrame.getInstance();
    }

    @BeforeEach
    public void setup() throws DoesNotExist, InvalidValue, AlreadyExists {

        doReturn(true).when(confirmDialogMock).confirm();
        doReturn(CREATE_NEW_TABLE_NAME).when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        tableFrame = TableFrame.getInstance();

        databaseManagementSystem.getDatabases().clear();
        tableFrame = TableFrame.getInstance();

        listModel = new DefaultListModel();
        Database database = databaseManagementSystem.createDatabase("DBTest");
        tableFrame.setSelectedDatabase(database.getName());
        int i = 0;
        int noOfCreated = 0;
        while (noOfCreated < 3) {

            try {

                Table table = databaseManagementSystem.getDatabases().get(0).createTable("TableTest" + i , new ArrayList<>());
                listModel.addElement(table.getName());
                noOfCreated++;
                i++;
            } catch (InvalidValue | AlreadyExists invalidValue) {

                //invalidValue.printStackTrace();
                i++;
            }
        }

        tableFrame.setListModel(listModel);
        tablesList = new JList(listModel);
        tableFrame.setTablesList(tablesList);
    }

    @Test
    public void givenTableFrameWithoutSelectedDatabase(){

        tableFrame.setSelectedDatabase(null);
        assertEquals(TABLES_TITLE, tableFrame.getTitleLabel().getText());
    }

    @Test
    public void givenTableFrameWithSelectedDatabase(){

        tableFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        assertEquals(tableFrame.getSelectedDatabase()+ "'s Table(s)", tableFrame.getTitleLabel().getText());
    }

    @Test
    public void givenTableFrameWhenInvalidValueSelectedThenDisableButtonsAndSetTableInTableContentFrameNull() {

        // setup
        tableFrame.getTablesList().setSelectedIndex(listModel.getSize());
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));

        // execute
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));

        // verify
        assertNull(tableContentFrame.getSelectedTable());
    }

    @Test
    public void givenDatabaseFrameWhenValidValueSelectedThenEnableButtonsAndSetDatabaseInTableFrame() {

        // setup
        int selectedIndex = 0;

        assertTrue(tablesList.getModel().getSize() == 3);
        tableFrame.getTablesList().setSelectedIndex(selectedIndex);

        assertTrue(listModel.getSize() > 1);

        // execute
        tableFrame.valueChanged(new ListSelectionEvent(listModel, 0, tablesList.getModel().getSize(), false)); //first selection

        // verify
        assertNotNull(tableContentFrame.getSelectedTable());
        assertEquals(listModel.get(selectedIndex), tableContentFrame.getSelectedTable());
    }

    /*
    Test create listener
     */
    @Test
    public void givenNullNewNameWhenCreateThenDoNothing() {

        assertNotNull(tableFrame.getBtnCreate());
        doReturn(null).when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(tableFrame.getBtnCreate().getActionListeners().length == 1);
        TableFrame.CreateListener createListener = (TableFrame.CreateListener) tableFrame.getBtnCreate().getActionListeners()[0];
        assertNotNull(createListener);
        assertEquals(TableFrame.CreateListener.class, createListener.getClass());

        createListener.setDatabasePersistence(databasePersistenceMock);
        createListener.setInputTextPopUp(inputTextPopUpMock);

        int noOfTablesBeforeCreate = databaseManagementSystem.getDatabases().get(0).getTables().size();
        tableFrame.getBtnCreate().doClick();

        assertEquals(noOfTablesBeforeCreate, databaseManagementSystem.getDatabases().get(0).getTables().size(), "No Table was created");
    }

    @Test
    public void givenValidNewNameWhenCreateShouldAddNewDB() {

        assertNotNull(tableFrame.getBtnCreate());

        assertTrue(tableFrame.getBtnCreate().getActionListeners().length == 1);
        TableFrame.CreateListener createListener = (TableFrame.CreateListener) tableFrame.getBtnCreate().getActionListeners()[0];
        assertNotNull(createListener);
        assertEquals(TableFrame.CreateListener.class, createListener.getClass());

        createListener.setDatabasePersistence(databasePersistenceMock);
        createListener.setInputTextPopUp(inputTextPopUpMock);

        int noOfTablesBeforeCreate = databaseManagementSystem.getDatabases().get(0).getTables().size();
        List<Table> oldTables = new ArrayList<>(databaseManagementSystem.getDatabases().get(0).getTables());
        tableFrame.getBtnCreate().doClick();

        assertEquals(noOfTablesBeforeCreate + 1, databaseManagementSystem.getDatabases().get(0).getTables().size(), "new Table was created");

        for (Table table : databaseManagementSystem.getDatabases().get(0).getTables()) {

            if (!oldTables.contains(table)) {

                assertEquals(CREATE_NEW_TABLE_NAME, table.getName());
            }
        }
    }

    /*
    Test update listener
     */

    @Test
    public void givenNullNewNameWhenUpdateThenDoNothing() {

        assertNotNull(tableFrame.getBtnUpdate());
        assertTrue(tableFrame.getTablesList().getModel().getSize() > 0);
        tableFrame.getTablesList().setSelectedIndex(0);
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));
        assertEnableButtons();

        doReturn(null).when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(tableFrame.getBtnUpdate().getActionListeners().length == 1);
        TableFrame.UpdateListener updateListener = (TableFrame.UpdateListener) tableFrame.getBtnUpdate().getActionListeners()[0];
        assertNotNull(updateListener);
        assertEquals(TableFrame.UpdateListener.class, updateListener.getClass());

        updateListener.setDatabasePersistence(databasePersistenceMock);
        updateListener.setInputTextPopUp(inputTextPopUpMock);

        List<Table> tablesListBeforeUpdateClicked = new ArrayList<>(databaseManagementSystem.getDatabases().get(0).getTables());
        assertTrue(tableFrame.getBtnUpdate().isEnabled());
        tableFrame.getBtnUpdate().doClick();
        assertEquals(tablesListBeforeUpdateClicked, databaseManagementSystem.getDatabases().get(0).getTables());
    }

    @Test
    public void givenValidNewNameWhenConfirmUpdateThenDoUpdateName() throws DoesNotExist {

        assertNotNull(tableFrame.getBtnUpdate());
        assertTrue(tableFrame.getTablesList().getModel().getSize() > 0);
        tableFrame.getTablesList().setSelectedIndex(0);
        String oldTableName = tableFrame.getTablesList().getSelectedValue().toString();
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));
        assertEnableButtons();

        doReturn("newTableName_update").when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(tableFrame.getBtnUpdate().getActionListeners().length == 1);
        TableFrame.UpdateListener updateListener = (TableFrame.UpdateListener) tableFrame.getBtnUpdate().getActionListeners()[0];
        assertNotNull(updateListener);
        assertEquals(TableFrame.UpdateListener.class, updateListener.getClass());

        updateListener.setDatabasePersistence(databasePersistenceMock);
        updateListener.setInputTextPopUp(inputTextPopUpMock);
        updateListener.setUpdateDialog(confirmDialogMock);

        List<Table> tablesListBeforeUpdateClicked = new ArrayList<>(databaseManagementSystem.getDatabases().get(0).getTables());
        assertTrue(tableFrame.getBtnUpdate().isEnabled());
        tableFrame.getBtnUpdate().doClick();
        assertEquals(tablesListBeforeUpdateClicked.size(), databaseManagementSystem.getDatabases().get(0).getTables().size());

        for(Table table : tablesListBeforeUpdateClicked){

            // verify that exist new name in the list
            Database selectedDB = databaseManagementSystem.getDatabases().get(0);
            if(selectedDB.getTable("newTableName_update") == null && !table.getName().equals(oldTableName)){

                assertEquals("newTableName_update", table.getName());
            }

            // verify that not exist new name in the list (exist only in the old list)
            if(selectedDB.getTable("newTableName_update") == null && table.getName().equals(oldTableName)){

                assertEquals(oldTableName, table.getName());
            }
        }
    }

    @Test
    public void givenValidNewNameWhenNOConfirmUpdateThenDoNothing() throws DoesNotExist {

        assertNotNull(tableFrame.getBtnUpdate());
        assertTrue(tableFrame.getTablesList().getModel().getSize() > 0);
        tableFrame.getTablesList().setSelectedIndex(0);
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));

        doReturn("newDBName_update").when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(tableFrame.getBtnUpdate().getActionListeners().length == 1);
        TableFrame.UpdateListener updateListener = (TableFrame.UpdateListener) tableFrame.getBtnUpdate().getActionListeners()[0];
        assertNotNull(updateListener);
        assertEquals(TableFrame.UpdateListener.class, updateListener.getClass());

        doReturn(false).when(confirmDialogMock).confirm();
        updateListener.setDatabasePersistence(databasePersistenceMock);
        updateListener.setInputTextPopUp(inputTextPopUpMock);
        updateListener.setUpdateDialog(confirmDialogMock);

        List<Table> tableListBeforeUpdateClicked = new ArrayList<>(databaseManagementSystem.getDatabases().get(0).getTables());
        assertTrue(tableFrame.getBtnUpdate().isEnabled());
        tableFrame.getBtnUpdate().doClick();
        assertEquals(tableListBeforeUpdateClicked.size(), databaseManagementSystem.getDatabases().get(0).getTables().size());
        assertEquals(tableListBeforeUpdateClicked, databaseManagementSystem.getDatabases().get(0).getTables());
    }


    /*
    Test delete listener
     */

    @Test
    public void whenConfirmDeleteThenDecreaseNoOfTables() {

        // setup
        assertTrue(listModel.getSize() > 0);
        tableFrame.getTablesList().setSelectedIndex(0);
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));

        // execute & verify
        int noOfTablesBeforeDelete = listModel.getSize();
        assertEquals(noOfTablesBeforeDelete, tableFrame.getTablesList().getModel().getSize(), "Number of tables before delete");


        assertTrue(tableFrame.getBtnDelete().getActionListeners().length > 0);
        TableFrame.DeleteListener deleteListener = (TableFrame.DeleteListener) tableFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(TableFrame.DeleteListener.class, deleteListener.getClass());

        deleteListener.setDeleteDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);
        assertTrue(tableFrame.getBtnDelete().isEnabled());
        tableFrame.getBtnDelete().doClick();
        assertEquals(noOfTablesBeforeDelete - 1, tableFrame.getTablesList().getModel().getSize(), "Number of tables after delete (GUI list)");
        assertEquals(noOfTablesBeforeDelete - 1, databaseManagementSystem.getDatabases().get(0).getTables().size(), "Number of databases after delete (ModelList)");
    }

    @Test
    public void whenNoConfirmDeleteThenSameNoOfTables() {

        // setup
        assertTrue(listModel.getSize() >= 3); //at least the ones added by me
        tableFrame.getTablesList().setSelectedIndex(1);
        tableFrame.getBtnDelete().setEnabled(true);

        // execute & verify
        int noOfTablesBeforeDelete = listModel.getSize();
        assertEquals(noOfTablesBeforeDelete, tableFrame.getTablesList().getModel().getSize(), "Number of tables before delete (GUI list)");

        doReturn(false).when(confirmDialogMock).confirm();

        assertTrue(tableFrame.getBtnDelete().getActionListeners().length > 0);
        TableFrame.DeleteListener deleteListener = (TableFrame.DeleteListener) tableFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(TableFrame.DeleteListener.class, deleteListener.getClass());

        deleteListener.setDeleteDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        assertTrue(tableFrame.getBtnDelete().isEnabled());
        tableFrame.getBtnDelete().doClick();
        assertEquals(noOfTablesBeforeDelete, tableFrame.getTablesList().getModel().getSize(), "Number of tables after delete (GUI list)");
        assertEquals(noOfTablesBeforeDelete, databaseManagementSystem.getDatabases().get(0).getTables().size(), "Number of tables after delete (Model list)");
    }

    @Test
    public void givenOneTableInListWhenDeleteThenDisableButtons() throws DoesNotExist, InvalidValue, AlreadyExists {

        // setup
        Database selectedDB = databaseManagementSystem.getDatabases().get(0);
        while (selectedDB.getTables().size() > 0) {

            selectedDB.deleteTable(selectedDB.getTables().get(0).getName());
        }

        listModel = new DefaultListModel();
        tableFrame.setListModel(listModel);

        tablesList = new JList(listModel);
        tableFrame.setTablesList(tablesList);

        Table table = databaseManagementSystem.getDatabases().get(0).createTable("TableTest", new ArrayList<>());
        listModel.addElement(table.getName());

        assertTrue(listModel.getSize() == 1);
        assertTrue(tableFrame.getTablesList().getModel().getSize() == 1);
        tableFrame.getTablesList().setSelectedIndex(0);
        tableFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));
        assertEnableButtons();

        // execute & verify
        assertEquals(1, tableFrame.getTablesList().getModel().getSize(), "Number of tables before delete (GUI list)");
        assertEquals(1, databaseManagementSystem.getDatabases().get(0).getTables().size(),  "Number of tables before delete (Model list)");

        assertTrue(tableFrame.getBtnDelete().getActionListeners().length > 0);
        TableFrame.DeleteListener deleteListener = (TableFrame.DeleteListener) tableFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(TableFrame.DeleteListener.class, deleteListener.getClass());

        deleteListener.setDeleteDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        assertTrue(tableFrame.getBtnDelete().isEnabled());
        tableFrame.getBtnDelete().doClick();
        int actualNoOfTables = tableFrame.getTablesList().getModel().getSize();
        assertEquals(0, actualNoOfTables, "Number of tables after delete");

       assertDisableButtons();
    }

    public void assertDisableButtons(){

        assertFalse(tableFrame.getBtnUpdate().isEnabled());
        assertFalse(tableFrame.getBtnUpdate().isEnabled());
        assertFalse(tableFrame.getBtnExportTable().isEnabled());
    }

    public void assertEnableButtons(){

        assertTrue(tableFrame.getBtnUpdate().isEnabled());
        assertTrue(tableFrame.getBtnUpdate().isEnabled());
        assertTrue(tableFrame.getBtnExportTable().isEnabled());
    }

    /*
    Test export listener
     */

}
