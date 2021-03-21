import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import statistics.Corona;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Launch {

    public static void main(String[] args) throws IOException, ZipException {

        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        String source = "./data/downloaded/" + date + ".zip";
        String destination = "./data/extracted/" + date + "/";

        InputStream file = new URL("https://files.ssi.dk/covid19/overvagning/dashboard/covid-19_dashboard_19032021-j3k4").openStream();
        Files.copy(file, Paths.get(source), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Fetching file for " + date + "...");

        File directory = new File(destination);

        if (directory.mkdir())
            System.out.println("Created directory for " + date);

        ZipFile zipFile = new ZipFile(source);
        zipFile.extractAll(destination);
        System.out.println("Extracting " + date + ".zip");

        CSVReader csvReader = new CSVReader(new FileReader(destination + "Regionalt_DB/01_noegle_tal.csv"));
        List<String[]> csvData = csvReader.readAll();

        System.out.println("Reading CSV data...");

        for (int i = 1; i < csvData.size(); i++) {

            String[] data = Arrays.toString(csvData.get(i)).split(";");

            int infected = Integer.parseInt(data[3]);
            int dead = Integer.parseInt(data[4]);
            int healthy = Integer.parseInt(data[5]);
            int hospitalized = Integer.parseInt(data[6]);
            int test = Integer.parseInt(data[7]);

            if (!Corona.containsRegion(data[1]))
                Corona.getCoronaStatistics().add(new Corona(data[1]));

            Corona region = Corona.getRegion(data[1]);

            if (region == null) {
                System.err.println(data[1] + " is skipped due to an error...");
                continue;
            }

            region.addInfected(infected);
            region.addDead(dead);
            region.addHealthy(healthy);
            region.addHospitalized(hospitalized);
            region.addTest(test);


        }

        System.out.println("Writing to json...");

        try (Writer writer = Files.newBufferedWriter(Paths.get("./data/json/" + date + ".json"), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(Corona.getCoronaStatistics(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Complete!");

    }

}
