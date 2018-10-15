package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.model.CreateAutonomousDatabaseDetails;
import com.oracle.bmc.database.requests.CreateAutonomousDatabaseRequest;
import com.oracle.bmc.database.responses.CreateAutonomousDatabaseResponse;
import picocli.CommandLine.*;
import picocli.CommandLine;

import java.io.IOException;
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

public class createAutonomousDatabase extends ATPCLI {

    @Parameters(index ="0", arity = "0..1",
        description = "Database Name, by default randomly generates one")
    public String dbName;

    @Parameters(index ="1", arity = "0..1",
            description = "Display Name for DB, by default randomly generates one")
    public String displayName;

    @Parameters(index = "2", arity = "0..1",
            description = "Password for DB, by default randomly generates one")
    public String password = "Welcome123456!";

   @Parameters(index = "3", arity = "0..1",
           description = "CPU Cores, defaults to ${DEFAULT-VALUE}")
    public Integer cpuCount = 1;

    @Parameters(index = "4", arity = "0..1",
            description = "DB size in TBs, defaults to ${DEFAULT-VALUE}")
    public Integer dbSize = 1;

    private final String[] funNamesList = new String[]{
            "Floof", "Einstein", "Dancing",
            "Tesla", "Dinosaur", "Autonomous", "Database", "Ellison",
            "Robot", "_As_A_Service_", "Integrated", "Party", "Puppy", "Kitten",
            "InMemory", "ActiveDataGuard", "AdvancedAnalytics", "Multitenant", "OLAP",
            "Partitioning", "RealApplicationClusters", "Sharding", "OracleApplicationExpress",
            "APEX", "SQL", "PL/SQL", "AutomaticStorageManagement", "OracleSecureBackup",
            "Java", "Linux", "Happiness", "Jumping", "Future"
    };

    private List<String> funNames = Arrays.asList(this.funNamesList);

    /**
     * gets s.substring(start, len) or s.substring(start, s.length())
     * Whichever is lesser (to avoid StringIndexOutOfBoundsException)
     * @param s String
     * @param start where to start the substring
     * @param len how long the substring should be
     * @return String, the substring
     */
    public String substringSafe(String s, int start, int len) {
        int lesserLength = Math.min(s.length(), len);
        return s.substring(start, lesserLength);
    }

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
            String funName = getRandomFunName(4);
            this.dbName = this.substringSafe(funName, 0, 13);
        }
        if (this.displayName == null) {
            this.displayName =  getRandomFunName(4);
        }
        if (this.password == null) {
            String funPassword = getRandomFunName(3);
            this.password = this.substringSafe(funPassword, 0, 29);
        }
    }

    // === okay, enough fun. back to the business logic

    public void run() {
        this.generateDetails();
        String profile = "DEFAULT";
        AuthenticationDetailsProvider provider;

        try {
            provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);


            System.out.println("\n================================\n");
            System.out.println("Credentials 'n such; ");
            System.out.println(provider.toString());

            this.compartmentId = this.getCompartmentId(this.configurationFilePath);

            if (this.compartmentId == null || this.compartmentId.isEmpty()) {
                this.compartmentId = provider.getTenantId();
            }

            DatabaseClient dbClient = new DatabaseClient(provider);

            Region region = getRegion(this.configurationFilePath);
            dbClient.setRegion(region);

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

            dbClient.close();
            System.out.println("================================");
            System.out.println("=== DONE ===");

        } catch (BmcException ex) {
            System.out.println("================================");
            System.out.println("=== ERROR ===");
            System.out.println(ex.getMessage());
            System.out.println("================================");
            System.out.println(ex.getOpcRequestId());
            System.out.println(ex.getServiceCode());
            System.out.println(ex.getStatusCode());
            System.out.println(ex.isClientSide());
            System.out.println("================================");
            ex.printStackTrace();

        } catch (IOException ex) {
            System.out.println("file " + configurationFilePath + " or it's config file caused an error");
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createAutonomousDatabase createAutonomousDatabaseObj = new createAutonomousDatabase();
        CommandLine.run(createAutonomousDatabaseObj, args);
    }
}
