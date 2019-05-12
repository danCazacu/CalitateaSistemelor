package graphicalInterface.database;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.InputTextPopUp;
import main.graphicalInterface.MainWindow;
import main.graphicalInterface.database.DatabaseFrame;
import main.graphicalInterface.table.TableFrame;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;
import java.util.List;

import static main.graphicalInterface.GIConstants.DATABASES_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests suit for {@link main.graphicalInterface.database.DatabaseFrame}
 */
public class DatabaseFrameTest {

    private static DatabaseManagementSystem databaseManagementSystem;

    private TableFrame tableFrame;
    private DatabaseFrame databaseFrame;
    private DefaultListModel listModel;

    private JList databasesList;

    private static ConfirmDialog confirmDialogMock;
    private static DatabasePersistance databasePersistenceMock;
    private static InputTextPopUp inputTextPopUpMock;
    private static MainWindow mainWindow;

    private static final String CREATE_NEW_DB_NAME = "newDBTest";

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
    }

    @BeforeEach
    public void setup() throws DoesNotExist {

        while (databaseManagementSystem.getDatabases().size() > 0) {

            databaseManagementSystem.deleteDatabase(databaseManagementSystem.getDatabases().get(0));
        }

        tableFrame = TableFrame.getInstance();
        databaseFrame = new DatabaseFrame();
        databaseFrame.setMainWindow(mainWindow);
        listModel = new DefaultListModel();

        //create another three DBs
        int i = 0;
        int noOfCreated = 0;
        while (noOfCreated < 3) {

            try {

                Database database = databaseManagementSystem.createDatabase("DBTest" + i);
                listModel.addElement(database.getName());
                noOfCreated++;
                i++;
            } catch (InvalidValue | AlreadyExists invalidValue) {

                //invalidValue.printStackTrace();
                i++;
            }
        }

        databaseFrame.setListModel(listModel);
        databasesList = new JList(listModel);
        databaseFrame.setDatabasesList(databasesList);

        doReturn(true).when(confirmDialogMock).confirm();
        doReturn(CREATE_NEW_DB_NAME).when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());
    }

    @Test
    public void testDatabaseFrame() {

        assertEquals(DATABASES_TITLE, databaseFrame.getTitleLabel().getText());
    }

    @Test
    public void givenDatabaseFrameWhenInvalidValueSelectedThenDisableButtonsAndSetDatabaseInTableFrameNull() {

        // setup
        databaseFrame.getDatabasesList().setSelectedIndex(listModel.getSize());

        // execute
        databaseFrame.valueChanged(new ListSelectionEvent(listModel, listModel.getSize(), listModel.getSize(), false));

        // verify
        assertNull(tableFrame.getSelectedDatabase());
    }

    @Test
    public void givenDatabaseFrameWhenValidValueSelectedThenEnableButtonsAndSetDatabaseInTableFrame() {

        // setup
        int selectedIndex = 0;

        assertTrue(databasesList.getModel().getSize() == 3);
        databaseFrame.getDatabasesList().setSelectedIndex(selectedIndex);

        assertTrue(listModel.getSize() > 1);

        // execute
        databaseFrame.valueChanged(new ListSelectionEvent(listModel, 0, databasesList.getModel().getSize(), false)); //first selection

        // verify
        assertNotNull(tableFrame.getSelectedDatabase());
        assertEquals(listModel.get(selectedIndex), tableFrame.getSelectedDatabase());
    }

    /*
     * Create tests
     */
    @Test
    public void givenNullNewNameWhenCreateThenDoNothing() {


        assertNotNull(databaseFrame.getBtnCreate());

        doReturn(null).when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(databaseFrame.getBtnCreate().getActionListeners().length == 1);
        DatabaseFrame.CreateListener createListener = (DatabaseFrame.CreateListener) databaseFrame.getBtnCreate().getActionListeners()[0];
        assertNotNull(createListener);
        assertEquals(DatabaseFrame.CreateListener.class, createListener.getClass());

        createListener.setDatabasePersistence(databasePersistenceMock);
        createListener.setInputTextPopUp(inputTextPopUpMock);

        int noOfDBsBeforeCreate = databaseManagementSystem.getDatabases().size();
        databaseFrame.getBtnCreate().doClick();

        assertEquals(noOfDBsBeforeCreate, databaseManagementSystem.getDatabases().size(), "No DB was created");
    }

    //exception not reachable
    /* @Ignore @Test
    public void givenInvalidNameWhenCreateThenThrowInvalidEmptyNameException() {


        assertNotNull(databaseFrame.getBtnCreate());

        doReturn("").when(inputTextPopUpMock).openPopUp("test", Boolean.FALSE);
        doReturn("validName").when(inputTextPopUpMock).openPopUp("test", Boolean.TRUE);

        assertTrue(databaseFrame.getBtnCreate().getActionListeners().length == 1);
        DatabaseFrame.CreateListener createListener = (DatabaseFrame.CreateListener) databaseFrame.getBtnCreate().getActionListeners()[0];
        assertNotNull(createListener);
        assertEquals(DatabaseFrame.CreateListener.class, createListener.getClass());
        createListener.setDatabasePersistence(databasePersistenceMock);

        createListener.setInputTextPopUp(inputTextPopUpMock);

        assertThrows(InvalidEmptyName.class, () -> {

            databaseFrame.getBtnCreate().doClick();
        });
    }
*/
    @Test
    public void givenValidNewNameWhenCreateShouldAddNewDB() {

        assertNotNull(databaseFrame.getBtnCreate());

        assertTrue(databaseFrame.getBtnCreate().getActionListeners().length == 1);
        DatabaseFrame.CreateListener createListener = (DatabaseFrame.CreateListener) databaseFrame.getBtnCreate().getActionListeners()[0];
        assertNotNull(createListener);
        assertEquals(DatabaseFrame.CreateListener.class, createListener.getClass());

        createListener.setDatabasePersistence(databasePersistenceMock);
        createListener.setInputTextPopUp(inputTextPopUpMock);

        int noOfDBsBeforeCreate = databaseManagementSystem.getDatabases().size();
        List<Database> oldDatabases = new ArrayList<>(databaseManagementSystem.getDatabases());
        databaseFrame.getBtnCreate().doClick();

        assertEquals(noOfDBsBeforeCreate + 1, databaseManagementSystem.getDatabases().size(), "new DB was created");

        for (Database db : databaseManagementSystem.getDatabases()) {

            if (!oldDatabases.contains(db)) {

                assertEquals(CREATE_NEW_DB_NAME, db.getName());
            }
        }
    }

    /*
     * Update tests
     */
    @Test
    public void givenNullNewNameWhenUpdateThenDoNothing() {

        assertNotNull(databaseFrame.getBtnUpdate());
        assertTrue(databaseFrame.getDatabasesList().getModel().getSize() > 0);
        databaseFrame.getDatabasesList().setSelectedIndex(0);
        databaseFrame.getBtnUpdate().setEnabled(true);

        doReturn(null).when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(databaseFrame.getBtnUpdate().getActionListeners().length == 1);
        DatabaseFrame.UpdateListener updateListener = (DatabaseFrame.UpdateListener) databaseFrame.getBtnUpdate().getActionListeners()[0];
        assertNotNull(updateListener);
        assertEquals(DatabaseFrame.UpdateListener.class, updateListener.getClass());

        updateListener.setDatabasePersistence(databasePersistenceMock);
        updateListener.setInputTextPopUp(inputTextPopUpMock);

        List<Database> dbListBeforeUpdateClicked = new ArrayList<>(databaseManagementSystem.getDatabases());
        assertTrue(databaseFrame.getBtnUpdate().isEnabled());
        databaseFrame.getBtnUpdate().doClick();
        assertEquals(dbListBeforeUpdateClicked, databaseManagementSystem.getDatabases());
    }

    @Test
    public void givenValidNewNameWhenConfirmUpdateThenDoUpdateName() throws DoesNotExist {

        assertNotNull(databaseFrame.getBtnUpdate());
        assertTrue(databaseFrame.getDatabasesList().getModel().getSize() > 0);
        databaseFrame.getDatabasesList().setSelectedIndex(0);
        String oldDBName = databaseFrame.getDatabasesList().getSelectedValue().toString();
        databaseFrame.getBtnUpdate().setEnabled(true);

        doReturn("newDBName_update").when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(databaseFrame.getBtnUpdate().getActionListeners().length == 1);
        DatabaseFrame.UpdateListener updateListener = (DatabaseFrame.UpdateListener) databaseFrame.getBtnUpdate().getActionListeners()[0];
        assertNotNull(updateListener);
        assertEquals(DatabaseFrame.UpdateListener.class, updateListener.getClass());

        updateListener.setDatabasePersistence(databasePersistenceMock);
        updateListener.setInputTextPopUp(inputTextPopUpMock);
        updateListener.setUpdateDialog(confirmDialogMock);

        List<Database> dbListBeforeUpdateClicked = new ArrayList<>(databaseManagementSystem.getDatabases());
        assertTrue(databaseFrame.getBtnUpdate().isEnabled());
        databaseFrame.getBtnUpdate().doClick();
        assertEquals(dbListBeforeUpdateClicked.size(), databaseManagementSystem.getDatabases().size());

        for(Database db : dbListBeforeUpdateClicked){

            // verify that exist new name in the list
            if(databaseManagementSystem.getDatabase(db.getName()) == null && !db.getName().equals(oldDBName)){

                assertEquals("newDBName_update", db.getName());
            }
            // verify that not exist new name in the list (exist only in the old list
            if(databaseManagementSystem.getDatabase(db.getName()) == null && db.getName().equals(oldDBName)){

                assertEquals(oldDBName, db.getName());
            }
        }
    }

    @Test
    public void givenValidNewNameWhenNOConfirmUpdateThenDoNothing() throws DoesNotExist {

        assertNotNull(databaseFrame.getBtnUpdate());
        assertTrue(databaseFrame.getDatabasesList().getModel().getSize() > 0);
        databaseFrame.getDatabasesList().setSelectedIndex(0);
        databaseFrame.getBtnUpdate().setEnabled(true);

        doReturn("newDBName_update").when(inputTextPopUpMock).openPopUp(anyString(), anyBoolean());

        assertTrue(databaseFrame.getBtnUpdate().getActionListeners().length == 1);
        DatabaseFrame.UpdateListener updateListener = (DatabaseFrame.UpdateListener) databaseFrame.getBtnUpdate().getActionListeners()[0];
        assertNotNull(updateListener);
        assertEquals(DatabaseFrame.UpdateListener.class, updateListener.getClass());

        doReturn(false).when(confirmDialogMock).confirm();
        updateListener.setDatabasePersistence(databasePersistenceMock);
        updateListener.setInputTextPopUp(inputTextPopUpMock);
        updateListener.setUpdateDialog(confirmDialogMock);

        List<Database> dbListBeforeUpdateClicked = new ArrayList<>(databaseManagementSystem.getDatabases());
        assertTrue(databaseFrame.getBtnUpdate().isEnabled());
        databaseFrame.getBtnUpdate().doClick();
        assertEquals(dbListBeforeUpdateClicked.size(), databaseManagementSystem.getDatabases().size());
        assertEquals(dbListBeforeUpdateClicked, databaseManagementSystem.getDatabases());
    }

    /*
     * Delete tests
     */
    @Test
    public void whenConfirmDeleteThenDecreaseNoOfDB() {

        // setup
        assertTrue(listModel.getSize() > 0);
        databaseFrame.getDatabasesList().setSelectedIndex(0);

        // execute & verify
        int noOfDBBeforeDelete = listModel.getSize();
        assertEquals(noOfDBBeforeDelete, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases before delete");

        databaseFrame.getBtnDelete().setEnabled(true);

        assertTrue(databaseFrame.getBtnDelete().getActionListeners().length > 0);
        DatabaseFrame.DeleteListener deleteListener = (DatabaseFrame.DeleteListener) databaseFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(DatabaseFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(databaseFrame.getBtnDelete().isEnabled());

        deleteListener.setConfirmDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        databaseFrame.getBtnDelete().doClick();
        assertEquals(noOfDBBeforeDelete - 1, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases after delete");
    }

    @Test
    public void whenNoConfirmDeleteThenSameNoOfDBs() {

        // setup
        assertTrue(listModel.getSize() >= 3); //at least the one added by me
        databaseFrame.getDatabasesList().setSelectedIndex(databaseFrame.getDatabasesList().getModel().getSize() - 1);
        databaseFrame.getBtnDelete().setEnabled(true);

        // execute & verify
        int noOfDBBeforeDelete = listModel.getSize();
        assertEquals(noOfDBBeforeDelete, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases before delete");

        doReturn(false).when(confirmDialogMock).confirm();

        assertTrue(databaseFrame.getBtnDelete().getActionListeners().length > 0);
        DatabaseFrame.DeleteListener deleteListener = (DatabaseFrame.DeleteListener) databaseFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(DatabaseFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(databaseFrame.getBtnDelete().isEnabled());

        deleteListener.setConfirmDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        databaseFrame.getBtnDelete().doClick();
        assertEquals(noOfDBBeforeDelete, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases after delete");
    }

    @Test
    public void givenOneDBInListWhenDeleteThenDisableButtons() throws DoesNotExist, InvalidValue, AlreadyExists {

        // setup
        //delete all DB from management system
        while (databaseManagementSystem.getDatabases().size() > 0) {

            databaseManagementSystem.deleteDatabase(databaseManagementSystem.getDatabases().get(0));
        }

        listModel = new DefaultListModel();
        databaseFrame.setListModel(listModel);

        databasesList = new JList(listModel);
        databaseFrame.setDatabasesList(databasesList);

        Database database = databaseManagementSystem.createDatabase("DBTest");
        listModel.addElement(database.getName());

        assertTrue(listModel.getSize() == 1);
        assertTrue(databaseFrame.getDatabasesList().getModel().getSize() == 1);
        databaseFrame.getDatabasesList().setSelectedIndex(0);
        databaseFrame.getBtnDelete().setEnabled(true);

        // execute & verify
        assertEquals(1, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases before delete");

        assertTrue(databaseFrame.getBtnDelete().getActionListeners().length > 0);
        DatabaseFrame.DeleteListener deleteListener = (DatabaseFrame.DeleteListener) databaseFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(DatabaseFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(databaseFrame.getBtnDelete().isEnabled());

        deleteListener.setConfirmDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        databaseFrame.getBtnDelete().doClick();
        int actualNoOfDBs = databaseFrame.getDatabasesList().getModel().getSize();
        assertEquals(0, actualNoOfDBs, "Number of databases after delete");
    }
}
