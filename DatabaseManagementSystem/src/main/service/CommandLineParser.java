package main.service;

import main.exception.InvalidCommand;
import main.persistance.DatabasePersistance;
import main.util.Constants;

import java.util.Scanner;

public class CommandLineParser {

    public void startCommandLine() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                parse(scanner.nextLine());
                new DatabasePersistance().persist();
            } catch (InvalidCommand invalidCommand) {
                System.out.println(invalidCommand.getMessage());
            }
        }
    }

    public void parse(String line) throws InvalidCommand {

        assert line != null: "Precondition failed: input parameter can not be null";

        String[] split = line.split("\\s+");
        if (split.length > 0) {
            String action = split[0];
            processAction(action, split);
        } else {
            throw new InvalidCommand();
        }

    }

    private void processAction(String action, String[] line) throws InvalidCommand {

        assert action != null: "Precondition failed: input action parameter can not be null";
        assert line != null: "Precondition failed: input list lines parameter can not be null";


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
                throw new InvalidCommand("Invalid command " + action);
        }
    }

    private void processUpdate(String[] line) {

    }

}
