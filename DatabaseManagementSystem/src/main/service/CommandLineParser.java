package main.service;

import main.exception.InvalidCommand;
import main.exception.TypeMismatchException;
import main.model.*;
import main.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandLineParser {

    public void startCommandLine() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                parse(scanner.nextLine());
            } catch (InvalidCommand invalidCommand) {
                System.out.println(invalidCommand.getMessage());
            }
        }
    }

    private void parse(String line) throws InvalidCommand {
        //this is just ideas, tried to do it but seems to much to do it at work
        String[] split = line.split("\\s+");
        if (split.length > 0) {
            String action = split[0];
            processAction(action, split);
        } else {
            throw new InvalidCommand();
        }

    }

    private void processAction(String action, String[] line) throws InvalidCommand {
        switch (action.toLowerCase()) {
            case Constants.INSERT:
                processInsert(line);
                break;
            case Constants.DELETE:
                processDelete(line);
                break;
            case Constants.UPDATE:
                processUpdate(line);
                break;
            case Constants.SELECT:
                processSelect(line);
                break;
            default:
                throw new InvalidCommand();
        }
    }

    private void processInsert(String[] line) {

    }

    private void processDelete(String[] line) throws InvalidCommand {
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
                throw new InvalidCommand("Mission from/table/database keyword after delete");

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
        if (line.length <= 3)
            throw new InvalidCommand("Mission from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Mission database keyword after from keyword");
        if (line.length <= 5)
            throw new InvalidCommand("Mission database name after database keyword");

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
        if (line.length <= 3 && !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Mission from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Mission database keyword after from keyword");
        if (line.length <= 5 && !line[5].equalsIgnoreCase(Constants.WHERE))
            throw new InvalidCommand("Mission where keyword after database name");
        if (line.length <= 8)
            throw new InvalidCommand("Incorrect matching clause after where keyword: ex: age > 10, name = John. Spaces are mandatory!");
        String databaseName = line[5];
        Database database = DatabaseManagementSystem.getInstance().getDatabase(databaseName);
        if (database == null)
            throw new InvalidCommand("No such database");
        Table table = database.getTable(line[2]);
        if (table == null)
            throw new InvalidCommand("No such table");
        String columnName = line[6];
        Column column = table.getColumn(columnName);
        FieldComparator.Sign sign = FieldComparator.Sign.getEnum(line[7]);
        Field field = new Field();
        String value = line[8];
        if(column.getType().equals(Column.Type.STRING))
            field.setValue(value);
        if(column.getType().equals(Column.Type.INT))
            field.setValue(Integer.parseInt(value));

        try {
            Map<Column, List<Field>> rowsAffected = table.where(column.getName(),sign,field);
            table.deleteRows(rowsAffected);
        } catch (TypeMismatchException e) {
            throw new InvalidCommand(e.getMessage());
        } catch (Exception e) {
            throw new InvalidCommand(e.getMessage());
        }

    }

    private void processUpdate(String[] line) {

    }

    private void processSelect(String[] line) {

    }

}
