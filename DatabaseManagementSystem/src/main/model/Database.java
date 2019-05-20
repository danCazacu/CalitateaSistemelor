package main.model;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.persistance.PersistenceContants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static main.service.FilteringService.isValid;
import static main.service.FilteringService.validate;
public class Database {
    private String name;
    private List<Table> tables;

    public Database(String name) throws InvalidValue {
        //validate(name);
        assert name != null && !name.trim().isEmpty() : "Precondition failed: input is null or empty";
        assert isValid(name) : "Precondition failed: " + new InvalidValue(name);

        this.name = name;
        this.tables = new ArrayList<>();

        assert this.name.equalsIgnoreCase(name): "Post-condition failed: database name incorrect ( " + this.name + " != " + name + ")";
        assert this.tables != null : "Post-condition failed: tables list is null";
        assert this.tables.size() == 0 : "Post-condition failed: tables list not empty list";
    }

    public Database(String name, List<Table> tables) throws InvalidValue {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: input is null or empty";
        assert isValid(name) : "Precondition failed: " + new InvalidValue(name);
        assert tables != null : "Precondition failed: tables list is null";

        this.tables = tables;
        this.name = name;

        assert this.name.equalsIgnoreCase(name) : "Post-condition failed: " + this.name + " != " + name ;
        assert this.tables.containsAll(tables) : "Post-condition failed: the tables list does not contain all the tables from the input list";
    }

    public List<Table> getTables() {

        assert tables != null;
        return tables;
    }

    public void setTables(List<Table> tables) {

        assert tables != null : "Precondition failed: input is null";
        this.tables = tables;

        assert tables.containsAll(tables) : "Post-condition failed ... the tables list does not contain all the tables from the input list";
    }

    public String getName() {

        assert name != null && !name.trim().isEmpty();
        return name;
    }

    /**
     * Use this to change the name of database
     * @param name
     */
    public void setName(String name) throws InvalidValue {
        assert isValid(name) : "Precondition failed... invalid name... " + new InvalidValue(name);
        this.name = name;
        assert this.name.equals(name): "Post-condition failed ... " + this.name + " != " + name;
    }

    /**
     *
     * @param tableName
     * @return true if this database contains such table, false if not
     */
    public boolean exists(String tableName){

        assert tableName != null && !tableName.trim().isEmpty() : "Precondition failed: null or empty input";
        assert isValid(tableName) : "Invariant failed  " + new InvalidValue(tableName);
        Table table;
        try {

            table = getTable(tableName);
        } catch (DoesNotExist ignored) {

            return false;
        }

        assert table != null && table.getName().equalsIgnoreCase(tableName) : "Post-condition failed: table null or different values (" + table.getName() + " != " + tableName + ")";
        return true;
    }

    public Table createTable(String name, List<Column> columnNames) throws AlreadyExists, InvalidValue {

        assert name != null && !name.trim().isEmpty() : "Precondition failed: null or empty input name";
        assert isValid(name) : "Precondition failed  " + new InvalidValue(name);
        assert columnNames != null : "Precondition failed null columns list";

        Table exists = null;
        try {
            exists = getTable(name);
        } catch (DoesNotExist ignored) {

        }
        if (exists != null)
            throw new AlreadyExists(exists.getName());

        Table table = createTable(new Table(name, columnNames));
        assert this.exists(name): "Post-condition failed: table was not created";
        return table;
    }

    public Table createTable(Table table) throws AlreadyExists, InvalidValue {

        assert table != null : "Precondition failed: the input is null";
        assert table.getName() != null && !table.getName().trim().isEmpty() : "Precondition failed: null or empty input name";
        assert isValid(table.getName()) : "Precondition failed  " + new InvalidValue(table.getName());

       /* if (tables.contains(table))
            throw new AlreadyExists(table.getName());*/

        assert !tables.contains(table): new AlreadyExists(table.getName());
        tables.add(table);

        assert tables.contains(table) : "Post-condition failed: table was not created";
        return table;
    }

    /**
     * delete table by instance
     * @param table
     */
    public void deleteTable(Table table) throws DoesNotExist {

        assert table != null : "Precondition failed: the input is null";

        if(!tables.contains(table))
            throw new DoesNotExist(table.getName());
        tables.remove(table);

        assert !tables.contains(table): "Post-condition table was not deleted";
    }

    /**
     * delete table by its name
     * @param tableName
     */
    public void deleteTable(String tableName) throws DoesNotExist {

        assert tableName != null && !tableName.trim().isEmpty() : "Precondition failed: the input is null or empty";
        assert isValid(tableName) : "Invariant failed: " + new InvalidValue(tableName);

        Table table = this.getTable(tableName);
        if (table != null)
            deleteTable(table);

        assert !this.exists(tableName) : "Post-condition failed, table was not deleted";
    }

    /**
     *
     * @param name
     * @return Table instance that has the name in parameter, returns NULL if no such table
     */
    public Table getTable(String name) throws DoesNotExist {

        assert name != null && !name.trim().isEmpty(): "Pre-condition failed: input null or empty ";
        assert isValid(name) : "Precondition failed " + new InvalidValue(name);

        for (Table table : tables) {

            assert isValid(table.getName()) : "Invariant failed " + new InvalidValue(table.getName());
            if (table.getName().equalsIgnoreCase(name)) {

                assert table.getName().equalsIgnoreCase(name) : "Post-condition failed ... " + table.getName() + " != " + name;
                return table;
            }
        }

        //assert exists(name) : "Post-condition failed (second way of exits from this function failed): table with the name \"" + name + "\" exists";
        throw new DoesNotExist(name);
    }
    @Override
    public int hashCode() {
        int superHash = super.hashCode();
        superHash += this.name.hashCode();
        for (Table table : tables) {
            superHash += table.hashCode();
        }
        return superHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Database))
            return false;
        Database database = (Database) obj;
        if (!this.name.equalsIgnoreCase(database.name))
            return false;
        for (Table table : this.tables) {
            if (!database.tables.contains(table))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public void persist(OutputStream outputstream) throws IOException {
        outputstream.write(PersistenceContants.START_DATABASE.getBytes());
        outputstream.write("\n".getBytes());
        outputstream.write(this.getName().getBytes());
        outputstream.write("\n".getBytes());
        for (Table table: tables) {
            table.persist(outputstream);
        }
        outputstream.write(PersistenceContants.END_DATABASE.getBytes());
        outputstream.write("\n".getBytes());
    }
}
