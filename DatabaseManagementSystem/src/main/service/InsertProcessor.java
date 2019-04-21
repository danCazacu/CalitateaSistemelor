package main.service;

import main.exception.ColumnAlreadyExists;
import main.exception.InvalidCommand;
import main.model.Column;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Table;
import main.util.Constants;

import java.util.ArrayList;

public class InsertProcessor {

    public void processInsert(String[] line) throws InvalidCommand {
        if (line.length <= 1)
            throw new InvalidCommand();

        String type = line[1];
        switch (type.toLowerCase()) {
            case Constants.INTO:
//                processInsertInto(line);
//                break;
                throw new InvalidCommand("Not yet implemented");
            case Constants.TABLE:
                processInsertTable(line);
                break;
            case Constants.DATABASE:
                processInsertDatabase(line);
                break;
            case Constants.COLUMN:
                processInsertColumn(line);
                break;
            default:
                throw new InvalidCommand("Mission into/table/database/column keyword after delete");

        }
    }

    private void processInsertDatabase(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing database name");
        String name = line[2];
        if (DatabaseManagementSystem.getInstance().getDatabase(name) != null)
            throw new InvalidCommand("Such database already exists");
        DatabaseManagementSystem.getInstance().createDatabase(name);
    }

    private void processInsertTable(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing table name");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.INTO))
            throw new InvalidCommand("Missing into keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name");
        String tableName = line[2];
        Database database = DatabaseManagementSystem.getInstance().getDatabase(line[4]);
        if (database == null)
            throw new InvalidCommand("No such database");
        if (database.getTable(tableName) != null)
            throw new InvalidCommand("Such table already exists");
        database.createTable(tableName, new ArrayList<>());
    }

    private void processInsertColumn(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing column name after keyword column");
        if (line.length <= 3)
            throw new InvalidCommand("Missing column type after keyword column");
        if (!line[3].equalsIgnoreCase("int") && !line[3].equalsIgnoreCase("string"))
            throw new InvalidCommand("Invalid column type after keyword column");
        if (line.length <= 4 || !line[4].equalsIgnoreCase(Constants.INTO))
            throw new InvalidCommand("Missing into keyword after keyword column");
        if (line.length <= 5)
            throw new InvalidCommand("Missing table name");
        if (line.length <= 6 || !line[6].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 7)
            throw new InvalidCommand("Missing database name after from keyword");

        String columnName = line[2];
        String tableName = line[5];
        Column.Type type = Column.Type.getEnum(line[3].toLowerCase());

        Database database = DatabaseManagementSystem.getInstance().getDatabase(line[7]);
        if (database == null)
            throw new InvalidCommand("No such database");
        Table table = database.getTable(tableName);
        if (table == null)
            throw new InvalidCommand("No such table");

        try {
            table.insertColumn(new Column(columnName, type));
        } catch (ColumnAlreadyExists columnAlreadyExists) {
            throw new InvalidCommand(columnAlreadyExists.getMessage());
        }

    }
}