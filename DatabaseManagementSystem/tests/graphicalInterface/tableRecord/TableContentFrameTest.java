package graphicalInterface.tableRecord;

import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.MainWindow;
import main.graphicalInterface.tableRecord.TableContentFrame;
import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static main.graphicalInterface.GIConstants.RECORDS_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * Tests suite for {@link TableContentFrame}
 */
public class TableContentFrameTest {

    TableContentFrame tableContentFrame = TableContentFrame.getInstance();

    private static DatabaseManagementSystem databaseManagementSystem;
    private static ConfirmDialog confirmDialogMock;
    private static DatabasePersistance databasePersistenceMock;
    private static MainWindow mainWindow;

    @BeforeAll
    public static void setUp() {

        confirmDialogMock = mock(ConfirmDialog.class);
        databasePersistenceMock = mock(DatabasePersistance.class);

        doNothing().when(databasePersistenceMock).persist();

        //not persist when close
        mainWindow = new MainWindow();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.setDatabasePersistance(databasePersistenceMock);

        databaseManagementSystem = DatabaseManagementSystem.getInstance();
        databaseManagementSystem.setDatabasePersistance(databasePersistenceMock);
    }

    @BeforeEach
    public void setup() {


    }

    @Test
    public void testTableContentFrameInit(){

        assertEquals(RECORDS_TITLE, tableContentFrame.getTitleLabel().getText());
        assertExistsButtonsInFrame();
    }

    public void assertExistsButtonsInFrame(){

        assertNotNull(tableContentFrame.getBtnSelect());
        assertNotNull(tableContentFrame.getBtnUpdateColumnName());
        assertNotNull(tableContentFrame.getBtnUpdateFieldValue());
        assertNotNull(tableContentFrame.getBtnInsertColumn());
        assertNotNull(tableContentFrame.getBtnInsertRecord());
        assertNotNull(tableContentFrame.getBtnDelete());
    }

    /*
    Select Listener
     */

    /*
    UpdateColumn Listener
     */

    /*
    UpdateField Listener
     */

    /*
    InsertColumn Listener
     */

    /*
    InsertRecord Listener
     */

    /*
    Delete Listener
     */


    public void assertDisableButtons() {

        assertFalse(tableContentFrame.getBtnSelect().isEnabled());
        assertFalse(tableContentFrame.getBtnUpdateColumnName().isEnabled());
        assertFalse(tableContentFrame.getBtnUpdateFieldValue().isEnabled());
        assertFalse(tableContentFrame.getBtnInsertColumn().isEnabled());
        assertFalse(tableContentFrame.getBtnInsertRecord().isEnabled());
    }

    public void assertEnableButtons() {

        assertTrue(tableContentFrame.getBtnSelect().isEnabled());
        assertTrue(tableContentFrame.getBtnUpdateColumnName().isEnabled());
        assertTrue(tableContentFrame.getBtnUpdateFieldValue().isEnabled());
        assertTrue(tableContentFrame.getBtnInsertColumn().isEnabled());
        assertTrue(tableContentFrame.getBtnInsertRecord().isEnabled());
    }
}
