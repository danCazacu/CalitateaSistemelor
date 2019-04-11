package main.model;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManagementSystem {
    private List<Database> databases;
    private static DatabaseManagementSystem instance = null;
    public static synchronized DatabaseManagementSystem getInstance(){
        if(instance == null)
            instance = new DatabaseManagementSystem();
        return instance;
    }
    private DatabaseManagementSystem() {
        this.databases = new ArrayList<>();
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    public Database createDatabase(String name){
        Database database = new Database(name);
        databases.add(database);
        return database;
    }
    public Database createDatabase(Database database){
        databases.add(database);
        return database;
    }
    public void deleteDatabase(Database database){
        databases.remove(database);
    }
    public void deleteDatabase(String name){
        Database data = this.getDatabase(name);
        if(data!=null)
            databases.remove(data);
    }
    public Database getDatabase(String name){
        for (Database database: databases) {
            if(database.getName().equals(name))
                return database;
        }
        return null;
    }
}
