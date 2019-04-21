package main.model;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.persistance.PersistenceContants;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import static main.service.FilteringService.validate;
public class Database {
    private String name;
    private List<Table> tables;

    public Database(String name) throws InvalidValue {
        validate(name);
        this.name = name;
        this.tables = new ArrayList<>();
    }

    public Database(String name, List<Table> tables) throws InvalidValue {
        validate(name);
        this.tables = tables;
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public String getName() {
        return name;
    }

    /**
     * Use this to change the name of database
     * @param name
     */
    public void setName(String name) throws InvalidValue {
        validate(name);
        this.name = name;
    }

    /**
     *
     * @param tableName
     * @return true if this database contains such table, false if not
     */
    public boolean exists(String tableName){
        Table table = null;
        try {
            table = getTable(tableName);
        } catch (DoesNotExist ignored) {

        }
        return table!=null;
    }

    public Table createTable(String name, List<Column> columnNames) throws AlreadyExists, InvalidValue {
        validate(name);
        Table exists = null;
        try {
            exists = getTable(name);
        } catch (DoesNotExist ignored) {

        }
        if (exists != null)
            throw new AlreadyExists(exists.getName());
        return createTable(new Table(name,columnNames));
    }

    public Table createTable(Table table) throws AlreadyExists, InvalidValue {
        validate(table.getName());
        if (tables.contains(table))
            throw new AlreadyExists(table.getName());
        tables.add(table);
        return table;
    }

    /**
     * delete table by instance
     * @param table
     */
    public void deleteTable(Table table) throws DoesNotExist {
        if(!tables.contains(table))
            throw new DoesNotExist(table.getName());
        tables.remove(table);
    }

    /**
     * delete table by its name
     * @param tableName
     */
    public void deleteTable(String tableName) throws DoesNotExist {
        Table table = this.getTable(tableName);
        if (table != null)
            deleteTable(table);
    }

    /**
     *
     * @param name
     * @return Table instance that has the name in paramter, returns NULL if no such table
     */
    public Table getTable(String name) throws DoesNotExist {
        for (Table table : tables) {
            if (table.getName().equalsIgnoreCase(name))
                return table;
        }
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
