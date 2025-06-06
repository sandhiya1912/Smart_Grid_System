package main.java.smartgrid;


import main.java.smartgrid.service.SmartGrid;
import main.java.smartgrid.util.FileUtils;

import java.util.Scanner;

public class SmartGridApp {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String basePath = "resources/";

        SmartGrid g = new SmartGrid(FileUtils.countDestinationsFromFile(basePath + "edges.txt"));

        FileUtils.readPlacesFromFile(g, basePath + "edges.txt");
        FileUtils.readEdgesFromFile(g, basePath + "edges.txt");
        g.storeGraphToFile("output/initial_grid.txt");

        FileUtils.readGridOptimizationFromFile(g, basePath + "Additionaledges.txt");

        g.printGrid();
        g.storeGraphToFile("output/Efficient_grid.txt");

        System.out.println("Give your Destination: ");
        String d = s.nextLine();

        FileUtils.checkDestinationFromGrid(d, basePath + "newdata.txt");

        s.close();
    }
}
