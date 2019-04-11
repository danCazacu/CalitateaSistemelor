package main.query;

import main.exception.WrongTypeInColumnException;
import main.model.Column;
import main.model.Database;
import main.model.Field;
import main.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {
    private Database database;
    private Table table;
    private List<Column> selectedColumns;

    public Query from(Table table) {
        this.setTable(table);
        return this;
    }

    public Query from(String tableName) {
        this.setTable(database.getTable(tableName));
        return this;
    }

    public Map<Column, List<Field>> whereEquals(String columnName, Field value) throws WrongTypeInColumnException {
        //prepare result
        Map<Column, List<Field>> result = new HashMap<>();
        for (Column col : table.getData().keySet()) {
            result.put(col, new ArrayList<>());
        }

        Map<Column, List<Field>> tableData = table.getData();
        for (Column col : tableData.keySet()) {
            if (col.getName().equals(columnName)) {
                if (!col.getType().equals(value.getType())) {
                    throw new WrongTypeInColumnException(col.getType(), value.getType(), col.getName());
                }

                List<Field> columnData = tableData.get(col);
                for (Field field : columnData) {
                    if (field.equals(value)) {
                        Map<Column,Field> row = table.getRow(columnData.indexOf(field));
                        for (Column rowColumn:row.keySet()) {
                            result.get(rowColumn).add(row.get(rowColumn));
                        }
                    }
                }

            }
        }
        //trim result only for wanted columns
        for (Column column: result.keySet()) {
            if(!selectedColumns.contains(column))
                result.remove(column);
        }
        return result;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<Column> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(List<Column> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

}
