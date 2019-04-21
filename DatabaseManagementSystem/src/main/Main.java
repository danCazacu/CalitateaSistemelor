package main;

import main.graphicalInterface.MainWindow;
import main.model.*;
import main.persistance.DatabasePersistance;
import main.util.DataBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        DatabaseManagementSystem.getInstance();
        CsvService.writeDataLineByLine("E:\\Facultate\\Master\\Anul I, semestrul II\\Radulescu\\test.csv");
        MainWindow mainFrame = new MainWindow();
        mainFrame.open();

        new DatabasePersistance().persist();

    }
}
