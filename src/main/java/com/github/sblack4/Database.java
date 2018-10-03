package com.github.sblack4;

import java.sql.*;
import java.security.Security;
import java.util.Properties;
import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(name="Connect", header = "@|green Connect an ATP instance with the JDBC in JAVA |@" )
public class Database implements Runnable{
    @Option(names={"-u", "--user"}, required = true, description = "Database Username, defaults to ${DEFAULT-VALUE}")
    public String user = "admin";

    @Option(names={"-p", "--password"}, required = true, description = "Database Password, defaults to ${DEFAULT-VALUE}")
    public String password;

    @Option(names={"-sn", "--name"}, required = true, description = "Database Service Name")
    public String serviceName;

    public void run() {
        String wallet_dir = "/opt/oracle/database/" + this.serviceName + "/wallet";
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

            Connection conn = DriverManager.getConnection(connection,props);

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
        CommandLine.run(new Database(), args);
    }
}
