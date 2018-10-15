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
@Command(name="list", header = "@|fg(5;0;0),bg(0;0;0) List ATP instances with the JAVA OCI SDK |@" )
public class listAutonomousDatabases extends ATPCLI {

    @Option(names={"-p", "--profile"},
            description = "OCI profile, defaults to ${DEFAULT-VALUE}")
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
            this.compartmentId = this.getCompartmentId(this.configurationFilePath);

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
