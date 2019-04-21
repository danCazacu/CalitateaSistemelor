package main.service;

import main.exception.InvalidCommand;
import main.util.Constants;

import java.util.Scanner;

public class CommandLineParser {

    public void startCommandLine() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                parse(scanner.nextLine());
            } catch (InvalidCommand invalidCommand) {
                System.out.println(invalidCommand.getMessage());
            }
        }
    }

    private void parse(String line) throws InvalidCommand {
        //this is just ideas, tried to do it but seems to much to do it at work
        String[] split = line.split("\\s+");
        if (split.length > 0) {
            String action = split[0];
            processAction(action, split);
        } else {
            throw new InvalidCommand();
        }

    }

    private void processAction(String action, String[] line) throws InvalidCommand {
        switch (action.toLowerCase()) {
            case Constants.INSERT:
                new InsertProcessor().processInsert(line);
                break;
            case Constants.DELETE:
                new DeleteProcessor().processDelete(line);
                break;
            case Constants.UPDATE:
                new UpdateProcessor().processUpdate(line);
                break;
            case Constants.SELECT:
                new SelectProcessor().processSelect(line);
                break;

            default:
                throw new InvalidCommand("Invalid command "+action);
        }
    }

    private void processUpdate(String[] line) {

    }

}
