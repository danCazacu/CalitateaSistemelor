package commandline.insert;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class InsertTable extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = "insert table new_inexistent_table_name into "+ DataBuilder.DATABASE;
    private static final String[] wrongCommands = {
            "insert",
            "insert table",
            "insert table new_table_name",
            "insert table new_table_name into",
            "insert table new_table_name into inexistent_dtata_base_name",
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
