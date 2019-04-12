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

    /**
     *
     * @return a list with all databases instances stored
     */
    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    /**
     *
     * @param name - name of database
     * @return true of such database with name exists, false if does not exist
     */
    private boolean exists(String name){
        Database database = getDatabase(name);
        return database != null;
    }

    /**
     *
     * @param name for database
     * @return instance of Database that was just created
     */
    public Database createDatabase(String name){
        if(exists(name))
            return getDatabase(name);
        Database database = new Database(name);
        databases.add(database);
        return database;
    }

    /**
     * adds the parameter into the list of databases
     * @param database instance of Database
     * @return input parameter
     */
    public Database createDatabase(Database database){
        if(databases.contains(database))
            return database;
        databases.add(database);
        return database;
    }

    /**
     * delete database by instance
     * @param database
     */
    public void deleteDatabase(Database database){
        databases.remove(database);
    }

    /**
     * delete database by its name
     * @param name
     */
    public void deleteDatabase(String name){
        Database data = this.getDatabase(name);
        if(data!=null)
            databases.remove(data);
    }

    /**
     * same as exists but returns the instance if exists
     * Uses equalsIgnoreCase for match
     * @param name
     * @return instance of database if exists
     */
    public Database getDatabase(String name){
        for (Database database: databases) {
            if(database.getName().equalsIgnoreCase(name))
                return database;
        }
        return null;
    }
}
