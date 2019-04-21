package main.service;

import main.exception.InvalidCommand;
import main.exception.TypeMismatchException;
import main.model.*;
import main.util.Constants;

import java.util.List;
import java.util.Map;

public class DeleteProcessor {

    public void processDelete(String[] line) throws InvalidCommand {
        if (line.length <= 1)
            throw new InvalidCommand();

        String type = line[1];
        switch (type.toLowerCase()) {
            case Constants.FROM:
                processDeleteFrom(line);
                break;
            case Constants.TABLE:
                processDeleteTable(line);
                break;
            case Constants.DATABASE:
                processDeleteDatabase(line);
                break;
            default:
                throw new InvalidCommand("Missing from/table/database keyword after delete");

        }

    }

    private void processDeleteDatabase(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing database name");
        String name = line[2];
        if (DatabaseManagementSystem.getInstance().getDatabase(name) == null)
            System.out.println("No such database name");
        DatabaseManagementSystem.getInstance().deleteDatabase(name);
    }

    private void processDeleteTable(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing table name");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 4 || !line[4].equalsIgnoreCase(Constants.DATABASE))
            throw new InvalidCommand("Missing database keyword after from keyword");
        if (line.length <= 5)
            throw new InvalidCommand("Missing database name after database keyword");

        Database database = DatabaseManagementSystem.getInstance().getDatabase(line[5]);
        if (database == null)
            throw new InvalidCommand("No such database");
        Table table = database.getTable(line[2]);
        if (table == null)
            throw new InvalidCommand("No such table");

        database.deleteTable(table);
    }

    private void processDeleteFrom(String[] line) throws InvalidCommand {
        if (line.length <= 2)
            throw new InvalidCommand("Missing table name");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length <= 5 || !line[5].equalsIgnoreCase(Constants.WHERE))
            throw new InvalidCommand("Missing where keyword after database name");
        if (line.length <= 8)
            throw new InvalidCommand("Incorrect matching clause after where keyword: ex: age > 10, name = John. Spaces are mandatory!");
        String databaseName = line[4];
        Database database = DatabaseManagementSystem.getInstance().getDatabase(databaseName);
        if (database == null)
            throw new InvalidCommand("No such database");
        Table table = database.getTable(line[2]);
        if (table == null)
            throw new InvalidCommand("No such table");
        String columnName = line[6];
        Column column = table.getColumn(columnName);
        if (column == null)
            throw new InvalidCommand("No such column: " + columnName);
        FieldComparator.Sign sign = FieldComparator.Sign.getEnum(line[7]);
        Field field = new Field();
        String value = line[8];
        if (column.getType().equals(Column.Type.STRING))
            field.setValue(value);
        if (column.getType().equals(Column.Type.INT))
            field.setValue(Integer.parseInt(value));

        try {
            Map<Column, List<Field>> rowsAffected = table.where(column.getName(), sign, field);
            table.deleteRows(rowsAffected);
        } catch (TypeMismatchException e) {
            throw new InvalidCommand(e.getMessage());
        } catch (Exception e) {
            throw new InvalidCommand(e.getMessage());
        }

    }
}
