package database;

import main.util.DataBuilder;
import org.junit.jupiter.api.Test;


public class DevTest {

    @Test
    public void createTableInsertDataTest(){
        DataBuilder.buildeDataOnce();
        DataBuilder.buildeDataSecondTime();
    }
}
