package database;

import main.exception.TypeMismatchException;
import main.model.*;
import main.util.DataBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DevTest {
    @BeforeAll
    public static void createTableInsertData() {
        DataBuilder.buildeDataOnce();
        DataBuilder.buildeDataSecondTime();
    }

    @Test()
    public void whereTypeMisMatchException() {
        assertThrows(TypeMismatchException.class, () -> {
            Table table = DatabaseManagementSystem.getInstance().getDatabases().get(0).getTables().get(0);
            Map<Column, List<Field>> result = table.where("name", FieldComparator.Sign.EQUAL, new Field(10));
            System.out.println(Table.toString(result));
        });
    }

    @Test()
    public void whereHeightLessThan180() {
            Table table = DatabaseManagementSystem.getInstance().getDatabases().get(0).getTables().get(0);
        Map<Column, List<Field>> result = null;
        try {
            result = table.where("height", FieldComparator.Sign.LESS_THAN, new Field(180));
            assertEquals("school | age | height | name | \n" +
                    "INFORMATICA | 10 | 169 | andreea | \n" +
                    "INFORMATICA | 12 | 169 | ilinca | \n",Table.toString(result));
            System.out.println(Table.toString(result));
        } catch (TypeMismatchException e) {
            e.printStackTrace();
        }
    }
}
