package persistence;

import main.exception.AlreadyExists;
import main.exception.InvalidValue;
import main.model.DatabaseManagementSystem;
import main.persistance.DatabasePersistance;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class Load {
    @Test
    public void cwd(){

        DatabasePersistance databasePersistance = new DatabasePersistance();
        try {
            databasePersistance.load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        } catch (AlreadyExists alreadyExists) {
            alreadyExists.printStackTrace();
        }

    }
}
