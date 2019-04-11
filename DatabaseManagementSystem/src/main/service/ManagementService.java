package main.service;

import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;

@Deprecated
public class ManagementService {
    private static ManagementService ourInstance = null;
    DatabaseManagementSystem databaseManagementSystem;

    public static synchronized ManagementService getInstance(String path) {
        if(ourInstance==null)
            ourInstance = new ManagementService(path);
        return ourInstance;

    }
    private ManagementService(String path) {
        databaseManagementSystem = new DatabasePersistance().load("path");
    }


    public Database createDatabase(String name){
        return databaseManagementSystem.createDatabase(name);
    }
    public void deleteDatabase(String name){
        databaseManagementSystem.deleteDatabase(name);
    }
    public Database getDatabase(String name){
        return databaseManagementSystem.getDatabase(name);
    }


//    public Table createTable(String databaseName,String tableName){
//        return databaseManagementSystem.getDatabase(databaseName).createTable(tableName);
//    }
//
//    public void deleteTable(String databaseName,String tableName){
//        databaseManagementSystem.getDatabase(databaseName).deleteTable(tableName);
//    }
//
//    public Record insertRow(String databaseName, String tableName, Record row){
//        Database database = databaseManagementSystem.getDatabase(databaseName);
//        Table table = database.getTable(tableName);
//        return table.createRecord(row);
//    }
//    public void deleteRow(String databaseName, String tableName, Record row){
//        Database database = databaseManagementSystem.getDatabase(databaseName);
//        Table table = database.getTable(tableName);
//        table.deleteRecord(row);
//    }


}
