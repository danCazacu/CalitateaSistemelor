package database;

import main.exception.InexistentColumn;
import main.exception.WrongTypeInColumnException;
import main.model.Column;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Field;
import main.model.Table;
import main.query.Query;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DevTest {

    @Test
    public void createTableInsertDataTest(){
        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
        List<Column> columnNames = new ArrayList<>();
        columnNames.add(new Column("name",Column.Type.STRING));
        columnNames.add(new Column("age",Column.Type.INT));
        columnNames.add(new Column("height",Column.Type.INT));
        Table table = system.createDatabase("store").createTable("employee",columnNames);
        Map<String, Field> row =  new HashMap<>();
        row.put("name",new Field("dan"));
        row.put("age",new Field(10));
        row.put("height",new Field(187));
//        row.put("blabla",new Field(10));
        try {
            table.insert(row);
        } catch (WrongTypeInColumnException e) {
            e.printStackTrace();
        } catch (InexistentColumn inexistentColumn) {
            inexistentColumn.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("andreea"));
        row.put("age",new Field(10));
        row.put("height",new Field(169));
//        row.put("blabla",new Field(10));
        try {
            table.insert(row);
        } catch (WrongTypeInColumnException e) {
            e.printStackTrace();
        } catch (InexistentColumn inexistentColumn) {
            inexistentColumn.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("ilinca"));
        row.put("age",new Field(12));
        row.put("height",new Field(169));
//        row.put("blabla",new Field(10));
        try {
            table.insert(row);
        } catch (WrongTypeInColumnException e) {
            e.printStackTrace();
        } catch (InexistentColumn inexistentColumn) {
            inexistentColumn.printStackTrace();
        }


        List<String> columnsAsString = table.getColumnNames();
        columnsAsString.remove("age");
        List<Column> columns = table.getColumnsByColumnNames(table.getColumnNames());
        Database store = system.getDatabase("store");
        try {
            Map<Column,List<Field>> result = store.select(columns).from("employee").whereEquals("name",new Field("dan"));
        } catch (WrongTypeInColumnException e) {
            e.printStackTrace();
        }

        try {
            Map<Column,List<Field>> result = store.select(columns).from("employee").whereEquals("name",new Field(10));
        } catch (WrongTypeInColumnException e) {
            e.printStackTrace();
        }



    }
}
