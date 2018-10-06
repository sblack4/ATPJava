package com.github.sblack4;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.DatabaseWaiters;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.model.UpdateAutonomousDatabaseDetails;
import com.oracle.bmc.database.requests.*;
import com.oracle.bmc.database.responses.GetAutonomousDatabaseResponse;
import com.oracle.bmc.database.responses.UpdateAutonomousDatabaseResponse;
import picocli.CommandLine;
import picocli.CommandLine.*;


/**
 * should respond to:
 *      updateAutonomousDatabase DBOCID CPUCount StorageInTBs
 */
@Command(name="update", header = "@|fg(5;0;0),bg(0;0;0) Scale an ATP instance with the JAVA OCI SDK |@" )
public class updateAutonomousDatabase implements Runnable {
    @Parameters(index = "0",
            description = "Autonomous ATPConnectionTest ID")
    public String adwId;

    @Parameters(index = "1", arity = "0..1",
           description = "CPU Cores, defaults to ${DEFAULT-VALUE}")
    public Integer cpuCount = 2;

    @Parameters(index = "2", arity = "0..1",
            description = "DB size in TBs, defaults to ${DEFAULT-VALUE}")
    public Integer dbSize = 2;

    @Option(names={"-c", "--config"}, description = "OCI Config file path, defaults to ${DEFAULT-VALUE}")
    public String configurationFilePath = "~/.oci/config";

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits, defaults to ${DEFAULT-VALUE}")
    private boolean helpRequested = false;

    public void run() {
        String profile = "DEFAULT";
        AuthenticationDetailsProvider provider;

        try {
            provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

            System.out.println("\n================================\n");
            System.out.println("Credentials 'n such; ");
            System.out.println(provider.toString());

            DatabaseClient dbClient = new DatabaseClient(provider);

            DatabaseWaiters waiter = dbClient.getWaiters();

            GetAutonomousDatabaseResponse provisionedResponse =
                    waiter.forAutonomousDatabase(
                            GetAutonomousDatabaseRequest.builder()
                                    .autonomousDatabaseId(adwId)
                                    .build(),
                            AutonomousDatabase.LifecycleState.Available
                    ).execute();

            AutonomousDatabase atpShared = provisionedResponse.getAutonomousDatabase();

            System.out.println("\n================================\n");
            System.out.println("Instance found with details:\n" + atpShared);

            UpdateAutonomousDatabaseDetails updateDb = UpdateAutonomousDatabaseDetails.builder()
                    .cpuCoreCount(cpuCount)
                    .dataStorageSizeInTBs(dbSize)
                    .build();

            System.out.println("\n================================\n");
            System.out.println("Updating database with details:\n" + atpShared);

            UpdateAutonomousDatabaseResponse response =
                    dbClient.updateAutonomousDatabase(
                            UpdateAutonomousDatabaseRequest.builder()
                                    .updateAutonomousDatabaseDetails(updateDb)
                                    .autonomousDatabaseId(adwId)
                                    .build());
            atpShared = response.getAutonomousDatabase();

            System.out.println("\n================================\n");
            System.out.println("GET request returned :\n" + atpShared);

            dbClient.close();
            System.out.println("================================");
            System.out.println("=== DONE ===");

        } catch (Exception ex) {
            System.out.println("================================");
            System.out.println("=== ERROR ===");
            System.out.println("================================");
            System.out.println(ex.toString());
        }
    }

    public static void main(String[] args) {
        CommandLine.run(new updateAutonomousDatabase(), args);
    }
}
