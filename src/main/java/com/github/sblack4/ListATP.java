package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.model.AutonomousDatabaseSummary;
import com.oracle.bmc.database.requests.ListAutonomousDatabasesRequest;
import com.oracle.bmc.database.responses.ListAutonomousDatabasesResponse;
import picocli.CommandLine;

import java.util.List;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;


@Command(name="List", header = "@|green Delete an ATP instance with the JAVA OCI SDK |@" )
public class ListATP implements Runnable {
    @Option(names={"-cId", "--compartmentId"}, required = true, description = "Compartment ID")
    public String compartmentId;

    @Option(names={"-c", "--config"}, required = false, description = "OCI Config file path, defaults to ${DEFAULT-VALUE}")
    String configurationFilePath = "~/.oci/config";

    @Option(names={"-p", "--profile"}, required = false, description = "OCI profile, defaults to ${DEFAULT-VALUE}")
    String profile = "DEFAULT";

    @Option(names = { "-h", "--help" }, usageHelp = true,
            description = "Displays this help message and quits.")
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
        CommandLine.run(new ListATP(), args);
    }
}
