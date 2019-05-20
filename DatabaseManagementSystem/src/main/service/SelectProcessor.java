package main.service;

import main.exception.DoesNotExist;
import main.exception.InvalidCommand;
import main.exception.InvalidValue;
import main.exception.TypeMismatchException;
import main.model.*;
import main.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectProcessor {

    public void processSelect(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 1)
            throw new InvalidCommand("Missing column list. ex age,name,height or *");
        if (line.length <= 2 || !line[2].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after column list");
        if (line.length <= 3)
            throw new InvalidCommand("Missing table name after from keyword");
        if (line.length <= 4 || !line[4].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 5)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length > 6) {
            if (!line[6].equalsIgnoreCase(Constants.WHERE))
                throw new InvalidCommand("No where clause");
            if (line.length <= 9)
                throw new InvalidCommand("Incorrect matching clause after where keyword: ex: age > 10, name = John. Spaces are mandatory!");
            try {
                FieldComparator.Sign.getEnum(line[8]);
            }catch (IllegalArgumentException e){
                throw new InvalidCommand("Invalid operator: "+line[8]);
            }
        }
        String tableName = line[3];
        String databaseName = line[5];
        try {
            Database database = DatabaseManagementSystem.getInstance().getDatabase(databaseName);
            Table table = database.getTable(tableName);
            String columns = line[1];
            List<Column> columnsSelected = new ArrayList<>();
            if (!columns.equalsIgnoreCase("*")) {
                String[] names = columns.split(",");
                for (String name : names) {
                    Column column = table.getColumn(name);
                    columnsSelected.add(column);
                }
            } else {
                for (String name : table.getColumnNames()) {
                    columnsSelected.add(table.getColumn(name));
                }
            }
            Map<Column, List<Field>> affectedColumns = null;
            if (line.length == 10) { //means we have a where clause
                String columnName = line[7];
                Column column = table.getColumn(columnName);
                FieldComparator.Sign sign = FieldComparator.Sign.getEnum(line[8]);
                Field field = new Field();
                String value = line[9];
                if (column.getType().equals(Column.Type.STRING))
                    field.setValue(value);
                if (column.getType().equals(Column.Type.INT))
                    field.setValue(Integer.parseInt(value));

                affectedColumns = table.where(column.getName(), sign, field);
//                Map<Column, List<Field>> result = new HashMap<>();
//                for (Column col : columnsSelected) {
//                    result.put(col, affectedColumns.get(col));
//                }
            } else {
                affectedColumns = table.getData();
            }
            System.out.println(Table.toString(table.select(affectedColumns, columnsSelected)));


        } catch (DoesNotExist | InvalidValue | TypeMismatchException e) {
            throw new InvalidCommand(e.getMessage());
        }
    }
}
