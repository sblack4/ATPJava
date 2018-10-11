package com.github.sblack4;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.Arrays;
import java.util.stream.Collectors;


@Command(name="CLI",
        sortOptions = false,
        header = "@|fg(0;0;0),bg(5;0;0),bold " +
                "  _  _     _ _        __                    _               \n" +
                " | || |___| | |___   / _|_ _ ___ _ __    _ | |__ ___ ____ _ \n" +
                " | __ / -_) | / _ \\ |  _| '_/ _ \\ '  \\  | || / _` \\ V / _` |\n" +
                " |_||_\\___|_|_\\___/ |_| |_| \\___/_|_|_|  \\__/\\__,_|\\_/\\__,_||@\n" +
                "@|fg(5;0;0),bg(0;0;0) \n Create, Scale, List, and Delete your ATP - examples in... JAVA!|@",
        subcommands = { createAutonomousDatabase.class,
                listAutonomousDatabases.class,
                updateAutonomousDatabase.class,
                deleteAutonomousDatabase.class,
                ATPConnectionTest.class,
                startAutonomousDatabase.class,
                getAutonomousDatabases.class,
                stopAutonomousDatabase.class}
                )
public class CLI implements Runnable {
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Incorrect Usage, please give command \n");
            CommandLine.usage(new CLI(), System.err);
            System.exit(1);
        }

        // make all the args lowercase
//        String [] lowerArgs = new String[args.length];
//        lowerArgs = Arrays.stream(args)
//                .filter(arg -> !arg.isEmpty())
//                .map(arg -> arg.toLowerCase())
//                .collect(Collectors.toList()).toArray(lowerArgs);

//        for (String arg : lowerArgs) {
//            System.out.println(arg);
//        }

        CommandLine.run(new CLI(), args);
    }
}
