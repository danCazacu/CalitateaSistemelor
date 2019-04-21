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
        MainWindow mainFrame = new MainWindow();
        mainFrame.open();

        new DatabasePersistance().persist();

    }
}
