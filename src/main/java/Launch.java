import com.google.gson.Gson;
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
import java.util.Objects;
import java.util.regex.Pattern;

public class Launch {

    public static void main(String[] args) throws IOException, ZipException {

        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        String source = "./data/downloaded/" + date + ".zip";
        String destination = "./data/extracted/" + date + "/";

        InputStream file = new URL("https://files.ssi.dk/covid19/overvagning/dashboard/covid-19_dashboard_19032021-j3k4").openStream();
        Files.copy(file, Paths.get(source), StandardCopyOption.REPLACE_EXISTING);

        File directory = new File(destination);

        if (directory.mkdir())
            System.out.println("Created directory for " + date);

        ZipFile zipFile = new ZipFile(source);
        zipFile.extractAll(destination);

        FileReader reader = new FileReader(destination + "Regionalt_DB/01_noegle_tal.csv");
        CSVReader csvReader = new CSVReader(reader);

        List<String[]> strings = csvReader.readAll();

        for (int i = 1; i < strings.size(); i++) {

            String[] data = Arrays.toString(strings.get(i)).split(";");

            int infected = Integer.parseInt(data[3]);
            int dead = Integer.parseInt(data[4]);
            int healthy = Integer.parseInt(data[5]);
            int hospitalized = Integer.parseInt(data[6]);
            int test = Integer.parseInt(data[7]);

            if (!Corona.containsRegion(data[1]))
                Corona.CORONA_STATISTICS.add(new Corona(data[1]));

            Corona region = Corona.getRegion(data[1]);

            if (region == null)
                continue;

            region.addInfected(infected);
            region.addDead(dead);
            region.addHealthy(healthy);
            region.addHospitalized(hospitalized);
            region.addTest(test);


        }

        try (Writer writer = Files.newBufferedWriter(Paths.get("./data/json/" + date + ".json"), StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(Corona.CORONA_STATISTICS, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
