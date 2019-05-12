package commandline.update;

import main.exception.InvalidCommand;
import main.service.CommandLineParser;
import main.util.DataBuilder;
import model.table.AbstractTableOperationsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UpdateTableName extends AbstractTableOperationsTest {

    CommandLineParser parser = new CommandLineParser();
    public static final String GOOD_COMMAND = "update table "+DataBuilder.TABLE+" from "+DataBuilder.DATABASE+" name new_table_name";
    private static final String[] wrongCommands = {
            "update",
            "update table",
            "update table inexistend_table_name",
            "update table inexistend_table_name from",
            "update table inexistend_table_name from inexitent_database_name",
            "update table inexistend_table_name from inexitent_database_name name",
            "update table inexistend_table_name from inexitent_database_name name invalid,newname",
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
