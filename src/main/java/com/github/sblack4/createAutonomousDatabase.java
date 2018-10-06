package com.github.sblack4;

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
import picocli.CommandLine.*;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;



/**
 * should be callable by:
 *      createAutonomousDatabase DBNAME DISPLAYNAME PASSWORD CPUCOUNT STORAGEINTBS
 *
 */
@Command(name="create",
        header = "@|fg(5;0;0),bg(0;0;0) Create an ATP instance with the JAVA OCI SDK |@" )

public class createAutonomousDatabase implements Runnable {

    @Parameters(index ="0", arity = "0..1",
        description = "Database Name, by default randomly generates one")
    public String dbName;

    @Parameters(index ="1", arity = "0..1",
            description = "Display Name for DB, by default randomly generates one")
    public String displayName;

    @Parameters(index = "2", arity = "0..1",
            description = "Password for DB, by default randomly generates one")
    public String password;

   @Parameters(index = "3", arity = "0..1",
           description = "CPU Cores, defaults to ${DEFAULT-VALUE}")
    public Integer cpuCount = 1;

    @Parameters(index = "4", arity = "0..1",
            description = "DB size in TBs, defaults to ${DEFAULT-VALUE}")
    public Integer dbSize = 1;

    @Option(names={"-cid", "--compartment-id"},
            description = "Compartment ID, by default it is retrieved from OCI Config")
    public String compartmentId;

    @Option(names={"-c", "--config"},
            description = "OCI Config file path, defaults to '~/.oci/config")
    public String configurationFilePath = "~/.oci/config";

    @Option(names = { "-h", "--help" }, usageHelp = true,
            description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    private final String[] funNamesList = new String[]{
            "Floof",
            "Einstein",
            "Dancing",
            "Tesla",
            "Dinosaur",
            "Autonomous",
            "Database",
            "Ellison",
            "Robot",
            "_As_A_Service_",
            "Integrated",
            "Party",
            "Puppy",
            "Kitten",
            "InMemory", "ActiveDataGuard", "AdvancedAnalytics", "Multitenant", "OLAP",
            "Partitioning", "RealApplicationClusters", "Sharding", "OracleApplicationExpress",
            "APEX", "SQL", "PL/SQL", "AutomaticStorageManagement", "OracleSecureBackup",
            "Java", "Linux", "Happiness", "Jumping", "Future"
    };

    private List<String> funNames = Arrays.asList(this.funNamesList);


    /**
     * Yay tail recursion!
     * @param numNames the number of names you'd like
     * @return new name(s) from the numNamesList
     */
    public String getRandomFunName(Integer numNames) {
        if (numNames < 1) {
            return "";
        }
        Integer numNameListLen = 30;
        Random rand = new Random();
        Integer randNum = rand.nextInt(numNameListLen);
        String newName = funNames.get(randNum);
        return newName + getRandomFunName(numNames - 1);
    }

    public void generateDetails() {
        if (this.dbName == null) {
            this.dbName = getRandomFunName(3);
        }
        if (this.displayName == null) {
            this.displayName = getRandomFunName(4);
        }
        if (this.password == null) {

            this.password = getRandomFunName(3) +  "2018!";
        }
    }
    public void run() {
        this.generateDetails();
        String profile = "DEFAULT";
        AuthenticationDetailsProvider provider;

        try {
            provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

            System.out.println("\n================================\n");
            System.out.println("Credentials 'n such; ");
            System.out.println(provider.toString());

            DatabaseClient dbClient = new DatabaseClient(provider);

            // Create
            CreateAutonomousDatabaseDetails createRequest = CreateAutonomousDatabaseDetails.builder()
                    .cpuCoreCount(cpuCount)
                    .dataStorageSizeInTBs(dbSize)
                    .displayName(displayName)
                    .adminPassword(password)
                    .dbName(dbName)
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
        createAutonomousDatabase createAutonomousDatabaseObj = new createAutonomousDatabase();
        CommandLine.run(createAutonomousDatabaseObj, args);
    }
}
