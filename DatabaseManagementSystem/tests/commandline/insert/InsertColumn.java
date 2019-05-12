package commandline.insert;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class InsertColumn extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = "insert column new_column_name int into "+DataBuilder.TABLE+" from "+ DataBuilder.DATABASE;
    private static final String[] wrongCommands = {
            "insert",
            "insert column",
            "insert column new_column_name",
            "insert column new_column_name string",
            "insert column new_column_name str",
            "insert column new_column_name string into ",
            "insert column new_column_name string into inexistent_tablename",
            "insert column new_column_name string into inexistent_tablename from",
            "insert column new_column_name string into inexistent_tablename from inexistent_database_name",
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
