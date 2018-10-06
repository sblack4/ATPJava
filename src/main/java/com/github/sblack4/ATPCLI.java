package com.github.sblack4;


import com.oracle.bmc.Region;
import picocli.CommandLine;
import picocli.CommandLine.*;

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
    public void run(){

    }

}
