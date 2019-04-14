package main.util;

import main.exception.InexistentColumn;
import main.exception.TypeMismatchException;
import main.model.Column;
import main.model.DatabaseManagementSystem;
import main.model.Field;
import main.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBuilder {

    public static void buildeDataOnce(){
        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
        List<Column> columnNames = new ArrayList<>();
        columnNames.add(new Column("name",Column.Type.STRING));
        columnNames.add(new Column("age",Column.Type.INT));
        columnNames.add(new Column("height",Column.Type.INT));
        columnNames.add(new Column("school",Column.Type.STRING));
        Table table = system.createDatabase("store").createTable("employee",columnNames);
        Map<String, Field> row =  new HashMap<>();
        row.put("name",new Field("dan"));
        row.put("age",new Field(10));
        row.put("height",new Field(187));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("andreea"));
        row.put("age",new Field(10));
        row.put("height",new Field(169));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("ilinca"));
        row.put("age",new Field(12));
        row.put("height",new Field(169));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }
    }
    public static void buildeDataSecondTime(){
        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
        List<Column> columnNames = new ArrayList<>();
        columnNames.add(new Column("name",Column.Type.STRING));
        columnNames.add(new Column("age",Column.Type.INT));
        columnNames.add(new Column("height",Column.Type.INT));
        columnNames.add(new Column("school",Column.Type.STRING));
        Table table = system.createDatabase("faculty").createTable("employee",columnNames);
        Map<String, Field> row =  new HashMap<>();
        row.put("name",new Field("dan"));
        row.put("age",new Field(10));
        row.put("height",new Field(187));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("andreea"));
        row.put("age",new Field(10));
        row.put("height",new Field(169));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("ilinca"));
        row.put("age",new Field(12));
        row.put("height",new Field(169));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }
    }

    public static void buildDataThree(){
        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
        List<Column> columnNames = new ArrayList<>();
        columnNames.add(new Column("name",Column.Type.STRING));
        columnNames.add(new Column("age",Column.Type.INT));
        columnNames.add(new Column("height",Column.Type.INT));
        columnNames.add(new Column("school",Column.Type.STRING));
        Table table = system.createDatabase("store").createTable("persons",columnNames);
        Map<String, Field> row =  new HashMap<>();
        row.put("name",new Field("dan"));
        row.put("age",new Field(10));
        row.put("height",new Field(187));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("andreea"));
        row.put("age",new Field(10));
        row.put("height",new Field(169));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put("name",new Field("ilinca"));
        row.put("age",new Field(12));
        row.put("height",new Field(169));
        row.put("school",new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }
    }
}
