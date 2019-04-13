package main.persistance;

import main.model.DatabaseManagementSystem;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabasePersistance {

    public void persist(DatabaseManagementSystem databaseManagementSystem) {
        String s = cwd();
        System.out.println("Current relative path is: " + s);
    }


    public DatabaseManagementSystem load(String fileNamePath) {
        //load system from this path
        return null;
    }


    private String cwd() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
