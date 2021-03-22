package statistics;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Corona {

    private static final ArrayList<Corona> CORONA_STATISTICS = new ArrayList<>();

    private final String region;

    private int infected;
    private int dead;
    private int recovered;
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

    public void addRecovered(int amount) {
        this.recovered += amount;
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

    public static void main(String[] args) {

        Corona[] actual = getStoredData("22-03-2021");
        Corona[] old = getStoredData("18-03-2021");

        assert actual != null;
        assert old != null;

        for (int i = 0; i < actual.length; i++) {

            int deltaInfected = subtract(actual[i].infected, old[i].infected);
            int deltaDead = subtract(actual[i].dead, old[i].dead);
            int deltaHealthy = subtract(actual[i].recovered, old[i].recovered);
            int deltaHospitalized = subtract(actual[i].hospitalized, old[i].hospitalized);
            int deltaTest = subtract(actual[i].test, old[i].test);

            System.out.println("Region: " + actual[i].getRegion());
            System.out.println(getSymbol("Infected", deltaInfected));
            System.out.println(getSymbol("Dead", deltaDead));
            System.out.println(getSymbol("Recovered", deltaHealthy));
            System.out.println(getSymbol("Hospitalized", deltaHospitalized));
            System.out.println(getSymbol("Test", deltaTest));
            System.out.println();

        }

    }

    private static String getSymbol(String category, int input) {
        if (input == 0)
            return category + ": " + input;
        return category + ": " + (input > 0 ? "+" : "-") + input;
    }

    private static int subtract(int input_one, int input_two) {
        return input_one > input_two ? input_one - input_two : input_two - input_one;
    }

    public static Corona[] getStoredData(String date) {
        try (Reader reader = Files.newBufferedReader(Paths.get("./data/json/" + date + ".json"), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, Corona[].class);
        } catch (IOException e) {
            System.err.println(date + ".json was not found.");
            return null;
        }
    }

    public static ArrayList<Corona> getCoronaStatistics() {
        return CORONA_STATISTICS;
    }


}
