import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import statistics.Corona;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class Launch {

    private final static String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

    private final static String source = "./data/downloaded/" + date + ".zip";
    private final static String destination = "./data/extracted/" + date + "/";

    public static void main(String[] args) {

        Timer timer = new Timer();
        Calendar date = Calendar.getInstance();

        date.set(Calendar.HOUR_OF_DAY, 16);
        date.set(Calendar.MINUTE, 10);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        System.out.println("Planned task will execute " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ".");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    download();
                    extract();
                    readData();
                    writeToJson();
                } catch (IOException | ZipException e) {
                    e.printStackTrace();
                }

            }
        }, date.getTime());

    }

    private static void download() throws IOException {
        InputStream file = new URL(Objects.requireNonNull(getUrl())).openStream();
        Files.copy(file, Paths.get(source), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Fetching file for " + date + " from " + getUrl() + "...");

        File directory = new File(destination);

        if (directory.mkdir())
            System.out.println("Created directory for " + date);
    }

    private static void extract() throws ZipException {
        ZipFile zipFile = new ZipFile(source);
        zipFile.extractAll(destination);
        System.out.println("Extracting " + date + ".zip");
    }

    private static void readData() throws IOException {
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
    }

    private static void writeToJson() {
        System.out.println("Writing to json...");

        try (Writer writer = Files.newBufferedWriter(Paths.get("./data/json/" + date + ".json"), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(Corona.getCoronaStatistics(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Complete!");
    }

    private static String getUrl() {

        try {

            System.out.println("Connecting to SSI...");

            Document url = Jsoup.connect("https://covid19.ssi.dk/overvagningsdata/download-fil-med-overvaagningdata").userAgent("Mozilla/5.0").get();

            return url.select("div.accordion-body").select("a[href]").attr("href");

        } catch (IOException e) {

            System.err.println("Connection failed.");
            e.printStackTrace();
            System.exit(0);

            return null;

        }

    }


}
