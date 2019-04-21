package main.service;

import main.exception.DoesNotExist;
import main.exception.InvalidCommand;
import main.exception.InvalidValue;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Table;
import main.util.Constants;

public class UpdateProcessor {
    public void processUpdate(String[] line) throws InvalidCommand {
        if (line.length <= 1)
            throw new InvalidCommand();
        String type = line[1];
        switch (type.toLowerCase()) {
            case Constants.DATABASE:
                processUpdateDatabase(line);
                break;
            case Constants.TABLE:
                processUpdateTable(line);
                break;
            default:
                throw new InvalidCommand("Missing from/table/database keyword after delete");

        }
    }

    private void processUpdateTable(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing table name after database keyword");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length <= 5 || line[5].equalsIgnoreCase(Constants.NAME))
            throw new InvalidCommand("Missing name keyword");
        if (line.length <= 6)
            throw new InvalidCommand("Missing table new name after name keyword");
        try {
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[4]);
            Table table = database.getTable(line[2]);
            table.setName(line[6]);
        } catch (DoesNotExist | InvalidValue e) {
            throw new InvalidCommand(e.getMessage());
        }
    }

    public void processUpdateDatabase(String line[]) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing databse name after database keyword");
        if (line.length <= 3 || line[3].equalsIgnoreCase(Constants.NAME))
            throw new InvalidCommand("Missing name keyword");
        if (line.length <= 4)
            throw new InvalidCommand("Missing databse new name after name keyword");
        try {
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[2]);
            database.setName(line[4]);
        } catch (DoesNotExist | InvalidValue e) {
            throw new InvalidCommand(e.getMessage());
        }

    }
}
