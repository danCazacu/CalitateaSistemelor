package main.service;

import main.exception.*;
import main.model.*;
import main.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InsertProcessor {

    public void processInsert(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 1)
            throw new InvalidCommand();

        String type = line[1];
        switch (type.toLowerCase()) {
            case Constants.INTO:
                processInsertInto(line);
                break;
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

    private void processInsertInto(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing table name");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length <= 5 || !line[5].equalsIgnoreCase(Constants.VALUE))
            throw new InvalidCommand("Missing value keyword after database name");
        if (line.length <= 6) {
            throw new InvalidCommand("Mising data for insert: [column_name1]=[value1],[column_name2]=[value2],[column_name3]=[value3]");
        }

        try {
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[4]);
            Table table = database.getTable(line[2]);
            String val = line[6];
            Map<String, Field> row = new HashMap<>();
            for (String property : val.split(",")) {
                if (property.split("=").length != 2)
                    throw new InvalidCommand("This is not good: " + property);
                String columnName = property.split("=")[0];
                Column column = table.getColumn(columnName);
                String value = property.split("=")[1];
                Field field = new Field();
                if(column.getType().equals(Column.Type.STRING)){
                    field.setValue(value);
                }
                if(column.getType().equals(Column.Type.INT)){
                    field.setValue(Integer.parseInt(value));
                }
                row.put(column.getName(),field);
            }
            table.insert(row);
        } catch (DoesNotExist | TypeMismatchException | InexistentColumn | InvalidValue e) {
            throw new InvalidCommand(e.getMessage());
        }
    }

    private void processInsertDatabase(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing database name");
        String name = line[2];
        try {
            DatabaseManagementSystem.getInstance().createDatabase(name);
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            throw new InvalidCommand(alreadyExists.getMessage());
        }
    }

    private void processInsertTable(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing table name");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.INTO))
            throw new InvalidCommand("Missing into keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name");
        String tableName = line[2];
        try {
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[4]);
            database.createTable(tableName, new ArrayList<>());
        } catch (DoesNotExist | InvalidValue | AlreadyExists e) {
            throw new InvalidCommand(e.getMessage());
        }
    }

    private void processInsertColumn(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

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
        try {
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[7]);
            Table table = database.getTable(tableName);
            table.insertColumn(new Column(columnName, type));
        } catch (DoesNotExist | ColumnAlreadyExists | InvalidValue e) {
            throw new InvalidCommand(e.getMessage());
        }
    }
}
