package main.persistance;

import main.exception.InexistentColumn;
import main.exception.TypeMismatchException;
import main.model.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabasePersistance {

    private static final String FILE_NAME = "persistance.txt";
    private final String PATH_FILE = cwd() + File.separator + FILE_NAME;

    public void persist() {
        try {
            OutputStream outputStream = new FileOutputStream(PATH_FILE);
            DatabaseManagementSystem.getInstance().persist(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void load() throws IOException {
        FileReader fileReader = new FileReader(PATH_FILE);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            if (line.equalsIgnoreCase(PersistenceContants.START_DATABASE)) {
                Database database = DatabaseManagementSystem.getInstance().createDatabase(bufferedReader.readLine());
                loadDatabase(bufferedReader, database);
            }
            line = bufferedReader.readLine();
        }
    }

    private void loadDatabase(BufferedReader bufferedReader, Database database) throws IOException {
        String line = bufferedReader.readLine();
        while (line.equalsIgnoreCase(PersistenceContants.START_TABLE)) {
            String tableName = bufferedReader.readLine();
            Table table = loadTable(bufferedReader, tableName);
            database.createTable(table);
            line = bufferedReader.readLine();
        }
    }

    private Table loadTable(BufferedReader bufferedReader, String tableName) throws IOException {
        String line = bufferedReader.readLine();
        Map<Column, List<Field>> data = new HashMap<>();
        int rowCount = 0;
        while (line.equalsIgnoreCase(PersistenceContants.START_COLUMN)) {
            String columnName = bufferedReader.readLine();
            Column.Type type = Column.Type.getEnum(bufferedReader.readLine());
            ArrayList<Field> columnData = loadData(bufferedReader, type, new ArrayList<>());
            if (rowCount == 0) rowCount = columnData.size();
            data.put(new Column(columnName, type), columnData);
            line = bufferedReader.readLine();
        }

        Table table = new Table(tableName, new ArrayList<>(data.keySet()));
        for (int i = 0; i < rowCount; i++) {
            Map<String, Field> row = new HashMap<>();
            for (Column column : data.keySet()) {
                row.put(column.getName(),data.get(column).get(i));
            }
            try {
                table.insert(row);
            } catch (TypeMismatchException | InexistentColumn e) {
                e.printStackTrace();
            }
        }
        return table;
    }

    private ArrayList<Field> loadData(BufferedReader bufferedReader, Column.Type type, ArrayList<Field> arrayList) throws IOException {
        String line = bufferedReader.readLine();
        while (!line.equalsIgnoreCase(PersistenceContants.END_COLUMN)) {
            if (type.equals(Column.Type.INT)) {
                Integer value = Integer.parseInt(line);
                arrayList.add(new Field(value));
            }
            if (type.equals(Column.Type.STRING)) {
                arrayList.add(new Field(line));
            }
            line = bufferedReader.readLine();
        }
        return arrayList;
    }

    private String cwd() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
