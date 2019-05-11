package graphicalInterface.database;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.graphicalInterface.ConfirmDialog;
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

    private DatabaseManagementSystem databaseManagementSystem;

    private TableFrame tableFrame;
    private DatabaseFrame databaseFrame;
    private DefaultListModel listModel;

    private JList databasesList;

    private String POPUP_TITLE = "InputTextPopUp_Title";
    private String POPUP_MSG = "InputTextPopUp_Message";

    int alreadyExistingDBs = 0;

    List<Database> dbAdded = new ArrayList<>();

    static ConfirmDialog confirmDialog;
    static DatabasePersistance databasePersistance;

    @BeforeAll
    public static void setUp(){

        confirmDialog = mock(ConfirmDialog.class);
        databasePersistance = mock(DatabasePersistance.class);

        //when(confirmDialog.confirm()).thenReturn(true);
        doReturn(true).when(confirmDialog).confirm();
        doNothing().when(databasePersistance).persist();
    }

    @BeforeEach
    public void setup() {

        databaseManagementSystem = DatabaseManagementSystem.getInstance();

        tableFrame = TableFrame.getInstance();
        databaseFrame = new DatabaseFrame();
        listModel = new DefaultListModel();

        alreadyExistingDBs = databaseManagementSystem.getDatabases().size() - dbAdded.size();
        //create another three DBs
        int i = 0;
        int noOfCreated = 0;
        dbAdded = new ArrayList<>();
        while (noOfCreated < 3) {

            try {

                Database database = databaseManagementSystem.createDatabase("DBTest" + i);
                listModel.addElement(database.getName());
                dbAdded.add(database);
                noOfCreated++;
                i++;
            } catch (InvalidValue | AlreadyExists invalidValue) {
                invalidValue.printStackTrace();
                i++;
            }
        }

        databaseFrame.setListModel(listModel);
        databasesList = new JList(listModel);
        databaseFrame.setDatabasesList(databasesList);
    }

    @AfterEach
    public void tearDown() {

        for (Database db : dbAdded) {

            try {
                databaseManagementSystem.deleteDatabase(db.getName());
            } catch (DoesNotExist doesNotExist) {
                doesNotExist.printStackTrace();
            }
        }
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
        int selectedIndex = alreadyExistingDBs;

        assertTrue(databasesList.getModel().getSize() >= 3);
        databaseFrame.getDatabasesList().setSelectedIndex(selectedIndex);

        assertTrue(listModel.getSize() > 1);

        // execute
        databaseFrame.valueChanged(new ListSelectionEvent(listModel, 0, databasesList.getModel().getSize(), false)); //first selection

        // verify
        assertNotNull(tableFrame.getSelectedDatabase());
        assertEquals(listModel.get(selectedIndex), tableFrame.getSelectedDatabase());
    }

    @Test
    public void testCreateListener() {

        // setup
        //JButton btnCreate = mock(JButton.class);
        // databaseFrame.setBtnCreate(btnCreate);


        // execute
        //btnCreate.doClick();
        //databaseFrame.getBtnCreate().doClick();

        // verify
    }

    @Test
    public void testUpdateListener() {

        // setup
        //JButton btnCreate = mock(JButton.class);
        // databaseFrame.setBtnCreate(btnCreate);


        // execute
        //btnCreate.doClick();
        //databaseFrame.getBtnUpdate().doClick();

        // verify
    }

    @Test
    public void whenConfirmDeleteThenDecreaseNoOfDB() {

        // setup
        assertTrue(listModel.getSize() > 0);
        databaseFrame.getDatabasesList().setSelectedIndex(databaseFrame.getDatabasesList().getModel().getSize() - 1);

        // execute & verify
        int noOfDBBeforeDelete = listModel.getSize();
        assertEquals(noOfDBBeforeDelete, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases before delete");

        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        DatabasePersistance databasePersistance = mock(DatabasePersistance.class);

        //when(confirmDialog.confirm()).thenReturn(true);
        doReturn(true).when(confirmDialog).confirm();
        doNothing().when(databasePersistance).persist();

        databaseFrame.getBtnDelete().setEnabled(true);

        assertTrue(databaseFrame.getBtnDelete().getActionListeners().length > 0);
        DatabaseFrame.DeleteListener deleteListener = (DatabaseFrame.DeleteListener) databaseFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(DatabaseFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(databaseFrame.getBtnDelete().isEnabled());

        deleteListener.setConfirmDialog(confirmDialog);
        deleteListener.setDatabasePersistence(databasePersistance);
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

        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        DatabasePersistance databasePersistance = mock(DatabasePersistance.class);

        //when(confirmDialog.confirm()).thenReturn(true);
        doReturn(false).when(confirmDialog).confirm();
        doNothing().when(databasePersistance).persist();

        assertTrue(databaseFrame.getBtnDelete().getActionListeners().length > 0);
        DatabaseFrame.DeleteListener deleteListener = (DatabaseFrame.DeleteListener) databaseFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(DatabaseFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(databaseFrame.getBtnDelete().isEnabled());

        deleteListener.setConfirmDialog(confirmDialog);
        deleteListener.setDatabasePersistence(databasePersistance);
        databaseFrame.getBtnDelete().doClick();
        assertEquals(noOfDBBeforeDelete, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases after delete");
    }

    @Test
    public void givenOneDBInListWhenDeleteThenDisableButtons() {

        // setup
        listModel = new DefaultListModel();
        try {

            Database database = databaseManagementSystem.createDatabase("DBTest");
            listModel.addElement(database.getName());
        } catch (InvalidValue | AlreadyExists invalidValue) {
            invalidValue.printStackTrace();
        }
        databasesList = new JList(listModel);
        databaseFrame.setListModel(listModel);
        databaseFrame.setDatabasesList(databasesList);

        assertTrue(listModel.getSize() == 1);
        databaseFrame.getDatabasesList().setSelectedIndex(0);
        databaseFrame.getBtnDelete().setEnabled(true);

        // execute & verify
        assertEquals(1, databaseFrame.getDatabasesList().getModel().getSize(), "Number of databases before delete");

        assertTrue(databaseFrame.getBtnDelete().getActionListeners().length > 0);
        DatabaseFrame.DeleteListener deleteListener = (DatabaseFrame.DeleteListener) databaseFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(DatabaseFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(databaseFrame.getBtnDelete().isEnabled());

        deleteListener.setConfirmDialog(confirmDialog);
        deleteListener.setDatabasePersistence(databasePersistance);

        databaseFrame.getBtnDelete().doClick();
        int actualNoOfDBs = databaseFrame.getDatabasesList().getModel().getSize();
        assertEquals(0, actualNoOfDBs, "Number of databases after delete");
    }
}
