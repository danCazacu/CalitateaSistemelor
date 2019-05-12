package graphicalInterface.tableRecord;

import main.exception.*;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.MainWindow;
import main.graphicalInterface.tableRecord.TableContentFrame;
import main.graphicalInterface.tableRecord.TableModel;
import main.model.*;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static main.graphicalInterface.GIConstants.RECORDS_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests suite for {@link TableContentFrame}
 */
public class TableContentFrameTest {

    TableContentFrame tableContentFrame = TableContentFrame.getInstance();

    private static DatabaseManagementSystem databaseManagementSystem;
    private static ConfirmDialog confirmDialogMock;
    private static DatabasePersistance databasePersistenceMock;
    private static MainWindow mainWindow;

    private JTable tableContent;
    private TableModel tableModel;

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
    public void setup() throws InvalidValue, AlreadyExists, DoesNotExist, FieldValueNotSet, InexistentColumn, TypeMismatchException {

        doReturn(true).when(confirmDialogMock).confirm();

        databaseManagementSystem.getDatabases().clear();
        Database database = databaseManagementSystem.createDatabase("DBTest");
        Table table = databaseManagementSystem.getDatabases().get(0).createTable("TableTest", new ArrayList<>());

        // add three columns: first int, second and third string
        databaseManagementSystem.getDatabases().get(0).getTable(table.getName()).insertColumn(new Column("TestColumn1", Column.Type.INT));
        databaseManagementSystem.getDatabases().get(0).getTable(table.getName()).insertColumn(new Column("TestColumn2", Column.Type.STRING));
        databaseManagementSystem.getDatabases().get(0).getTable(table.getName()).insertColumn(new Column("TestColumn3", Column.Type.STRING));

        // add values for columns (3 rows)
        for(int i = 0; i < 3; i++){

            Map<String, Field> row =  new HashMap<>();
            row.put("TestColumn1",new Field(i));
            row.put("TestColumn2",new Field("col2value" + "_" + i));
            row.put("TestColumn2",new Field("col3value" + "_" + i));
            databaseManagementSystem.getDatabase(database.getName()).getTable(table.getName()).insert(row);
        }

        tableModel = new TableModel(databaseManagementSystem.getDatabase(database.getName()).getTable(table.getName()), false);
        tableContent = new JTable(tableModel);
        tableContentFrame.setMyTableModel(tableModel);
        tableContentFrame.setTableContent(tableContent);
    }

    @Test
    public void testTableContentFrameInitNoTableSelected(){

        tableContentFrame.setSelectedTable(null);
        assertEquals(RECORDS_TITLE, tableContentFrame.getTitleLabel().getText());
        assertExistsButtonsInFrame();
    }

    @Test
    public void testTableContentFrameInitWhenNoDatabaseSelectedThenDisableButtons(){

        tableContentFrame.setSelectedDatabase(null);
        assertExistsButtonsInFrame();
        assertDisableButtons();
    }

