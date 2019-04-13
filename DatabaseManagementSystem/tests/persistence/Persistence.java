package persistence;

import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;
import org.junit.jupiter.api.Test;

public class Persistence {
    @Test
    public void cwd(){
        DatabasePersistance databasePersistance = new DatabasePersistance();
        databasePersistance.persist(DatabaseManagementSystem.getInstance());

    }
}
