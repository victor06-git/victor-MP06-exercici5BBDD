package cat.iesesteveterradas;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class PR210Honor {
    private static final String DB_FILE = Paths.get(System.getProperty("user.dir"), "data", "forhonor.db").toString();
    private final DBHelper db;

    public PR210Honor() {
        db = new DBHelper(DB_FILE);
        db.initializeDatabase();

    }

    public void run() {
        try (Scanner sc = new Scanner(System.in)) {
            boolean exit = false;
            while (!exit) {
                printMenu();
                System.out.print("Tria una opció: ");
                String line = sc.nextLine().trim();
                switch (line) {
                    case "1":
                        showTable(sc);
                        break;
                    case "2":
                        showPersonatgesByFaccio(sc);
                        break;
                    case "3":
                        showBestAtacantByFaccio(sc);
                        break;
                    case "4":
                        showBestDefensorByFaccio(sc);
                        break;
                    case "5":
                        exit = true;
                        break;
                    default:
                        System.out.println("Opció invàlida. Intenta-ho de nou.");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- PR210Honor - Menú ---");
        System.out.println("1) Mostrar una taula (Faccio / Personatge)");
        System.out.println("2) Mostrar personatges per facció");
        System.out.println("3) Mostrar el millor atacant per facció");
        System.out.println("4) Mostrar el millor defensor per facció");
        System.out.println("5) Sortir");
    }

    private void showTable(Scanner sc) {
        System.out.print("Quina taula vols veure (Faccio / Personatge): ");
        String t = sc.nextLine().trim().toLowerCase();
        if (t.equals("faccio") || t.equals("facció") || t.equals("facciones")) {
            List<Faccio> facs = db.getAllFaccions();
            System.out.println("--- Faccions ---");
            facs.forEach(System.out::println);
        } else if (t.equals("personatge") || t.equals("personatges") || t.equals("personaje")) {
            List<Personatge> ps = db.getAllPersonatges();
            System.out.println("--- Personatges ---");
            ps.forEach(System.out::println);
        } else {
            System.out.println("Taula desconeguda.");
        }
    }

    private int chooseFaccio(Scanner sc) {
        List<Faccio> facs = db.getAllFaccions();
        System.out.println("Tria una facció pel seu id:");
        facs.forEach(f -> System.out.println(f.getId() + ") " + f.getNom()));
        System.out.print("id = ");
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Id invàlid.");
            return -1;
        }
    }

    private void showPersonatgesByFaccio(Scanner sc) {
        int id = chooseFaccio(sc);
        if (id < 0)
            return;
        List<Personatge> ps = db.getPersonatgesByFaccio(id);
        // find faccio name
        String nomF = db.getAllFaccions().stream().filter(f -> f.getId() == id).map(Faccio::getNom).findFirst()
                .orElse("(desconegut)");
        System.out.println("Personatges de la facció: " + nomF);
        if (ps.isEmpty())
            System.out.println("(cap personatge)");
        else
            ps.forEach(System.out::println);
    }

    private void showBestAtacantByFaccio(Scanner sc) {
        int id = chooseFaccio(sc);
        if (id < 0)
            return;
        Personatge p = db.getBestAtacantByFaccio(id);
        String nomF = db.getAllFaccions().stream().filter(f -> f.getId() == id).map(Faccio::getNom).findFirst()
                .orElse("(desconegut)");
        System.out.println("Millor atacant de la facció: " + nomF);
        if (p == null)
            System.out.println("(cap personatge)");
        else
            System.out.println(p);
    }

    private void showBestDefensorByFaccio(Scanner sc) {
        int id = chooseFaccio(sc);
        if (id < 0)
            return;
        Personatge p = db.getBestDefensorByFaccio(id);
        String nomF = db.getAllFaccions().stream().filter(f -> f.getId() == id).map(Faccio::getNom).findFirst()
                .orElse("(desconegut)");
        System.out.println("Millor defensor de la facció: " + nomF);
        if (p == null)
            System.out.println("(cap personatge)");
        else
            System.out.println(p);
    }

    public static void main(String[] args) {
        PR210Honor app = new PR210Honor();
        app.run();
    }
}
