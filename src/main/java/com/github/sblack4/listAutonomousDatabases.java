package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.model.AutonomousDatabaseSummary;
import com.oracle.bmc.database.requests.ListAutonomousDatabasesRequest;
import com.oracle.bmc.database.responses.ListAutonomousDatabasesResponse;
import picocli.CommandLine;
import picocli.CommandLine.*;
import java.util.List;


/**
 * listAutonomousDatabases - no necessary args
 */
@Command(name="list", header = "@|fg(5;0;0),bg(0;0;0) Delete an ATP instance with the JAVA OCI SDK |@" )
public class listAutonomousDatabases implements Runnable {
    @Parameters(index = "0",
            description = "Compartment ID, retrieved from OCI Config")
    public String compartmentId;

    @Option(names={"-r", "--region"},
            description = "region as specified by the enum com.oracle.bmc.Region or it's id " +
                    ", like EU_FRANKFURT_1 or fra \n" +
                    "UK_LONDON_1 or lhr \n" +
                    "US_ASHBURN_1 or iad \n" +
                    "US_PHOENIX_1  or phx \n etc..")
    public String regionOrId;

    @Option(names={"-c", "--config"}, description = "OCI Config file path, defaults to ${DEFAULT-VALUE}")
    String configurationFilePath = "~/.oci/config";

    @Option(names={"-p", "--profile"}, description = "OCI profile, defaults to ${DEFAULT-VALUE}")
    String profile = "DEFAULT";

    @Option(names = { "-h", "--help" }, usageHelp = true,
            description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public Region getRegion() {
        // I think this is
        Region region = Region.US_PHOENIX_1;

        try {
            region = Region.fromRegionCodeOrId(this.regionOrId);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Using default region" + region.toString());
        }

        return region;
    }

    @Override
    public void run() {
        // busyn-ness logix
        try {
            AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

            System.out.println("\n================================\n");
            System.out.println("Credentials 'n such; ");
            System.out.println(provider.toString());

            DatabaseClient dbClient = new DatabaseClient(provider);
            dbClient.setRegion(this.getRegion());

            ListAutonomousDatabasesRequest dbReq = ListAutonomousDatabasesRequest.builder()
                            .compartmentId(compartmentId)
                            .build();

            ListAutonomousDatabasesResponse dbResp = dbClient.listAutonomousDatabases(dbReq);

            List<AutonomousDatabaseSummary> dbItems = dbResp.getItems();

            for (AutonomousDatabaseSummary item : dbItems) {
                System.out.println("\n================================");
                System.out.println("id = " + item.getId());
                System.out.println("dbName = " + item.getDbName());
                System.out.println("displayName = " + item.getDisplayName());
                System.out.println("serviceConsoleUrl = " + item.getServiceConsoleUrl());
                System.out.println("LifecycleState = " + item.getLifecycleState());
                System.out.println("\n" + item.toString());
            }

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
        CommandLine.run(new listAutonomousDatabases(), args);
    }
}
