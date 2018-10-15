package com.github.sblack4;


import com.oracle.bmc.Region;
import picocli.CommandLine;
import picocli.CommandLine.*;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;

@CommandLine.Command(name="create",
        header = "@|fg(5;0;0),bg(0;0;0) Create an ATP instance with the JAVA OCI SDK |@" )
public class ATPCLI implements Runnable {

    @Option(names={"-r", "--region"},
            description = "region as specified by the enum com.oracle.bmc.Region or it's id " +
                    ", like EU_FRANKFURT_1 or fra \n" +
                    "UK_LONDON_1 or lhr \n" +
                    "US_ASHBURN_1 or iad \n" +
                    "US_PHOENIX_1  or phx \n etc..")
    public String regionOrId;

    @Option(names={"-cid", "--compartment-id"},
            description = "Compartment ID, by default it is retrieved from OCI Config")
    public String compartmentId;

    @Option(names={"-c", "--config"},
            description = "OCI Config file path, defaults to ${DEFAULT-VALUE}")
    public String configurationFilePath = "~/.oci/config";

    @Option(names={"-p", "--profile"},
            description = "OCI profile, defaults to ${DEFAULT-VALUE}")
    public String profile = "DEFAULT";

    @Option(names = { "-h", "--help" },
            usageHelp = true,
            description = "Displays this help message and quits.")
    private boolean helpRequested = false;

    public Region getRegion(String configPath) {
        // I think this is
        Region region = Region.US_PHOENIX_1;

        try {
            ConfigFile configFile = ConfigFileReader.parse(configPath);
            region = Region.fromRegionCodeOrId(configFile.get("region"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (this.regionOrId != null) {
            try {
                region = Region.fromRegionCodeOrId(this.regionOrId);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Using default region" + region.toString());
            }
        }

        return region;
    }

    public String getCompartmentId(String configPath) {
        if (this.compartmentId != null) {
            return this.compartmentId;
        }
        try {
            ConfigFile configFile = ConfigFileReader.parse(configPath);
            String cmptmnt = configFile.get("compartmentid");
            if (cmptmnt == null) {
                return configFile.get("compartmentId");
            }
            return cmptmnt;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public void run(){

    }

}
