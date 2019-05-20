package main;

import main.exception.InvalidCommand;
import main.graphicalInterface.MainWindow;
import main.persistance.DatabasePersistance;
import main.service.CommandLineParser;


/**
 * Tests dependencies needed
 * org.mockito:mockito-core:2.27.0
 * org.junit.jupiter:junit-jupiter-params:5.4.2
 */
public class Main {

    public static void main(String[] args) throws InvalidCommand {

        MainWindow mainFrame = new MainWindow();
        mainFrame.open();
        CommandLineParser commandLineParser = new CommandLineParser();
        while(true)
            commandLineParser.startCommandLine();

//        new DatabasePersistance().persist();

    }
}
