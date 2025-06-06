package main.java.smartgrid.util;

import main.java.smartgrid.service.SmartGrid;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class FileUtils {

    public static int countDestinationsFromFile(String filePath) {
        HashSet<String> destinations = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    destinations.add(parts[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // +1 for "grid"
        return destinations.size() + 1;
    }

    public static void readEdgesFromFile(SmartGrid g, String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                String src = parts[0];
                String des = parts[1];
                int dist = Integer.parseInt(parts[2]);
                double volt = Double.parseDouble(parts[3]);
                char from = parts[4].charAt(0);
                char to = parts[5].charAt(0);

                if (g.containsValue(src) && g.containsValue(des)) {
                    int a = g.placeMap.get(src);
                    int b = g.placeMap.get(des);
                    g.addEdge(a, b, dist, volt, from, to);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readGridOptimizationFromFile(SmartGrid g, String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String src = parts[0];
                String des = parts[1];
                int dist = Integer.parseInt(parts[2]);

                if (g.containsValue(src) && g.containsValue(des)) {
                    int a = g.placeMap.get(src);
                    int b = g.placeMap.get(des);
                    g.GridOptimization(a, b, dist);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readPlacesFromFile(SmartGrid g, String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            int num = 1;
            int i = 1;
            g.place_names[0] = "grid";

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String des = parts[1];
                if (!g.containsValue(des)) {
                    g.addplace(des, num);
                    g.place_names[i] = des;
                    num++;
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void checkDestinationFromGrid(String d, String filePath) {
        boolean found = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String des = parts[1];
                    if (des.equals(d)) {
                        found = true;
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (found) {
            System.out.println("Destination found: " + d);
        } else {
            System.out.println("Destination not found");
        }
    }
}
