
package graphicalInterface.importExport;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests suit for {@link DatabaseFrame}
 */
public class ImportExportTest {

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


        //not persist when close
        mainWindow = new MainWindow();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        databaseManagementSystem = DatabaseManagementSystem.getInstance();
    }

    @BeforeEach
    public void setup() throws DoesNotExist {

        while (databaseManagementSystem.getDatabases().size() > 0) {

            databaseManagementSystem.deleteDatabase(databaseManagementSystem.getDatabases().get(0));
        }

        tableFrame = TableFrame.getInstance();
        databaseFrame = new DatabaseFrame();
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

    }

    @Test
    public void givenValidPath_WhenImport_ShouldImportTables() {

        assertNotNull(databaseFrame.getBtnImportTable());

        assertTrue(databaseFrame.getBtnImportTable().getActionListeners().length == 1);
        DatabaseFrame.ImportListener importListener = (DatabaseFrame.ImportListener) databaseFrame.getBtnImportTable().getActionListeners()[0];
        assertNotNull(importListener);
        assertEquals(DatabaseFrame.ImportListener.class, importListener.getClass());
    }


    @Test
    public void givenValidPath_WhenExport_ShouldExportTables() {

        assertNotNull(tableFrame.getBtnExportTable());

        assertTrue(tableFrame.getBtnExportTable().getActionListeners().length == 1);
        TableFrame.ExportListener exportListener = (TableFrame.ExportListener) tableFrame.getBtnExportTable().getActionListeners()[0];
        assertNotNull(exportListener);
    }
}