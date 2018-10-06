

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.lang.RuntimeException;
import java.net.URISyntaxException;
import java.lang.ProcessBuilder.Redirect;

/**
 * createAutonomousDatabase DBNAME DISPLAYNAME PASSWORD CPUCOUNT STORAGEINTBS
 */
public class createAutonomousDatabase {
    public String DB_USER;
    public String WALLET_DIR;
    private String dbConfig = "./db.config";
    private String dbname;
    private String displayName;
    private String password;
    private String cpucount;
    private String storageInTbs;

    public static void printUsage() {
        String usage = "\nUSAGE:\n\tjava createAutonomousDatabase DBNAME DISPLAYNAME PASSWORD CPUCOUNT STORAGEINTBS" +
                "Or pass no args to read db.config file from this directory \n" +
                "\nEXAMPLE: ./db.config: " +
                "\n\tDBNAME=mydatabase123" + "" +
                "\n\tDISPLAYNAME=mydatabase" + "" +
                "\n\tPASSWORD=SuperStringPassword123!" + "" +
                "\n\tCPUCOUNT=1" + "" +
                "\n\tSTORAGEINTBS=1";

        System.out.println(usage);
    }

    public String getPath(String arg) {
        Path thisFilePath = Paths.get(".").toAbsolutePath();

        if (arg != null) {
            System.out.println("Got path " + arg);
            thisFilePath = Paths.get(arg).toAbsolutePath();
        }
        if (!Files.exists(thisFilePath)) {
            System.out.println("Error. Path does not exist");
            throw new RuntimeException("Error. Path does not exist");
        }

        return thisFilePath.toString();
    }

    private void parseConfigLine(String line) {
        String[] keyVal = line.split("=");
        String key = keyVal[0].trim();
        String val = keyVal[1].trim();

        switch (key) {
            case "DB_USER":
                this.DB_USER = val;
            case "DB_PASSWD":
                this.DB_PASSWD = val;
            case "WALLET_DIR":
                this.WALLET_DIR = val;
        }
    }

    public void validateDBConfig() {
        Path dbConfig = Paths.get(this.dbConfig).toAbsolutePath();
        Boolean dbConfigExists = Files.exists(dbConfig);
        if (!dbConfigExists) {
            throw new RuntimeException("Error. Could not find DB Config file");
        }
        System.out.println("using db.config: " + dbConfig.toString());
    }

    public void parseDBConfig() {
        try {
            List<String> dbConfigLines = Files.readAllLines(Paths.get(this.dbConfig));
            for (String configLine : dbConfigLines) {
                this.parseConfigLine(configLine);
            }
        } catch (Exception ex) {
            System.out.println("Error parsing db config file: " + ex.toString());
        }
        System.out.println("\n Parsed DB Config, values:");
        System.out.println(String.format("DB_USER=%s DB_PASSWD=%s WALLET_DIR=%s \n", this.DB_USER, this.DB_PASSWD, this.WALLET_DIR));
    }

    public String getClassPath() {
        // things might be in other places
        // because one version of this app is to be deployed to this docker image
        // another version to OL7 on Oracle Cloud
        // and yet another on my machine <3
        String thisDir = "./";
        try {
            thisDir = new File(ATPConnectionTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (Exception ex) {
            System.out.println("Error finding directory of this file " + ex.toString());
        }
        String jarPath = thisDir + "/../target/atp-rest-scripts.jar";
        String bigLibPath = thisDir + "/../lib/*";
        String jdbcLib = "/opt/oracle/tools/java/ojdbc8-full/*";
        String sdkBase = "/opt/oracle/tools/java/sdk";
        String sdkLib = sdkBase + "/lib/*:" + sdkBase + "/third-party/lib/*";

        return bigLibPath + ":" + jdbcLib + ":" + sdkLib + ":" + jarPath;
    }

    public String getCommand() {
        String classPath = this.getClassPath();

        String command = "java -cp " + classPath +
                " com.github.sblack4.CLI connect -u " + this.DB_USER +
                " -p " + this.DB_PASSWD + " -w " + this.WALLET_DIR;

        return command;
    }

    public void executeCommand() throws IOException {
        String commandString = this.getCommand();

        List<String> command = new ArrayList<String>();
        for(String cmd : commandString.split(" ")) {
            command.add(cmd);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectInput(Redirect.INHERIT);
//        builder.redirectError(Redirect.INHERIT);

//        final Process process = builder.inheritIO().start();
        final Process process = builder.start();

        try {
            process.waitFor();
        } catch (InterruptedException iex) {
            System.out.println("Error. Process interrupted. \n");
            iex.printStackTrace();
        }
    }

    public void run(String[] args) {
        this.printUsage();

        // they passed it in as args
        if (args.length > 2) {
            this.DB_USER = args[0];
            this.DB_PASSWD = args[1];
            this.WALLET_DIR = args[2];
        } else {
            this.validateDBConfig();
            this.parseDBConfig();
        }
        try {
            this.executeCommand();
        } catch (Exception ex) {
            System.out.println("Error executing command: " + ex.toString());
        }
    }

    public static void main(String[] args) {
        createAutonomousDatabase atpConnectionTest = new createAutonomousDatabase();
        atpConnectionTest.run(args);
    }
}