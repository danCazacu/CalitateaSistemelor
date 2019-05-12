package graphicalInterface.tableRecord;

import jdk.nashorn.internal.ir.annotations.Ignore;
import main.exception.*;
import main.graphicalInterface.ConfirmDialog;
import main.graphicalInterface.MainWindow;
import main.graphicalInterface.tableRecord.*;
import main.model.*;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.*;

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
    private static SelectPanel selectPanelMock;
    private static MainWindow mainWindow;

    private JTable tableContent;
    private TableModel tableModel;

    @BeforeAll
    public static void setUp() {

        confirmDialogMock = mock(ConfirmDialog.class);
        databasePersistenceMock = mock(DatabasePersistance.class);
        selectPanelMock = mock(SelectPanel.class);

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
            row.put("TestColumn1",new Field(1));
            row.put("TestColumn2",new Field("col2value" + "_" + i));
            row.put("TestColumn2",new Field("col3value" + "_" + i));
            databaseManagementSystem.getDatabase(database.getName()).getTable(table.getName()).insert(row);
        }

        tableModel = new TableModel(databaseManagementSystem.getDatabase(database.getName()).getTable(table.getName()), false);
        tableContent = new JTable(tableModel);
        tableContentFrame.setMyTableModel(tableModel);
        tableContentFrame.setTableContent(tableContent);


        doCallRealMethod().when(selectPanelMock).setSelectedTable(any(Table.class));
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);
        when(selectPanelMock.getWhereCheckBox()).thenReturn(checkBox);
        JComboBox cbBox = new JComboBox<>(FieldComparator.Sign.values());
        cbBox.setSelectedItem(FieldComparator.Sign.EQUAL);
        when(selectPanelMock.getCbOperators()).thenReturn(cbBox);
        when(selectPanelMock.getTextMatchValue()).thenReturn(new JTextField("1"));
        SelectPanel.CheckListItem checkListItem = new SelectPanel.CheckListItem("TestColumn1");
        checkListItem.setSelected(true);
        JList<SelectPanel.CheckListItem> selectedColumns = new JList(new Object[]{"TestColumn1"});
        selectedColumns.setSelectedValue(checkListItem, false);
        selectedColumns.setSelectedIndex(0);
        doReturn(selectedColumns).when(selectPanelMock).getLstColumns();
        List<SelectPanel.CheckListItem> selectedCols = Collections.singletonList(checkListItem);
         when(selectPanelMock.openPopUp(anyString(), anyBoolean())).thenReturn(JOptionPane.OK_OPTION);
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
    @Test
    public void whenSelectPressCancelThenContentShouldNotBeChanged() throws DoesNotExist {

        // setup
        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        // execute & verify
        JTable oldTable = new JTable(tableContent.getModel());
        assertEquals(oldTable.getColumnCount(), tableContentFrame.getTableContent().getColumnCount(), "table content before select");
        assertEquals(oldTable.getRowCount(), tableContentFrame.getTableContent().getRowCount());

        doReturn(false).when(confirmDialogMock).confirm();

        assertTrue(tableContentFrame.getBtnSelect().getActionListeners().length > 0);
        TableContentFrame.SelectListener selectListener = (TableContentFrame.SelectListener) tableContentFrame.getBtnSelect().getActionListeners()[0];
        assertNotNull(selectListener);
        assertEquals(TableContentFrame.SelectListener.class, selectListener.getClass());

        assertTrue(tableContentFrame.getBtnSelect().isEnabled());

        doReturn(JOptionPane.CANCEL_OPTION).when(selectPanelMock).openPopUp(anyString(), anyBoolean());
        selectListener.setSelectPanel(selectPanelMock);
        selectListener.setDatabasePersistence(databasePersistenceMock);

        tableContentFrame.getBtnSelect().doClick();
        assertEquals(oldTable.getColumnCount(), tableContentFrame.getTableContent().getColumnCount(), "table content after select (col)");
        assertEquals(oldTable.getRowCount(), tableContentFrame.getTableContent().getRowCount(), "table content after select (row)");
    }

    @Test
    public void whenSelectPressOKThenContentShouldBeChanged_containsWhereClause() throws DoesNotExist {

        // setup
        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        // execute & verify
        JTable oldTable = new JTable(tableContent.getModel());
        assertEquals(oldTable.getColumnCount(), tableContentFrame.getTableContent().getColumnCount(), "table content before select");
        assertEquals(oldTable.getRowCount(), tableContentFrame.getTableContent().getRowCount());

        doReturn(false).when(confirmDialogMock).confirm();

        assertTrue(tableContentFrame.getBtnDelete().getActionListeners().length > 0);
        TableContentFrame.SelectListener selectListener = (TableContentFrame.SelectListener) tableContentFrame.getBtnSelect().getActionListeners()[0];
        assertNotNull(selectListener);
        assertEquals(TableContentFrame.SelectListener.class, selectListener.getClass());

        assertTrue(tableContentFrame.getBtnSelect().isEnabled());

        selectListener.setSelectPanel(selectPanelMock);
        selectListener.setDatabasePersistence(databasePersistenceMock);

        assertTrue(selectPanelMock.getLstColumns().getSelectedValuesList().size()  > 0);
        tableContentFrame.getBtnSelect().doClick();
        assertNotEquals(oldTable.getColumnCount(), selectListener.getShowTableAfterSelect().getTable().getColumnCount(), "table content after select (col)");
        //assertNotEquals(oldTable.getRowCount(), selectListener.getShowTableAfterSelect().getTable().getRowCount(), "table content after select (row)");
    }

    @Test
    public void whenSelectPressOKThenContentShouldBeChanged_withoutWhereClause() throws DoesNotExist {

        // setup
        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        // execute & verify
        JTable oldTable = new JTable(tableContent.getModel());
        assertEquals(oldTable.getColumnCount(), tableContentFrame.getTableContent().getColumnCount(), "table content before select");
        assertEquals(oldTable.getRowCount(), tableContentFrame.getTableContent().getRowCount());

        doReturn(false).when(confirmDialogMock).confirm();

        assertTrue(tableContentFrame.getBtnDelete().getActionListeners().length > 0);
        TableContentFrame.SelectListener selectListener = (TableContentFrame.SelectListener) tableContentFrame.getBtnSelect().getActionListeners()[0];
        assertNotNull(selectListener);
        assertEquals(TableContentFrame.SelectListener.class, selectListener.getClass());

        assertTrue(tableContentFrame.getBtnSelect().isEnabled());

        selectListener.setSelectPanel(selectPanelMock);
        selectListener.setDatabasePersistence(databasePersistenceMock);

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(false);
        doReturn(checkBox).when(selectPanelMock).getWhereCheckBox();

        assertTrue(selectPanelMock.getLstColumns().getSelectedValuesList().size()  > 0);
        tableContentFrame.getBtnSelect().doClick();
        assertNotEquals(oldTable.getColumnCount(), selectListener.getShowTableAfterSelect().getTable().getColumnCount(), "table content after select (col)");
        assertEquals(oldTable.getRowCount(), selectListener.getShowTableAfterSelect().getTable().getRowCount(), "table content after select (row)");
    }

    /*
    InsertColumn Listener
     */
    @Ignore
    @Test
    public void whenInsertColumn_CancelPressed_DoNothing(){

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnInsertColumn().getActionListeners().length > 0);
        TableContentFrame.InsertColumnListener insertColumnListener = (TableContentFrame.InsertColumnListener) tableContentFrame.getBtnInsertColumn().getActionListeners()[0];
        assertNotNull(insertColumnListener);
        assertEquals(TableContentFrame.InsertColumnListener.class, insertColumnListener.getClass());

        insertColumnListener.setDatabasePersistence(databasePersistenceMock);

        JPanel panelMock = mock(JPanel.class);
        insertColumnListener.setInsertColumnPanel(panelMock);

        String title = "Please enter the column name and select the column type";
        JOptionPane optionPaneMock = mock(JOptionPane.class);

        when(optionPaneMock.showConfirmDialog(any(), any(JPanel.class), anyString(),
              anyInt(), anyInt()))
                .thenReturn(JOptionPane.CANCEL_OPTION);
        insertColumnListener.setOptionPane(optionPaneMock);

        int noOfColBeforeInsert = tableContentFrame.getTableContent().getColumnCount();
        tableContentFrame.getBtnInsertColumn().doClick();

        assertEquals(noOfColBeforeInsert, tableContentFrame.getTableContent().getColumnCount());
    }


    /*
    InsertRecord Listener
     */
    @Test
    public void whenInsertRecord_CancelPressed_DoNothing(){

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnInsertRecord().getActionListeners().length > 0);
        TableContentFrame.InsertRecordListener insertRecordListener = (TableContentFrame.InsertRecordListener) tableContentFrame.getBtnInsertRecord().getActionListeners()[0];
        assertNotNull(insertRecordListener);
        assertEquals(TableContentFrame.InsertRecordListener.class, insertRecordListener.getClass());

        insertRecordListener.setDatabasePersistence(databasePersistenceMock);

        InsertRecordPanel insertRecordPanel = mock(InsertRecordPanel.class);
        when(insertRecordPanel.openPopUp(anyString(),anyBoolean())).thenReturn(JOptionPane.CANCEL_OPTION);
        insertRecordListener.setInsertRecordPanel(insertRecordPanel);

        int noOfRowsBeforeInsert = tableContentFrame.getTableContent().getRowCount();
        tableContentFrame.getBtnInsertRecord().doClick();

        assertEquals(noOfRowsBeforeInsert, tableContentFrame.getTableContent().getRowCount());
    }

    @Test
    public void whenInsertRecord_OkPressed_AddRecord(){

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnInsertRecord().getActionListeners().length > 0);
        TableContentFrame.InsertRecordListener insertRecordListener = (TableContentFrame.InsertRecordListener) tableContentFrame.getBtnInsertRecord().getActionListeners()[0];
        assertNotNull(insertRecordListener);
        assertEquals(TableContentFrame.InsertRecordListener.class, insertRecordListener.getClass());

        insertRecordListener.setDatabasePersistence(databasePersistenceMock);

        InsertRecordPanel insertRecordPanel = mock(InsertRecordPanel.class);
        when(insertRecordPanel.openPopUp(anyString(),anyBoolean())).thenReturn(JOptionPane.OK_OPTION);

        Map<JLabel, JTextField> mapColumnNameValue = new HashMap<>();

        mapColumnNameValue.put(new JLabel("TestColumn1"), new JTextField("1"));
        mapColumnNameValue.put(new JLabel("TestColumn2"), new JTextField("test_col_2"));
        mapColumnNameValue.put(new JLabel("TestColumn3"), new JTextField("test_col_3"));

        when(insertRecordPanel.getMapColumnNameValue()).thenReturn(mapColumnNameValue);
        insertRecordListener.setInsertRecordPanel(insertRecordPanel);

        int noOfRowsBeforeInsert = tableContentFrame.getTableContent().getRowCount();
        tableContentFrame.getBtnInsertRecord().doClick();

        assertEquals(noOfRowsBeforeInsert + 1, tableContentFrame.getTableContent().getRowCount());
    }

    /*
    UpdateColumn Listener
     */
    @Test
    public void whenUpdateColumn_CancelPressed_DoNothing() throws DoesNotExist {

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnUpdateColumnName().getActionListeners().length > 0);
        TableContentFrame.UpdateColumnListener updateColumnListener = (TableContentFrame.UpdateColumnListener) tableContentFrame.getBtnUpdateColumnName().getActionListeners()[0];
        assertNotNull(updateColumnListener);
        assertEquals(TableContentFrame.UpdateColumnListener.class, updateColumnListener.getClass());

        updateColumnListener.setDatabasePersistence(databasePersistenceMock);

        UpdateColumnNamePanel updateColumnNamePanelMock = mock(UpdateColumnNamePanel.class);
        when(updateColumnNamePanelMock.openPopUp(anyString(),anyBoolean())).thenReturn(JOptionPane.CANCEL_OPTION);
        updateColumnListener.setUpdateColumnNamePanel(updateColumnNamePanelMock);

        Table tableBeforeUpdate = databaseManagementSystem.getDatabases().get(0).getTable(tableContentFrame.getSelectedTable());
        tableContentFrame.getBtnUpdateColumnName().doClick();

        assertEquals(tableBeforeUpdate.getColumnNames().size(), tableContentFrame.getTableContent().getRowCount());
        for(String columnName: tableBeforeUpdate.getColumnNames()){

            assertNotNull(databaseManagementSystem.getDatabases().get(0).getTable(tableContentFrame.getSelectedTable()).getColumn(columnName));
        }
    }

    @Test
    public void whenUpdateColumn_OkPressed_ChangeColName() throws DoesNotExist {

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnUpdateColumnName().getActionListeners().length > 0);
        TableContentFrame.UpdateColumnListener updateColumnListener = (TableContentFrame.UpdateColumnListener) tableContentFrame.getBtnUpdateColumnName().getActionListeners()[0];
        assertNotNull(updateColumnListener);
        assertEquals(TableContentFrame.UpdateColumnListener.class, updateColumnListener.getClass());

        updateColumnListener.setDatabasePersistence(databasePersistenceMock);

        UpdateColumnNamePanel updateColumnNamePanelMock = mock(UpdateColumnNamePanel.class);
        when(updateColumnNamePanelMock.openPopUp(anyString(),anyBoolean())).thenReturn(JOptionPane.OK_OPTION);

        JComboBox<Column> lstColumns = new JComboBox(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getData().keySet().toArray());
        lstColumns.setSelectedIndex(0);
        when(updateColumnNamePanelMock.getLstColumns()).thenReturn(lstColumns);
        when(updateColumnNamePanelMock.getTxtNewColumnName()).thenReturn(new JTextField("updated_col_name"));
        updateColumnListener.setUpdateColumnNamePanel(updateColumnNamePanelMock);

        Table tableBeforeUpdate = databaseManagementSystem.getDatabases().get(0).getTable(tableContentFrame.getSelectedTable());
        tableContentFrame.getBtnUpdateColumnName().doClick();

        assertEquals(tableBeforeUpdate.getColumnNames().size(), tableContentFrame.getTableContent().getRowCount());
        for(String columnName: databaseManagementSystem.getDatabases().get(0).getTable(tableContentFrame.getSelectedTable()).getColumnNames()){

            if(tableBeforeUpdate.getColumn(columnName) == null) {
                assertEquals("updated_col_name" , columnName);
            }
        }
    }

    /*
    UpdateField Listener
     */
    @Test
    public void whenUpdateRecord_CancelPressed_DoNothing() throws DoesNotExist, InvalidValue, FieldValueNotSet, InexistentColumn, TypeMismatchException {

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnUpdateFieldValue().getActionListeners().length > 0);
        TableContentFrame.UpdateFieldListener updateFieldListener = (TableContentFrame.UpdateFieldListener) tableContentFrame.getBtnUpdateFieldValue().getActionListeners()[0];
        assertNotNull(updateFieldListener);
        assertEquals(TableContentFrame.UpdateFieldListener.class, updateFieldListener.getClass());

        updateFieldListener.setDatabasePersistence(databasePersistenceMock);

        UpdateFieldPanel updateFieldPanelMock = mock(UpdateFieldPanel.class);
        when(updateFieldPanelMock.openPopUp(anyString(), anyBoolean())).thenReturn(JOptionPane.CANCEL_OPTION);

        updateFieldListener.setUpdateFieldPanel(updateFieldPanelMock);

        Table tableBeforeUpdate = createTableBeforeUpdateField();
        assertNotNull(tableBeforeUpdate);

        tableContentFrame.getBtnUpdateFieldValue().doClick();
        assertEquals(tableBeforeUpdate.getColumnNames().size(), tableContentFrame.getTableContent().getRowCount());

        Table dbTable = databaseManagementSystem.getDatabases().get(0).getTable(tableContentFrame.getSelectedTable());
        for(int i = 0 ; i < tableBeforeUpdate.getNumberOfRows(); i++){

            //Map<Column, Field> row = tableBeforeUpdate.getRow(i);
            for(String colName : tableBeforeUpdate.getColumnNames()){

                if(tableBeforeUpdate.getColumn(colName).getType().equals(Column.Type.INT)) {

                    assertEquals(tableBeforeUpdate.getRow(i).get(tableBeforeUpdate.getColumn(colName)).getIntValue(), dbTable.getRow(i).get(dbTable.getColumn(colName)).getIntValue());
                }else{

                    assertEquals(tableBeforeUpdate.getRow(i).get(tableBeforeUpdate.getColumn(colName)).getStringValue(), dbTable.getRow(i).get(dbTable.getColumn(colName)).getStringValue());
                }
            }
        }
    }

    @Test
    public void whenUpdateRecord_OkPressed_UpdateValues() throws DoesNotExist, InvalidValue, InexistentColumn, TypeMismatchException, FieldValueNotSet {

        assertTrue(tableContent.getColumnCount() > 0);
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getName());
        tableContentFrame.setSelectedTable(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
        assertEnableButtons();

        assertTrue(tableContentFrame.getBtnUpdateFieldValue().getActionListeners().length > 0);
        TableContentFrame.UpdateFieldListener updateFieldListener = (TableContentFrame.UpdateFieldListener) tableContentFrame.getBtnUpdateFieldValue().getActionListeners()[0];
        assertNotNull(updateFieldListener);
        assertEquals(TableContentFrame.UpdateFieldListener.class, updateFieldListener.getClass());

        updateFieldListener.setDatabasePersistence(databasePersistenceMock);

        UpdateFieldPanel updateFieldPanelMock = mock(UpdateFieldPanel.class);
        when(updateFieldPanelMock.openPopUp(anyString(), anyBoolean())).thenReturn(JOptionPane.OK_OPTION);

        JComboBox cbBox = new JComboBox<>(FieldComparator.Sign.values());
        cbBox.setSelectedItem(FieldComparator.Sign.EQUAL);
        when(updateFieldPanelMock.getCbOperators()).thenReturn(cbBox);
        when(updateFieldPanelMock.getTextMatchValue()).thenReturn(new JTextField("1"));
        when(updateFieldPanelMock.getTxtNewValue()).thenReturn(new JTextField("2"));
        JComboBox<Column> lstColumns =  new JComboBox(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getData().keySet().toArray());
        lstColumns.setSelectedItem(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getColumn("TestColumn1"));
        when(updateFieldPanelMock.getLstColumns()).thenReturn(lstColumns);

        updateFieldListener.setUpdateFieldPanel(updateFieldPanelMock);

        Table tableBeforeUpdate = createTableBeforeUpdateField();
        assertNotNull(tableBeforeUpdate);
        assertNotNull(tableBeforeUpdate.getColumn("TestColumn1"));
        assertEquals(tableBeforeUpdate.getNumberOfRows(), databaseManagementSystem.getDatabases().get(0).getTables().get(0).getNumberOfRows());

        tableContentFrame.getBtnUpdateFieldValue().doClick();
        assertEquals(tableBeforeUpdate.getColumnNames().size(), tableContentFrame.getTableContent().getColumnCount() - 1);

        Table dbTable = databaseManagementSystem.getDatabases().get(0).getTable(tableContentFrame.getSelectedTable());

        for(int i = 0 ; i < tableBeforeUpdate.getNumberOfRows(); i++){

            assertNotEquals(tableBeforeUpdate.getRow(i).get(tableBeforeUpdate.getColumn("TestColumn1")).getIntValue(), dbTable.getRow(i).get(dbTable.getColumn("TestColumn1")).getIntValue());
            assertEquals(1, tableBeforeUpdate.getRow(i).get(tableBeforeUpdate.getColumn("TestColumn1")).getIntValue());
            assertEquals(2, dbTable.getRow(i).get(dbTable.getColumn("TestColumn1")).getIntValue());
        }
    }

    public Table createTableBeforeUpdateField() throws FieldValueNotSet, InvalidValue, InexistentColumn, TypeMismatchException, DoesNotExist {

        List<Column> cols = new ArrayList<>();
        for(String colName : databaseManagementSystem.getDatabases().get(0).getTables().get(0).getColumnNames()){

            cols.add(new Column(colName, databaseManagementSystem.getDatabases().get(0).getTables().get(0).getColumn(colName).getType()));
        }

        Table tableBeforeUpdate = new Table(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName(), cols);
        for(int i = 0 ; i < databaseManagementSystem.getDatabases().get(0).getTables().get(0).getNumberOfRows(); i++) {

            Map<Column, Field> dbRow = databaseManagementSystem.getDatabases().get(0).getTables().get(0).getRow(i);
            Map<String, Field> row = new HashMap<>();
            for(Column col : dbRow.keySet()){

                if(dbRow.get(col).isIntValueSet()) {

                    row.put(col.getName(), new Field(dbRow.get(col).getIntValue()));
                }else{

                    row.put(col.getName(), new Field(dbRow.get(col).getStringValue()));
                }
            }

            tableBeforeUpdate.insert(row);
        }

        return tableBeforeUpdate;
    }

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
        tableContentFrame.setSelectedDatabase(databaseManagementSystem.getDatabases().get(0).getTables().get(0).getName());
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
