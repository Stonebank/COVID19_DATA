import com.opencsv.CSVReader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Launch {

    public static void main(String[] args) throws IOException, ZipException {

        // test to gather data

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

        for (String[] strings : csvReader.readAll()) {
            System.out.println(strings[0]);
        }

    }

}
