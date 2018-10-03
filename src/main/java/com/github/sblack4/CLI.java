package com.github.sblack4;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(name="CLI", header = "@|red Create, Scale, List, and Delete your ATP - examples in... JAVA!|@",
    subcommands = { CreateATP.class, ListATP.class, ScaleATP.class, DeleteATP.class, Database.class})
public class CLI implements Runnable {
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public void run() {

    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}
