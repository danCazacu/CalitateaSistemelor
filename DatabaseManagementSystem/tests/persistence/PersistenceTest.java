package persistence;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {

    @Test
    public void loadPersistTest(){

        //auto loads data from file
        DatabaseManagementSystem.getInstance().getDatabases().clear();

        DatabasePersistance persistance = new DatabasePersistance();
        try {
            persistance.load();
        } catch (IOException | InvalidValue | AlreadyExists e) {
            fail(e);
        }
        List<Database> databases = new ArrayList<>(DatabaseManagementSystem.getInstance().getDatabases());
        persistance.persist();

        DatabaseManagementSystem.getInstance().getDatabases().clear();
        try {
            persistance.load();
        } catch (IOException | InvalidValue | AlreadyExists e) {
            fail(e);
        }

        for (Database database : databases) {
            try {
                DatabaseManagementSystem.getInstance().getDatabase(database.getName());
            } catch (DoesNotExist doesNotExist) {
                fail(doesNotExist);
            }
        }

    }

}
