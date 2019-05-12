package commandline.insert;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class InsertIntoTable extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = String.format(
            "insert into %s from %s value age=10,name=danut,school=negruzzi",
            DataBuilder.TABLE,
            DataBuilder.DATABASE

    );
    private static final String[] wrongCommands = {
            "insert",
            "insert into",
            "insert into inexistent_table",
            "insert into inexistent_table from",
            "insert into inexistent_table from inexistent_database",
            "insert into inexistent_table from inexistent_database value",
            "insert into inexistent_table from inexistent_database value age%10,name=10",
            "insert into inexistent_table from inexistent_database value age%10",
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
