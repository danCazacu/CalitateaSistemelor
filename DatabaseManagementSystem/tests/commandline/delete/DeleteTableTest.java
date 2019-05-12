package commandline.delete;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class DeleteTableTest extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = "delete table "+DataBuilder.TABLE+" from database "+ DataBuilder.DATABASE;
    private static final String[] wrongCommands = {
            "delete",
            "delete table",
            "delete table invalid_table_name",
            "delete table invalid_table_name from ",
            "delete table invalid_table_name from database",
            "delete table invalid_table_name from database "+DataBuilder.DATABASE,
            "delete table invalid_table_name from database INVALID_DATABASE_NAME",
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
