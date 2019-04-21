package main.model;

import jdk.nashorn.internal.runtime.Debug;
import main.exception.FieldValueNotSet;
import main.exception.InexistentColumn;
import main.exception.TypeMismatchException;
import main.persistance.DatabasePersistance;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvService {
    public static void writeDataLineByLine(String filePath, String databaseName, String tableName) {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        DatabaseManagementSystem databaseManagementSystem = DatabaseManagementSystem.getInstance();
        Table table = databaseManagementSystem.getDatabase(databaseName).getTable(tableName);


//        String pathWhereToSaveTable = DatabasePersistance.cwd()+File.separator+table.getName();
//        Map<Column, List<Field>> data = table.getData();
////        table.getNumberOfRows();
////        table.get
////        table.getRow(i)


        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            outputfile.write("Database,Table\n");
            outputfile.write(databaseName + "," + tableName + '\n');
            outputfile.write("\n");
            for (String columnName : table.getColumnNames()) {
                outputfile.write(columnName + ",");

            }
            outputfile.write("\n");
            for (int i = 0; i < table.getNumberOfRows(); i++) {
                Map<Column, Field> row = table.getRow(i);
                int j = 0;
                for (Column column : row.keySet()) {
                    Field field = row.get(column);
                    j++;
                    if (field.isIntValueSet()) {
                        if (j == row.size())
                            outputfile.write(field.getIntValue().toString());
                        else
                            outputfile.write(field.getIntValue().toString() + ",");
                    }
                    if (field.isStringValueSet()) {
                        if (j == row.size())
                            outputfile.write(field.getStringValue());
                        else
                            outputfile.write(field.getStringValue() + ",");
                    }

                }
                outputfile.write("\n");
            }
            outputfile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FieldValueNotSet fieldValueNotSet) {
            fieldValueNotSet.printStackTrace();
        }
    }


    public static void importDataLineByLine(String filePath) {
        File file = new File(filePath);
        DatabaseManagementSystem databaseManagementSystem = DatabaseManagementSystem.getInstance();
        try {
            FileReader inputFile = new FileReader(file);
            BufferedReader inStream = new BufferedReader(inputFile);
            String inString;
            inString = inStream.readLine();
            inString = inStream.readLine();
            String databaseExcel = inString.split(",")[0];
            String tableExcel = inString.split(",")[1];

            Table table = databaseManagementSystem.getDatabase(databaseExcel).getTable(tableExcel);
            inString = inStream.readLine();
            System.out.println(inString);


            inString = inStream.readLine();
            String[] headers = inString.split(",");

            while ((inString = inStream.readLine()) != null) {
                String[] values = inString.split(",");

                Map<String, Field> row = new HashMap<>();
                for (int index = 0; index < values.length; index++) {
                    Field field = new Field();
                    try {
                        field.setValue(Integer.parseInt(values[index]));

                    } catch (NumberFormatException e) {
                        field.setValue(values[index]);
                    }
                    row.put(headers[index], field);
                }
                table.insert(row);
            }

            inputFile.close();
            inStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InexistentColumn inexistentColumn) {
            inexistentColumn.printStackTrace();
        } catch (TypeMismatchException e) {
            e.printStackTrace();
        }
    }
}
