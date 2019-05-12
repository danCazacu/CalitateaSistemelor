package commandline.update;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UpdateColumnName extends AbstractTableOperationsTest {
    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = String.format(
            "update column %s from %s from %s name %s",
            "age",
            DataBuilder.TABLE,
            DataBuilder.DATABASE,
            "new_column_name"
            );
    private static final String[] wrongCommands = {
            "update",
            "update column",
            "update column inexistent_column",
            "update column inexistent_column from ",
            "update column inexistent_column from table",
            "update column inexistent_column from table from",
            "update column inexistent_column from table from database",
            "update column inexistent_column from table from database name",
            "update column inexistent_column from table from database name newcol_name",
            "update column age from table from database name invalid,column_name",
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
