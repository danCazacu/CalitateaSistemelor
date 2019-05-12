package commandline.select;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SelectTest extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String[] GOOD_COMMAND = {String.format(
            "select age,height,name from %s from %s where age = 12",
            DataBuilder.TABLE,
            DataBuilder.DATABASE),

            String.format(
            "select * from %s from %s where age = 12",
            DataBuilder.TABLE,
            DataBuilder.DATABASE),

            String.format(
                    "select * from %s from %s",
                    DataBuilder.TABLE,
                    DataBuilder.DATABASE)
    };

    private static final String[] wrongCommands = {
            "select",
            "select *",
            "select * from",
            "select * from inexistent_table_name",
            "select * from inexistent_table_name from",
            "select * from inexistent_table_name from inexistent_database_name ",
            "select * from inexistent_table_name from inexistent_database_name lala",
            "select * from inexistent_table_name from inexistent_database_name where ",
            "select * from inexistent_table_name from inexistent_database_name where age ",
            "select * from inexistent_table_name from inexistent_database_name where name * dan",
            "select status from " + DataBuilder.TABLE + " from " + DataBuilder.DATABASE + " where name = dan",
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
