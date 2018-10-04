package com.github.sblack4;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(name="CLI",
        sortOptions = false,
        header = "@|red,bg(white) " +
                "  _  _     _ _        __                    _               \n" +
                " | || |___| | |___   / _|_ _ ___ _ __    _ | |__ ___ ____ _ \n" +
                " | __ / -_) | / _ \\ |  _| '_/ _ \\ '  \\  | || / _` \\ V / _` |\n" +
                " |_||_\\___|_|_\\___/ |_| |_| \\___/_|_|_|  \\__/\\__,_|\\_/\\__,_||@\n" +
                "@|red Create, Scale, List, and Delete your ATP - examples in... JAVA!|@",
        subcommands = { CreateATP.class, ListATP.class, ScaleATP.class, DeleteATP.class, Database.class})
public class CLI implements Runnable {
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Incorrect Usage, please give command");
            CommandLine.usage(new CLI(), System.err);
        }
        CommandLine.run(new CLI(), args);
    }
}
