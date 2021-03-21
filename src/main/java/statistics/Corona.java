package statistics;

import java.util.ArrayList;

public class Corona {

    private static final ArrayList<Corona> CORONA_STATISTICS = new ArrayList<>();

    private final String region;

    private int infected;
    private int dead;
    private int healthy;
    private int hospitalized;
    private int test;

    public Corona(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void addInfected(int amount) {
        this.infected += amount;
    }

    public void addDead(int amount) {
        this.dead += amount;
    }

    public void addHealthy(int amount) {
        this.healthy += amount;
    }

    public void addHospitalized(int amount) {
        this.hospitalized += amount;
    }

    public void addTest(int amount) {
        this.test += amount;
    }

    public static boolean containsRegion(String region) {
        for (Corona data : CORONA_STATISTICS) {
            if (data.getRegion().equalsIgnoreCase(region))
                return true;
        }
        return false;
    }

    public static Corona getRegion(String region) {
        for (Corona data : CORONA_STATISTICS) {
            if (data.getRegion().equalsIgnoreCase(region))
                return data;
        }
        return null;
    }

    public static ArrayList<Corona> getCoronaStatistics() {
        return CORONA_STATISTICS;
    }


}
