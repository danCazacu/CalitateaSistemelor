package main.service;

import main.exception.InvalidCommand;

import java.util.Scanner;

public class CommandLineParser {

    public void startCommandLine(){
        Scanner scanner = new Scanner(System.in);

        while(true){
            scanner.nextLine();

        }
    }
    private void parser(String line) throws InvalidCommand {
        //this is just ideas, tried to do it but seems to much to do it at work
//        String[] split = line.split("\\s+");
//        String action = split[0];
//        switch (action) {
//            case Constants.INSERT:
//            case Constants.DELETE:
//            case Constants.UPDATE:
//            case Constants.SELECT:
//                default: throw new InvalidCommand();
//        }
    }

    private void parseInsert(String[] args){

    }


}
