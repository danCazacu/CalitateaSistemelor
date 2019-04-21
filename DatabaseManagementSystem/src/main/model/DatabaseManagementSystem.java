package main.model;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.persistance.DatabasePersistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import static main.service.FilteringService.validate;

public class DatabaseManagementSystem {
    private List<Database> databases;
    private static DatabaseManagementSystem instance = null;

    public static synchronized DatabaseManagementSystem getInstance() {
        if (instance == null) {
            instance = new DatabaseManagementSystem();
            try {
                new DatabasePersistance().load();
            } catch (IOException | InvalidValue | AlreadyExists e) {
                System.out.println(e.getMessage());
            }
        }
        return instance;
    }

    private DatabaseManagementSystem() {
        this.databases = new ArrayList<>();
    }

    /**
     * @return a list with all databases instances stored
     */
    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    /**
     * @param name - name of database
     * @return true of such database with name exists, false if does not exist
     */
    public boolean exists(String name) {
        Database database = null;
        try {
            database = getDatabase(name);
        } catch (DoesNotExist ignored) {

        }
        return database != null;
    }

    /**
     * @param name for database
     * @return instance of Database that was just created
     */
    public Database createDatabase(String name) throws AlreadyExists, InvalidValue {
        validate(name);
        if (exists(name))
            throw new AlreadyExists(name);
        Database database = new Database(name);
        databases.add(database);
        return database;
    }

    /**
     * adds the parameter into the list of databases
     *
     * @param database instance of Database
     * @return input parameter
     */
    public Database createDatabase(Database database) throws AlreadyExists, InvalidValue {
        validate(database.getName());
        if (databases.contains(database))
            throw new AlreadyExists(database.getName());
        databases.add(database);
        return database;
    }

    /**
     * delete database by instance
     *
     * @param database
     */
    public void deleteDatabase(Database database) throws DoesNotExist {
        if (!databases.contains(database))
            throw new DoesNotExist(database.getName());
        databases.remove(database);
    }

    /**
     * delete database by its name
     *
     * @param name
     */
    public void deleteDatabase(String name) throws DoesNotExist {
        Database data = this.getDatabase(name);
        if (data != null)
            databases.remove(data);
    }

    /**
     * same as exists but returns the instance if exists
     * Uses equalsIgnoreCase for match
     *
     * @param name
     * @return instance of database if exists
     */
    public Database getDatabase(String name) throws DoesNotExist {
        for (Database database : databases) {
            if (database.getName().equalsIgnoreCase(name))
                return database;
        }
        throw new DoesNotExist(name);
    }

    public void persist(OutputStream outputstream) throws IOException {
        for (Database database : databases) {
            database.persist(outputstream);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        new DatabasePersistance().persist();
    }
}
