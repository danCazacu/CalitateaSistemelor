package main.model;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.persistance.DatabasePersistance;
import main.service.FilteringService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static main.service.FilteringService.isValid;
import static main.service.FilteringService.validate;

public class DatabaseManagementSystem {

    private List<Database> databases;
    private static DatabaseManagementSystem instance = null;
    private static DatabasePersistance databasePersistance;

    public static synchronized DatabaseManagementSystem getInstance() {
        if (instance == null) {
            instance = new DatabaseManagementSystem();
            try {

                databasePersistance = new DatabasePersistance();
                databasePersistance.load();
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

        assert this.databases != null : "Post-condition (get all databases) : the list is null";
        return databases;
    }

    public void setDatabases(List<Database> databases) {

        assert databases != null : "Precondition failed: input parameter is null";
        this.databases = databases;

        assert this.databases.containsAll(databases) : "Post-condition failed: The lists aren't equals";
    }

    /**
     * @param name - name of database
     * @return true of such database with name exists, false if does not exist
     */
    public boolean exists(String name) {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: input is empty or null";
        assert isValid(name) : "Invariant failed: the input name is not valid " + new InvalidValue(name);

        Database database;
        try {
            database = getDatabase(name);
        } catch (DoesNotExist ignored) {
            return false;
        }

        assert database != null && database.getName().equalsIgnoreCase(name) : "Post-condition failed: " + database.getName() + " != " + name;
        return true;
    }

    /**
     * @param name for database
     * @return instance of Database that was just created
     */
    public Database createDatabase(String name) throws AlreadyExists, InvalidValue {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: input is empty or null";
        assert isValid(name): "Precondition failed: the name is not valid " + new InvalidValue(name);

        if (exists(name))
            throw new AlreadyExists(name);

        assert !exists(name) : "Precondition failed (create database(name)) ... " + new AlreadyExists(name);

        Database database = new Database(name);
        databases.add(database);

        assert exists(name) : "Post-condition failed (create database(name)) ... The database with name \"" + name + "\" was not found in databases list.";
        return database;
    }

    /**
     * adds the parameter into the list of databases
     *
     * @param database instance of Database
     * @return input parameter
     */
    public Database createDatabase(Database database) throws AlreadyExists, InvalidValue {
        assert database != null : "Precondition failed: input is null";

        assert isValid(database.getName()) : "Invariant failed : database name \"" + database.getName() + "\" is not valid";
        //validate(database.getName());

     /*   if (databases.contains(database))
            throw new AlreadyExists(database.getName());*/

        assert !databases.contains(database) : "Precondition failed (create database(Database)) ... " + new AlreadyExists(database.getName());

        databases.add(database);

        assert databases.contains(database) : "Post-condition failed (create database(Database)) ... The database was not found in databases list.";

        return database;
    }

    /**
     * delete database by instance
     *
     * @param database
     */
    public void deleteDatabase(Database database) throws DoesNotExist {

        assert database != null : "Precondition failed: input is null";

        if (!databases.contains(database))
            throw new DoesNotExist(database.getName());

        assert isValid(database.getName()) : "Invariant failed : database name \"" + database.getName() + "\" is not valid";
        assert databases.contains(database) : "Precondition failed (delete database(Database)) ... " + new DoesNotExist(database.getName());

        databases.remove(database);

        assert !databases.contains(database) : "Post-condition failed (delete database(Database)) ... The database was still found in databases list.";
    }

    /**
     * delete database by its name
     *
     * @param name
     */
    public void deleteDatabase(String name) throws DoesNotExist {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: input is empty or null";
        assert isValid(name) : "Invariant failed : database name \"" + name + "\" is not valid";
        Database data = this.getDatabase(name);

        assert data != null : "Precondition failed (delete database(name)) ... " + new DoesNotExist(name);
        //if (data != null)
        databases.remove(data);

        assert !databases.contains(name): "Post-condition failed (delete database(Database)) ... The database was still found in databases list.";
    }

    /**
     * same as exists but returns the instance if exists
     * Uses equalsIgnoreCase for match
     *
     * @param name
     * @return instance of database if exists
     */
    public Database getDatabase(String name) throws DoesNotExist {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: input name si null or empty";
        for (Database database : databases) {

            assert isValid(database.getName()) : "Invariant failed: database name \"" + database.getName() + "\" is not valid";
            assert database != null  : "Invariant failed: database null";

            if (database.getName().equalsIgnoreCase(name)) {

                assert database.getName().equalsIgnoreCase(name): "Post-condition failed ... the name aren't equals";
                return database;
            }
        }

        //assert !exists(name) : "Post-condition failed (second way of exits from this function failed): database with the name \"" + name + "\" exists";
        throw new DoesNotExist(name);
    }

    public void persist(OutputStream outputstream) throws IOException {
        for (Database database : databases) {
            database.persist(outputstream);
        }
    }

    @Override
    protected void finalize() throws Throwable {

        databasePersistance.persist();
    }

    public void setDatabasePersistance(DatabasePersistance databasePersistance){

        this.databasePersistance = databasePersistance;
    }
}
