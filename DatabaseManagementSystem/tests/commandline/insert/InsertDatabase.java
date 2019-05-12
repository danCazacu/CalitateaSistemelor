package commandline.insert;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class InsertDatabase extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = "insert database NEW_NOT_HERE_BEFORE_DATABASE_NAME";
    private static final String[] wrongCommands = {
            "",
            "no",
            "insert",
            "insert nimic",
            "insert database",
            "insert database "+DataBuilder.DATABASE,
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
