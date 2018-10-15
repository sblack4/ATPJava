package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.requests.GetAutonomousDatabaseRequest;
import picocli.CommandLine;
import picocli.CommandLine.*;


/**
 * `getAutonomousDatabase` DBOCID
 */
@Command(name="get",
        sortOptions = false,
        header = "@|fg(5;0;0),bg(0;0;0)  Get an ATP instance with the JAVA OCI SDK |@" )
public class getAutonomousDatabases extends ATPCLI {

    @Parameters(index = "0",
            description = "Autonomous Database ID")
    public String adwId;

    @Option(names={"-p", "--profile"}, description = "OCI profile, defaults to ${DEFAULT-VALUE}")
    String profile = "DEFAULT";

    @Override
    public void run() {
        // busyn-ness logix
        try {
            AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

            System.out.println("\n================================\n");
            System.out.println("Credentials 'n such; ");
            System.out.println(provider.toString());

            DatabaseClient dbClient = new DatabaseClient(provider);
            dbClient.setRegion(this.getRegion(this.configurationFilePath));

            AutonomousDatabase item = dbClient.getAutonomousDatabase(
                    GetAutonomousDatabaseRequest.builder()
                            .autonomousDatabaseId(adwId)
                            .build())
                    .getAutonomousDatabase();

            System.out.println("\n================================");
            System.out.println("id = " + item.getId());
            System.out.println("dbName = " + item.getDbName());
            System.out.println("displayName = " + item.getDisplayName());
            System.out.println("serviceConsoleUrl = " + item.getServiceConsoleUrl());
            System.out.println("LifecycleState = " + item.getLifecycleState());
            System.out.println("\n" + item.toString());

            System.out.println("\n================================");
            System.out.println("=== DONE ===");
            System.out.println("================================");

        } catch (Exception ex) {
            System.out.println("================================");
            System.out.println("=== ERROR ===");
            System.out.println("================================");

            System.out.println(ex.toString());
            ex.printStackTrace();
        }


    }

    public static void main(String[] args) {
        CommandLine.run(new getAutonomousDatabases(), args);
    }
}
