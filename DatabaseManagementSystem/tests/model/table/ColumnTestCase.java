package model.table;

import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.Column;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ColumnTestCase extends AbstractTableOperationsTest {

    private static final String NEW_COLUMN_NAME = "NEW_COLUMN_NAME";

    @Test
    public void deleteColumn() {
//        try {
//            Column colum = new Column(NEW_COLUMN_NAME, Column.Type.INT);
//        } catch (InvalidValue invalidValue) {
//            fail(invalidValue);
//        }

        try {
            table.deleteColumn(table.getColumn(DataBuilder.COLUMNS[0]));
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }

        assertThrows(DoesNotExist.class,()->{
            table.getColumn(DataBuilder.COLUMNS[0]);
        });
    }
}
