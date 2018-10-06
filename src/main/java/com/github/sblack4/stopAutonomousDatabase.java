package com.github.sblack4;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.DatabaseWaiters;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.requests.GetAutonomousDatabaseRequest;
import com.oracle.bmc.database.requests.StopAutonomousDatabaseRequest;
import com.oracle.bmc.database.responses.GetAutonomousDatabaseResponse;
import picocli.CommandLine;
import picocli.CommandLine.*;


/**
 * should run with:
 *      stopAutonomousDatabase DBOCID
 */
@Command(name="stop",
        sortOptions = false,
        header = "@|fg(5;0;0),bg(0;0;0)  Stop an ATP instance with the JAVA OCI SDK |@" )
public class stopAutonomousDatabase implements Runnable {
    @Parameters(index = "0",
           description = "Autonomous ATPConnectionTest ID")
    public String adwId;

    @Option(names={"-c", "--config"}, description = "OCI Config file path, defaults to ${DEFAULT-VALUE}")
    public String configurationFilePath = "~/.oci/config";

    @Option(names={"-p", "--profile"}, description = "OCI profile, defaults to ${DEFAULT-VALUE}")
    public String profile = "DEFAULT";

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    @Override
    public void run() {
        // busyn-ness logix
        try {
            AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

            System.out.println("\n================================\n");
            System.out.println("Credentials 'n such; ");
            System.out.println(provider.toString());

            DatabaseClient dbClient = new DatabaseClient(provider);

            // Get
            AutonomousDatabase adw =
                    dbClient.getAutonomousDatabase(
                            GetAutonomousDatabaseRequest.builder()
                                    .autonomousDatabaseId(adwId)
                                    .build())
                            .getAutonomousDatabase();

            System.out.println("\n================================\n");
            System.out.println("GET request returned this database:\n" + adw);

            System.out.println("\n================================\n");
            System.out.println("Stopping Autonomous Database: \n" + adw);
            dbClient.stopAutonomousDatabase(
                StopAutonomousDatabaseRequest.builder()
                        .autonomousDatabaseId(adw.getId())
                        .build());

            DatabaseWaiters waiter = dbClient.getWaiters();
            GetAutonomousDatabaseResponse response = waiter.forAutonomousDatabase(
                                GetAutonomousDatabaseRequest.builder()
                                        .autonomousDatabaseId(adw.getId())
                                        .build(),
                                AutonomousDatabase.LifecycleState.Stopped
                ).execute();

            System.out.println("\n================================\n");
            System.out.println("Request for Stopped Instance returned: \n" + response.getAutonomousDatabase());

            System.out.println("\n======== DONE ========\n");
            System.out.println("\n================================\n");

        dbClient.close();
        } catch (Exception ex) {
            System.out.println("================================");
            System.out.println("=== ERROR ===");
            System.out.println("================================");

            System.out.println(ex.toString());
            ex.printStackTrace();
        }


    }

    public static void main(String[] args) {
        CommandLine.run(new stopAutonomousDatabase(), args);
    }
}
