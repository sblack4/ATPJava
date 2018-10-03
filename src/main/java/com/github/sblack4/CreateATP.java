package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.DatabaseWaiters;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.model.CreateAutonomousDatabaseDetails;
import com.oracle.bmc.database.requests.CreateAutonomousDatabaseRequest;
import com.oracle.bmc.database.requests.GetAutonomousDatabaseRequest;
import com.oracle.bmc.database.responses.CreateAutonomousDatabaseResponse;
import com.oracle.bmc.database.responses.GetAutonomousDatabaseResponse;
import picocli.CommandLine;

import java.util.Random;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;


@Command(name="Create", header = "@|green Create an ATP instance with the JAVA OCI SDK |@" )
public class CreateATP implements Runnable {
    @Option(names={"-cId", "--compartmentId"}, required = true, description = "Compartment ID")
    public String compartmentId;

    @Option(names={"-dn", "--displayName"}, description = "Display Name for DB, defaults to ${DEFAULT-VALUE}")
    public String displayName = "javaSDKExample";

    @Option(names={"-p", "--password"}, description = "Password for DB, defaults to ${DEFAULT-VALUE}")
    public String password = "Welcome123123123#";

    @Option(names={"-cpu", "--cpuCount"}, description = "CPU Cores, defaults to ${DEFAULT-VALUE}")
    public Integer cpuCount = 1;

    @Option(names={"-tb", "--dbSize"}, description = "DB size in TBs, defaults to ${DEFAULT-VALUE}")
    public Integer dbSize = 1;

    @Option(names={"-c", "--config"}, description = "OCI Config file path, defaults to '~/.oci/config")
    public String configurationFilePath = "~/.oci/config";

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Displays this help message and quits.")
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
            dbClient.setRegion(Region.US_PHOENIX_1);

            // Create
            Random rand = new Random();
            CreateAutonomousDatabaseDetails createRequest = CreateAutonomousDatabaseDetails.builder()
                    .cpuCoreCount(cpuCount)
                    .dataStorageSizeInTBs(dbSize)
                    .displayName(displayName)
                    .adminPassword(password)
                    .dbName("javaSdkExam" + rand.nextInt(500))
                    .compartmentId(compartmentId)
                    .licenseModel(CreateAutonomousDatabaseDetails.LicenseModel.LicenseIncluded)
                    .build();

            System.out.println("\n================================\n");
            System.out.println("Creating Autonomous Transaction Processing Shared with request : \n" + createRequest);

            CreateAutonomousDatabaseResponse response =
                dbClient.createAutonomousDatabase(
                        CreateAutonomousDatabaseRequest.builder()
                                .createAutonomousDatabaseDetails(createRequest)
                                .build());

            AutonomousDatabase atpShared = response.getAutonomousDatabase();

            System.out.println("\n================================\n");
            System.out.println("ATP Shared instance is provisioning with given details: \n" + atpShared);

            DatabaseWaiters waiter = dbClient.getWaiters();
            GetAutonomousDatabaseResponse provisionedResponse =
                    waiter.forAutonomousDatabase(
                            GetAutonomousDatabaseRequest.builder()
                                    .autonomousDatabaseId(atpShared.getId())
                                    .build(),
                            AutonomousDatabase.LifecycleState.Available)
                            .execute();

            atpShared = provisionedResponse.getAutonomousDatabase();

            System.out.println("\n================================\n");
            System.out.println("Instance is provisioned:\n" + atpShared);

            // Get
            atpShared = dbClient.getAutonomousDatabase(
                    GetAutonomousDatabaseRequest.builder()
                            .autonomousDatabaseId(atpShared.getId())
                            .build()
            ).getAutonomousDatabase();

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
        CommandLine.run(new CreateATP(), args);
    }
}
