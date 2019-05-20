package main.service;

import main.exception.DoesNotExist;
import main.exception.InvalidCommand;
import main.exception.InvalidValue;
import main.exception.TypeMismatchException;
import main.model.*;
import main.util.Constants;
import org.omg.PortableServer.THREAD_POLICY_ID;

import javax.xml.crypto.Data;

public class UpdateProcessor {
    public void processUpdate(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

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
            case Constants.DATA:
                processUpdateTableData(line);
                break;
            case Constants.COLUMN:
                processUpdateColumn(line);
                break;
            default:
                throw new InvalidCommand("Missing from/table/database keyword after delete");

        }
    }

    private void processUpdateColumn(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing column name after column keyword");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after column name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing table name after from keyword");
        if (line.length <= 5 || !line[5].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 6)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length <= 7 || !line[7].equalsIgnoreCase(Constants.NAME))
            throw new InvalidCommand("Missing name keyword");
        if (line.length <= 8)
            throw new InvalidCommand("Missing column new name after name keyword");

        try{
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[6]);
            Table table = database.getTable(line[4]);
            Column column = table.getColumn(line[2]);
            column.setName(line[8]);
        } catch (DoesNotExist | InvalidValue e) {
            throw new InvalidCommand(e.getMessage());
        }
    }

    private void processUpdateTableData(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing table name after table keyword");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length <= 5 || !line[5].equalsIgnoreCase(Constants.SET))
            throw new InvalidCommand("Missing set keyword after database name");
        if (line.length <= 6)
            throw new InvalidCommand("Missing new value after set keyword: new_value. No spaces!");
        if (line.length > 7) {
            if (!line[7].equalsIgnoreCase(Constants.WHERE))
                throw new InvalidCommand("No where clause");
            if (line.length <= 10)
                throw new InvalidCommand("Incorrect matching clause after where keyword: ex: age > 10, name = John. Spaces are mandatory!");
            try {
                FieldComparator.Sign.getEnum(line[9]);
            }catch (IllegalArgumentException e){
                throw new InvalidCommand("Invalid operator: "+line[9]);
            }
        }

        try{
            Database database = DatabaseManagementSystem.getInstance().getDatabase(line[4]);
            Table table = database.getTable(line[2]);
            if(line.length>7){
                Column column = table.getColumn(line[8]);
                Field newValue = new Field();
                Field field = new Field();
                if(column.getType().equals(Column.Type.STRING)) {
                    newValue.setValue(line[6]);
                    field.setValue(line[10]);
                }
                if(column.getType().equals(Column.Type.INT)) {
                    newValue.setValue(Integer.parseInt(line[6]));
                    field.setValue(Integer.parseInt(line[10]));
                }
                table.updateWhere(column.getName(),FieldComparator.Sign.getEnum(line[9]),field,newValue);
            }
        } catch (DoesNotExist | TypeMismatchException | InvalidValue e) {
            throw new InvalidCommand(e.getMessage());
        }


    }

    private void processUpdateTable(String[] line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing table name after table keyword");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.FROM))
            throw new InvalidCommand("Missing from keyword after table name");
        if (line.length <= 4)
            throw new InvalidCommand("Missing database name after from keyword");
        if (line.length <= 5 || !line[5].equalsIgnoreCase(Constants.NAME))
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

        assert line != null: "Precondition failed: input parameter can not be null";

        if (line.length <= 2)
            throw new InvalidCommand("Missing databse name after database keyword");
        if (line.length <= 3 || !line[3].equalsIgnoreCase(Constants.NAME))
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
