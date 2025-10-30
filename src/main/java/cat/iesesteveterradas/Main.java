package cat.iesesteveterradas;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static Connection conn = null;
    private static String filePath = "data/database.sqlite";

    public static void main(String[] args) {
        Path filePath = obtenirPathFitxer();

        try {
            List<String> lines = readFileContent(filePath);

            // Imprimir les línies a la consola
            lines.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("S'ha produït un error en llegir el fitxer: " + e.getMessage());
        }
    }

    public static Path obtenirPathFitxer() {
        return Paths.get(System.getProperty("user.dir"), "data", "bones_practiques_programacio.txt");
    }

    public static List<String> readFileContent(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }

}
