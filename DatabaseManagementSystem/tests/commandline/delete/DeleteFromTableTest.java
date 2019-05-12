package commandline.delete;

import main.exception.InvalidCommand;
import main.model.FieldComparator;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class DeleteFromTableTest extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND =
            String.format("delete from %s from %s where %s %s %s",
                    DataBuilder.TABLE,
                    DataBuilder.DATABASE,
                    DataBuilder.AGE,
                    "=",
                    "12");
    private static final String[] wrongCommands = {
            "delete",
            "delete from",
            "delete fro",
            "delete from table",
            "delete from invalid_table_name",
            "delete from invalid_table_name from",
            "delete from invalid_table_name frm ",
            "delete from invalid_table_name from INVALID_DATABASE_NAME",
            "delete from invalid_table_name from INVALID_DATABASE_NAME where",
            "delete from invalid_table_name from INVALID_DATABASE_NAME here",
            "delete from invalid_table_name from INVALID_DATABASE_NAME where inexistent_column = 17",
            "delete from invalid_table_name from INVALID_DATABASE_NAME where inexistent_column & string_field",
    };


    @Test
    public void wrongCommands() {
        for (String command : wrongCommands) {
            assertThrows(InvalidCommand.class, () -> {
                System.out.println(command);
                parser.parse(command);
            });

        }
    }

    @Test
    public void sweetCase() {
        try {
            parser.parse(GOOD_COMMAND);
        } catch (InvalidCommand invalidCommand) {
            fail(invalidCommand);
        }
    }
}
