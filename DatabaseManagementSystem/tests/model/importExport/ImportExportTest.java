
package model.importExport;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.graphicalInterface.database.DatabaseFrame;
import main.model.CsvService;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Table;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests suit for {@link DatabaseFrame}
 */
public class ImportExportTest {

    private static CsvService csvService = new CsvService();
    private DatabaseManagementSystem databaseManagementSystem = DatabaseManagementSystem.getInstance();
    @Test
    public void givenCsvService_WhenCallImportDataLineByLine_ShouldImportTable() throws InvalidValue, AlreadyExists, DoesNotExist {

        databaseManagementSystem.createDatabase("ImportExportTest");
        CsvService.importDataLineByLine("E:\\Facultate\\Master\\Anul I, semestrul II\\Radulescu\\CalitateaSistemelor\\DatabaseManagementSystem\\excel.csv","ImportExportTest" );

        assertNotNull(databaseManagementSystem.getDatabase("ImportExportTest").getTables());

        databaseManagementSystem.deleteDatabase("ImportExportTest");
    }


    @Test
    public void givenCsvService_WhenCallWriteDataLineByLine_ShouldExportTable() throws InvalidValue, AlreadyExists, DoesNotExist {
        databaseManagementSystem.createDatabase("ImportExportTest");
        Database database = databaseManagementSystem.getDatabase("ImportExportTest");
        Table table = DataBuilder.createTable("ExportTest");
        database.createTable(table.getName(), DataBuilder.buildColumns());

        CsvService.writeDataLineByLine("ImportExportTest","ExportTest" );

//        assertDoesNotThrow(() -> { throw new DoesNotExist("ExportTest"); });
        databaseManagementSystem.deleteDatabase("ImportExportTest");
    }
}