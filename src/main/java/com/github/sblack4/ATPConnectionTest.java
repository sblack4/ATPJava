package com.github.sblack4;

import java.sql.*;
import java.util.*;
import java.nio.file.*;
import java.security.Security;
import java.util.Properties;
import picocli.CommandLine;
import picocli.CommandLine.*;

/**
 * CLI Usage should be:  ATPConnectionTest DB_USER DB_PASSWD WALLET_DIR
 * or read from db.config
 */
@Command(name="connect",
        sortOptions = false,
        header = "@|fg(5;0;0),bg(0;0;0) Connect to an ATP instance with the JDBC in JAVA! \n" +
                " Pass in the arguments or use a db.config file (example below) |@" )
public class ATPConnectionTest implements Runnable {
    @Parameters(index="0", arity = "0..1",
            description = "ATPConnectionTest Username, defaults to ${DEFAULT-VALUE}")
    public String user = "admin";

    @Parameters(index="1", arity = "0..1",
            description = "ATPConnectionTest Password")
    public String password;

    @Parameters(index="2", arity = "0..1",
            description = "ATP Wallet path. Should be the path to your unzipped wallet, defaults to ${DEFAULT-VALUE}")
    public String wallet_dir = "/opt/oracle/database/wallet";

    @Option(names={"-sn", "--service-name"}, description = "ATPConnectionTest Service Name")
    public String serviceName = "";

    @Option(names={"-c", "--dbconfig"},
            description = "Path to db.config file, defaults to ${DEFAULT-VALUE}" +
                    "\nExample:" +
                    "\n\tpassword=MyPassword123!" +
                    "\n\tuser=admin" +
                    "\n\tservicename=oci.ok.123" +
                    "\n\twalletdir=/tmp/my_wallet")
    public String dbconfig = Paths.get(".").toAbsolutePath().toString() + "/db.config";

    private void parseConfigLine(String line) {
        String[] keyVal = line.split("=");
        String key = keyVal[0].trim().toLowerCase();
        String val = keyVal[1].trim();

        switch (key) {
            case "user":
                this.user = val;
            case "password":
                this.password = val;
            case "walletdir":
                this.wallet_dir = val;
            case "servicename":
                this.serviceName = val;

        }
    }

    public void parseDBConfig() {
        try {
            List<String> dbConfigLines = Files.readAllLines(Paths.get(this.dbconfig));
            for (String configLine : dbConfigLines) {
                this.parseConfigLine(configLine);
            }
        } catch (Exception ex) {
            CommandLine.usage(new ATPConnectionTest(), System.out);
            System.out.flush();
            System.out.println("\n :O !Error parsing db config file! :O \n");
            System.exit(1);
        }
    }

    public void run() {
        if(this.password == null || this.user == null || this.wallet_dir == null)
            this.parseDBConfig();

        String wallet_location = String.format("(SOURCE=(METHOD=file)(METHOD_DATA=(DIRECTORY=%s)))", wallet_dir);
        String connection = String.format("jdbc:oracle:thin:@%s_low?TNS_ADMIN=%s",this.serviceName, wallet_dir);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Can't find the Oracle JDBC Driver");
            e.printStackTrace();
            return;
        }

        try {
            Security.addProvider(new oracle.security.pki.OraclePKIProvider());

            Properties props = new Properties();
            props.setProperty("oracle.net.wallet_location",wallet_location);
            props.setProperty("oracle.net.tns_admin", wallet_dir);
            props.setProperty("user", this.user);
            props.setProperty("password", this.password);

            Connection conn = DriverManager.getConnection(connection, props);

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT 'hello world' FROM DUAL");
            while (result.next())
                System.out.println(result.getString(1));
            result.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        CommandLine.run(new ATPConnectionTest(), args);
    }
}