    @Test
    public void testTableContentFrameInitWithTableSelected(){

        assertEquals(tableContentFrame.getSelectedTable() + "'s Records", tableContentFrame.getTitleLabel().getText());
        assertExistsButtonsInFrame();
        assertEnableButtons();
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
    @Test
    public void givenOneRecordInListWhenDeleteThenDisableButtons() throws DoesNotExist, InvalidValue, AlreadyExists, InexistentColumn, TypeMismatchException, FieldValueNotSet {

        databaseManagementSystem.getDatabases().clear();

        Database database = databaseManagementSystem.createDatabase("DBTest");
        Table table = databaseManagementSystem.getDatabases().get(0).createTable("TableTest", new ArrayList<>());

       // add three columns: first int, second and third string
        databaseManagementSystem.getDatabases().get(0).getTable(table.getName()).insertColumn(new Column("TestColumn1", Column.Type.INT));
        databaseManagementSystem.getDatabases().get(0).getTable(table.getName()).insertColumn(new Column("TestColumn2", Column.Type.STRING));
        databaseManagementSystem.getDatabases().get(0).getTable(table.getName()).insertColumn(new Column("TestColumn3", Column.Type.STRING));

        Map<String, Field> row =  new HashMap<>();
        row.put("TestColumn1",new Field(1));
        row.put("TestColumn2",new Field("col2value"));
        row.put("TestColumn2",new Field("col3value"));
        databaseManagementSystem.getDatabase(database.getName()).getTable(table.getName()).insert(row);

        tableModel = new TableModel(databaseManagementSystem.getDatabase(database.getName()).getTable(table.getName()), false);
        tableContent = new JTable(tableModel);
        tableContentFrame.setMyTableModel(tableModel);
        tableContentFrame.setTableContent(tableContent);

        assertTrue(tableContentFrame.getTableContent().getRowCount() == 1);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();
        tableContentFrame.getTableContent().setValueAt(true, 0, 0);
        assertTrue(tableContentFrame.getBtnDelete().isEnabled());

        // execute & verify
        assertEquals(1, tableContentFrame.getTableContent().getRowCount(), "Number of rows before delete");

        assertTrue(tableContentFrame.getBtnDelete().getActionListeners().length > 0);
        TableContentFrame.DeleteListener deleteListener = (TableContentFrame.DeleteListener) tableContentFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(TableContentFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(tableContentFrame.getBtnDelete().isEnabled());

        deleteListener.setDeleteDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        tableContentFrame.getBtnDelete().doClick();
        int actualNoOfRows = tableContentFrame.getTableContent().getRowCount();
        assertEquals(0, actualNoOfRows, "Number of rows after delete");
        tableContentFrame.setAreRowsSelected(false);
        assertFalse(tableContentFrame.getBtnDelete().isEnabled());
    }

    @Test
    public void whenNoConfirmDeleteThenSameNoOfDBs() {

        // setup
        assertTrue(tableContent.getRowCount() >= 3); //at least the one added by me
        tableContentFrame.getTableContent().setValueAt(true, 0, 0);
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        // execute & verify
        int noOfRowsBeforeDelete = tableContent.getRowCount();
        assertEquals(noOfRowsBeforeDelete, tableContentFrame.getTableContent().getRowCount(), "Number of rows before delete");

        doReturn(false).when(confirmDialogMock).confirm();

        assertTrue(tableContentFrame.getBtnDelete().getActionListeners().length > 0);
        TableContentFrame.DeleteListener deleteListener = (TableContentFrame.DeleteListener) tableContentFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(TableContentFrame.DeleteListener.class, deleteListener.getClass());

        assertTrue(tableContentFrame.getBtnDelete().isEnabled());

        deleteListener.setDeleteDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        tableContentFrame.getBtnDelete().doClick();
        assertEquals(noOfRowsBeforeDelete, tableContentFrame.getTableContent().getRowCount(), "Number of rows after delete");
    }

    @Test
    public void whenConfirmDeleteThenDecreaseNoOfDB() {

        // setup
        assertTrue(tableContent.getRowCount() > 0);
        tableContentFrame.getTableContent().setValueAt(true, 0, 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();
        tableContentFrame.getTableContent().setValueAt(true, 0, 0);
        assertTrue(tableContentFrame.getBtnDelete().isEnabled());

        // execute & verify
        int noOfRowsBeforeDelete = tableContent.getRowCount();
        assertEquals(noOfRowsBeforeDelete, tableContentFrame.getTableContent().getRowCount(), "Number of rows before delete");

        assertTrue(tableContentFrame.getBtnDelete().getActionListeners().length > 0);
        TableContentFrame.DeleteListener deleteListener = (TableContentFrame.DeleteListener) tableContentFrame.getBtnDelete().getActionListeners()[0];
        assertNotNull(deleteListener);
        assertEquals(TableContentFrame.DeleteListener.class, deleteListener.getClass());

        deleteListener.setDeleteDialog(confirmDialogMock);
        deleteListener.setDatabasePersistence(databasePersistenceMock);

        tableContentFrame.getBtnDelete().doClick();
        assertEquals(noOfRowsBeforeDelete - 1, tableContentFrame.getTableContent().getRowCount(), "Number of rows after delete");
    }


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
