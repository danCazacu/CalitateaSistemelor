package main.model;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private String name;
    private List<Table> tables;

    public Database(String name) {
        this.name = name;
        this.tables = new ArrayList<>();
    }

    public Database(String name, List<Table> tables) {
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
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param tableName
     * @return true if this database contains such table, false if not
     */
    public boolean exists(String tableName){
        Table table = getTable(tableName);
        return table!=null;
    }

    public Table createTable(String name, List<Column> columnNames) {
        Table exists = getTable(name);
        if (exists != null)
            return exists;
        return createTable(new Table(name,columnNames));
    }

    public Table createTable(Table table) {
        if (tables.contains(table))
            return table;
        tables.add(table);
        return table;
    }

    /**
     * delete table by instance
     * @param table
     */
    public void deleteTable(Table table) {
        tables.remove(table);
    }

    /**
     * delete table by its name
     * @param tableName
     */
    public void deleteTable(String tableName) {
        Table table = this.getTable(tableName);
        if (table != null)
            deleteTable(table);
    }

    /**
     *
     * @param name
     * @return Table instance that has the name in paramter, returns NULL if no such table
     */
    public Table getTable(String name) {
        for (Table table : tables) {
            if (table.getName().equalsIgnoreCase(name))
                return table;
        }
        return null;
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
}
