package com.powerboot;

import java.math.BigDecimal;

public class TestMain {


    public static void main(String[] args) {
        String str = "GHS 50.00 SDHSJDBA DJSJDS SDNBNW RRN: 121118151213 DFNSJDNS SNDJBSNAD HSJDBAHB SBNDBS FROM 1231561 SDADN 1S1D1 ";
        int ghsIndex = str.toLowerCase().indexOf("ghs");
        String ghsReplace = str.substring(ghsIndex + 4);
        System.out.println(ghsReplace);
        String ghsAmount = ghsReplace.substring(0, ghsReplace.indexOf(" "));
        System.out.println(ghsAmount);


        int rrnIndex = str.toLowerCase().indexOf(" rrn:");
        String rrnReplace = str.substring(rrnIndex + 6);
        System.out.println(rrnReplace.substring(0, rrnReplace.indexOf(" ")));
    }
}
