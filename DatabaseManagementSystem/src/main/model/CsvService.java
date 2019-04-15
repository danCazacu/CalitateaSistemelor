package main.model;

import com.opencsv.CSVWriter;
import main.exception.FieldValueNotSet;
import main.persistance.DatabasePersistance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CsvService {
    public static void writeDataLineByLine(String filePath)
    {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        DatabaseManagementSystem databaseManagementSystem = DatabaseManagementSystem.getInstance();
        Table table = databaseManagementSystem.getDatabase("store").getTable("employee");
//        String pathWhereToSaveTable = DatabasePersistance.cwd()+File.separator+table.getName();
//        Map<Column, List<Field>> data = table.getData();
////        table.getNumberOfRows();
////        table.get
////        table.getRow(i)


        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);
            for(String columnName : table.getColumnNames()){
                outputfile.write("\""+columnName+"\""+",");

            }
            for(int i=0; i<table.getNumberOfRows(); i++){
                Map<Column,Field> row = table.getRow(i);
                int j=0;
                for (Column column: row.keySet()) {
                    Field field = row.get(column);
                    j++;
                    if(field.isIntValueSet()){
                        if(j==row.size())
                            outputfile.write("\""+field.getIntValue().toString()+"\"");
                        else
                            outputfile.write("\""+field.getIntValue().toString()+"\""+",");
                    }
                    if(field.isStringValueSet()){
                        if(j==row.size())
                            outputfile.write("\""+field.getStringValue()+"\"");
                        else
                            outputfile.write("\""+field.getStringValue()+"\""+",");
                    }

                }
            }
//            // create CSVWriter object filewriter object as parameter
//            CSVWriter writer = new CSVWriter(outputfile);
//
//            // adding header to csv
//            String[] header = { "Name", "Class", "Marks" };
//            writer.writeNext(header);
//
//            // add data to csv
//            String[] data1 = { "Ilinca", "10", "620" };
//            writer.writeNext(data1);
//            String[] data2 = { "Magdalena", "10", "630" };
//            writer.writeNext(data2);

            // closing writer connection
//            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FieldValueNotSet fieldValueNotSet) {
            fieldValueNotSet.printStackTrace();
        }
    }
}
