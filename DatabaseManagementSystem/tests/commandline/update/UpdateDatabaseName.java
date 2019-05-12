package commandline.update;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UpdateDatabaseName extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = "update database "+DataBuilder.DATABASE+" name new_database_name";
    private static final String[] wrongCommands = {
            "update",
            "update database",
            "update database invalid_database_name",
            "update database invalid_database_name name",
            "update database invalid_database_name name invalid,new\"databasebane",
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
