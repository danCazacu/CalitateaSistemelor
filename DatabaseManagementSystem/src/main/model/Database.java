package main.model;

import main.query.Query;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private String name;
    private List<Table> tables;

    public Database(String name){
        this.name = name;
        this.tables = new ArrayList<>();
    }
    public Database(String name,List<Table> tables) {
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

    public void setName(String name) {
        this.name = name;
    }

    public Table createTable(String name,List<Column> columnNames){
        Table table = new Table(name,columnNames);
        tables.add(table);
        return table;
    }
    public Table createTable(Table table){
        tables.add(table);
        return table;
    }
    public void deleteTable(Table table){
        tables.remove(table);
    }

    public void deleteTable(String tableName){
        Table table = this.getTable(tableName);
        if(table!=null)
            deleteTable(table);
    }

    public Table getTable(String name){
        for (Table table: tables) {
            if(table.getName().equals(name))
                return table;
        }
        return null;
    }

    public Query select(List<Column> selectColumns){
        Query query = new Query();
        query.setDatabase(this);
        query.setSelectedColumns(selectColumns);
        return query;
    }

}
