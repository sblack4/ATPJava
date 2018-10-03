package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.DatabaseWaiters;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.requests.*;
import com.oracle.bmc.database.responses.GetAutonomousDatabaseResponse;
import picocli.CommandLine;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;


@Command(name="Delete", header = "@|green Delete an ATP instance with the JAVA OCI SDK |@" )
public class DeleteATP implements Runnable {
    @Option(names={"-cId", "--compartmentId"}, required = true, description = "Compartment ID")
    public String compartmentId;

    @Option(names={"-Id", "--adwId"}, required = true, description = "Autonomous Database ID")
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
            dbClient.setRegion(Region.US_PHOENIX_1);

            // Get
            AutonomousDatabase adw =
                    dbClient.getAutonomousDatabase(
                            GetAutonomousDatabaseRequest.builder()
                                    .autonomousDatabaseId(adwId)
                                    .build())
                            .getAutonomousDatabase();

            System.out.println("\n================================\n");
            System.out.println("GET request returned :\n" + adw);

            // Delete
            System.out.println("\n================================\n");
            System.out.println("Deleting Autonomous Datawarehouse Shared : \n" + adw);
            dbClient.deleteAutonomousDatabase(
                DeleteAutonomousDatabaseRequest.builder()
                        .autonomousDatabaseId(adw.getId())
                        .build());

            DatabaseWaiters waiter = dbClient.getWaiters();
            GetAutonomousDatabaseResponse response = waiter.forAutonomousDatabase(
                                GetAutonomousDatabaseRequest.builder()
                                        .autonomousDatabaseId(adw.getId())
                                        .build(),
                                AutonomousDatabase.LifecycleState.Terminated
                ).execute();

            System.out.println("\n================================\n");
            System.out.println("Request for Terminated Instances: \n" + response.getAutonomousDatabase());

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
        CommandLine.run(new DeleteATP(), args);
    }
}
