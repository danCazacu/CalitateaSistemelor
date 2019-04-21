package main;

import main.graphicalInterface.MainWindow;
import main.model.*;
import main.persistance.DatabasePersistance;
import main.service.CommandLineParser;

public class Main {

    public static void main(String[] args) {
        DatabaseManagementSystem.getInstance();
        MainWindow mainFrame = new MainWindow();
        mainFrame.open();
        CommandLineParser commandLineParser = new CommandLineParser();
        commandLineParser.startCommandLine();

        new DatabasePersistance().persist();

    }
}
