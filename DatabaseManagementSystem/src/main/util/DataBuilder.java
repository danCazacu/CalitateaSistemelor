package main.util;

import main.exception.AlreadyExists;
import main.exception.InexistentColumn;
import main.exception.InvalidValue;
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

    public static final String DATABASE = "DATABASE";
    public static final String TABLE = "TABLE";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String HEIGHT = "height";
    public static final String SCHOOL = "school";

    public static final String[] COLUMNS = {NAME,AGE,HEIGHT,SCHOOL};

    public static Table createTable(String tableName) throws InvalidValue {

        List<Column> columnNames = new ArrayList<>();
        columnNames.add(new Column(NAME, Column.Type.STRING));
        columnNames.add(new Column(AGE, Column.Type.INT));
        columnNames.add(new Column(HEIGHT, Column.Type.INT));
        columnNames.add(new Column(SCHOOL, Column.Type.STRING));
        Table table = new Table(tableName, columnNames);
        return table;
    }

    public static List<Column> buildColumns(){
        List<Column> columnNames = new ArrayList<>();
        try {
            columnNames.add(new Column(NAME, Column.Type.STRING));
            columnNames.add(new Column(AGE, Column.Type.INT));
            columnNames.add(new Column(HEIGHT, Column.Type.INT));
            columnNames.add(new Column(SCHOOL, Column.Type.STRING));
        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        }
        return columnNames;
    }

    public static void buildTestData(){
        try {
            Table table = createTable(TABLE);
            insertDataIntoTable(table);
            DatabaseManagementSystem.getInstance().createDatabase(DATABASE).createTable(table);
        } catch (AlreadyExists alreadyExists) {
            alreadyExists.printStackTrace();
        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        }
    }

    public static void insertDataIntoTable(Table table) throws InvalidValue {
        Map<String, Field> row =  new HashMap<>();
        row.put(NAME,new Field("dan"));
        row.put(AGE,new Field(10));
        row.put(HEIGHT,new Field(187));
        row.put(SCHOOL,new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put(NAME,new Field("andreea"));
        row.put(AGE,new Field(10));
        row.put(HEIGHT,new Field(169));
        row.put(SCHOOL,new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }

        row =  new HashMap<>();
        row.put(NAME,new Field("ilinca"));
        row.put(AGE,new Field(12));
        row.put(HEIGHT,new Field(169));
        row.put(SCHOOL,new Field("INFORMATICA"));
        try {
            table.insert(row);
        } catch (TypeMismatchException | InexistentColumn e) {
            e.printStackTrace();
        }
    }

//    public static void buildeDataOnce(){
//        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
//        List<Column> columnNames = new ArrayList<>();
//        columnNames.add(new Column("name",Column.Type.STRING));
//        columnNames.add(new Column("age",Column.Type.INT));
//        columnNames.add(new Column("height",Column.Type.INT));
//        columnNames.add(new Column("school",Column.Type.STRING));
//        Table table = system.createDatabase("store").createTable("employee",columnNames);
//        Map<String, Field> row =  new HashMap<>();
//        row.put("name",new Field("dan"));
//        row.put("age",new Field(10));
//        row.put("height",new Field(187));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//
//        row =  new HashMap<>();
//        row.put("name",new Field("andreea"));
//        row.put("age",new Field(10));
//        row.put("height",new Field(169));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//
//        row =  new HashMap<>();
//        row.put("name",new Field("ilinca"));
//        row.put("age",new Field(12));
//        row.put("height",new Field(169));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//    }
//    public static void buildeDataSecondTime(){
//        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
//        List<Column> columnNames = new ArrayList<>();
//        columnNames.add(new Column("name",Column.Type.STRING));
//        columnNames.add(new Column("age",Column.Type.INT));
//        columnNames.add(new Column("height",Column.Type.INT));
//        columnNames.add(new Column("school",Column.Type.STRING));
//        Table table = system.createDatabase("faculty").createTable("employee",columnNames);
//        Map<String, Field> row =  new HashMap<>();
//        row.put("name",new Field("dan"));
//        row.put("age",new Field(10));
//        row.put("height",new Field(187));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//
//        row =  new HashMap<>();
//        row.put("name",new Field("andreea"));
//        row.put("age",new Field(10));
//        row.put("height",new Field(169));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//
//        row =  new HashMap<>();
//        row.put("name",new Field("ilinca"));
//        row.put("age",new Field(12));
//        row.put("height",new Field(169));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void buildDataThree(){
//        DatabaseManagementSystem system = DatabaseManagementSystem.getInstance();
//        List<Column> columnNames = new ArrayList<>();
//        columnNames.add(new Column("name",Column.Type.STRING));
//        columnNames.add(new Column("age",Column.Type.INT));
//        columnNames.add(new Column("height",Column.Type.INT));
//        columnNames.add(new Column("school",Column.Type.STRING));
//        Table table = system.createDatabase("store").createTable("persons",columnNames);
//        Map<String, Field> row =  new HashMap<>();
//        row.put("name",new Field("dan"));
//        row.put("age",new Field(10));
//        row.put("height",new Field(187));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//
//        row =  new HashMap<>();
//        row.put("name",new Field("andreea"));
//        row.put("age",new Field(10));
//        row.put("height",new Field(169));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//
//        row =  new HashMap<>();
//        row.put("name",new Field("ilinca"));
//        row.put("age",new Field(12));
//        row.put("height",new Field(169));
//        row.put("school",new Field("INFORMATICA"));
//        try {
//            table.insert(row);
//        } catch (TypeMismatchException | InexistentColumn e) {
//            e.printStackTrace();
//        }
//    }
}
