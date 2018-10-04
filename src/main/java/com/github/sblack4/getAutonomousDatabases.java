package com.github.sblack4;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.model.AutonomousDatabase;
import com.oracle.bmc.database.requests.GetAutonomousDatabaseRequest;
import picocli.CommandLine;


import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;


@Command(name="get",
        sortOptions = false,
        header = "@|fg(5;0;0),bg(0;0;0)  Get an ATP instance with the JAVA OCI SDK |@" )
public class getAutonomousDatabases implements Runnable {
    @Option(names={"-id", "--adw-id"}, required = true, description = "Autonomous Database ID")
    public String adwId;

    @Option(names={"-cid", "--compartment-id"}, description = "Compartment ID, retrieved from OCI Config")
    public String compartmentId;

    @Option(names={"-c", "--config"}, description = "OCI Config file path, defaults to ${DEFAULT-VALUE}")
    String configurationFilePath = "~/.oci/config";

    @Option(names={"-p", "--profile"}, description = "OCI profile, defaults to ${DEFAULT-VALUE}")
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
