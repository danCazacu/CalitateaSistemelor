package persistence;

import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

public class Persist {
    @Test
    public void persist() {
        DataBuilder.buildeDataOnce();
        DataBuilder.buildeDataSecondTime();
        DataBuilder.buildDataThree();

        DatabasePersistance databasePersistance = new DatabasePersistance();
        databasePersistance.persist();
    }
}
