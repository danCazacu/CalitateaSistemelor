package commandline.update;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UpdateData extends AbstractTableOperationsTest {
    CommandLineParser parser = new CommandLineParser();

    public static final String[] GOOD_COMMAND = {String.format(
            "update data %s from %s set 18 where age = 12",
            DataBuilder.TABLE,
            DataBuilder.DATABASE
    ),String.format(
            "update data %s from %s set mihai where name = dan",
            DataBuilder.TABLE,
            DataBuilder.DATABASE
    )
    };
    private static final String[] wrongCommands = {
            "update",
            "update data",
            "update dat",
            "update data table",
            "update data table from",
            "update data table frm",
            "update data table from databse",
            "update data table from databse set",
            "update data table from databse st",
            "update data table from databse set string",
            "update data table from databse set string wgere",
            "update data table from databse set string where",
            "update data table from databse set string where age = string",
            "update data table from databse set string where age = 12",
            "update data table from databse set string where age & 12",
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
            for (String command : GOOD_COMMAND) {
                parser.parse(command);
            }
        } catch (InvalidCommand invalidCommand) {
            fail(invalidCommand);
        }
    }
}
