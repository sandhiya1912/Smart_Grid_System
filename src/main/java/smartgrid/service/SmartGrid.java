package main.java.smartgrid.service;

import main.java.smartgrid.model.Container;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SmartGrid {
    List<List<Container>> Adjlist;
    private int places;
    int[] path;
    double[] voltpath;
    int temp_distance = 0;
    public Map<String, Integer> placeMap = new HashMap<>();
    Map<Integer, Double> voltdict = new HashMap<>();
    public String[] place_names;
    double[] heuristic;

    public SmartGrid(int vertices) {
        this.places = vertices;
        this.Adjlist = new ArrayList<>(vertices);
        this.path = new int[vertices + 1];
        this.voltpath = new double[vertices + 1];
        this.place_names = new String[vertices];
        this.heuristic = new double[vertices];

        // Initialize voltage dictionary
        voltdict.put(30, 33.0);
        voltdict.put(50, 48.0);
        voltdict.put(60, 50.0);
        voltdict.put(80, 60.0);
        voltdict.put(90, 66.0);
        voltdict.put(20, 0.44);
        voltdict.put(70, 49.0);
        voltdict.put(40, 40.0);
        voltdict.put(85, 63.0);
        voltdict.put(120, 345.0);
        voltdict.put(100, 345.0);
        voltdict.put(200, 400.0);
        voltdict.put(300, 450.0);
        voltdict.put(400, 490.0);
        voltdict.put(190, 380.0);
        voltdict.put(160, 360.0);

        placeMap.put("grid", 0);

        for (int i = 0; i < vertices; i++) {
            this.Adjlist.add(new ArrayList<>());
        }

        for (int i = 0; i < vertices; i++) {
            heuristic[i] = 0; // Will be updated externally
        }
    }

    public void addEdge(int src, int des, int dist, double volt, char from, char to) {
        Container c = new Container(src, des, dist, volt, from, to);
        Adjlist.get(src).add(c);
    }

    public void removeEdge(int src, int des) {
        Iterator<Container> iterator = Adjlist.get(src).iterator();
        while (iterator.hasNext()) {
            Container neigh = iterator.next();
            if (neigh.destination == des) {
                iterator.remove();
                System.out.println("Edge removed: " + src + " --- " + des);
                break;
            }
        }
    }

    public int First_findpath(int src, int des, int p_src) {
        for (Container neighbor : Adjlist.get(src)) {
            temp_distance += neighbor.distance;
            path[p_src] = neighbor.destination;
            voltpath[p_src] = neighbor.voltage;
            if (neighbor.destination == des) {
                return temp_distance;
            }
            int dist = First_findpath(neighbor.destination, des, p_src + 1);
            if (dist == 0) {
                temp_distance -= neighbor.distance;
                path[p_src] = 0;
                voltpath[p_src] = 0;
            } else {
                return dist;
            }
        }
        return 0;
    }

    public int Second_findpath(int src, int des) {
        for (Container neighbor : Adjlist.get(src)) {
            temp_distance += neighbor.distance;

            if (neighbor.destination == des) {
                return temp_distance;
            }
            int dist = Second_findpath(neighbor.destination, des);
            if (dist == 0) {
                temp_distance -= neighbor.distance;
            } else {
                return dist;
            }
        }
        return 0;
    }

    public void updateHeuristics(int goal) {
        for (int i = 0; i < places; i++) {
            heuristic[i] = estimateDistance(i, goal);
        }
    }

    private int estimateDistance(int from, int to) {
        boolean[] visited = new boolean[places];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { from, 0 });
        visited[from] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int node = current[0];
            int dist = current[1];

            if (node == to)
                return dist;

            for (Container edge : Adjlist.get(node)) {
                if (!visited[edge.destination]) {
                    visited[edge.destination] = true;
                    queue.add(new int[] { edge.destination, dist + edge.distance });
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public void GridOptimization(int s, int d, int dist) {
        Arrays.fill(path, 0);
        Arrays.fill(voltpath, 0.0);

        double getvolt = voltdict.getOrDefault(dist, 0.0);

        updateHeuristics(d); // update heuristic values for A*

        int min_distance = AStarPath(0, d);
        temp_distance = 0;
        int org_dis = Second_findpath(0, s);
        int sum = org_dis + dist;

        if (sum > min_distance) {
            System.out.println("Min-distance: 0 -- " + d + " is " + min_distance);
        } else {
            System.out.println("Min-distance: 0 -- " + d + " is " + sum);
            int j = places - 1;
            while (j > 0) {
                if (path[j] == 0) {
                    j--;
                } else {
                    removeEdge(path[j - 1], path[j]);
                    addEdge(s, d, dist, getvolt, 'c', 'c');
                    System.out.println("Edge added: " + s + " --- " + d);
                    j = 0;
                }
            }
        }
    }

    public int AStarPath(int start, int goal) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        boolean[] visited = new boolean[places];
        pq.add(new int[] { start, 0 });

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];
            int gCost = current[1];

            if (node == goal)
                return gCost;

            if (visited[node])
                continue;
            visited[node] = true;

            for (Container edge : Adjlist.get(node)) {
                if (!visited[edge.destination]) {
                    int fCost = gCost + edge.distance + (int) heuristic[edge.destination];
                    pq.add(new int[] { edge.destination, fCost });
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public void printGrid() {
        System.out.println("Source  Destination  Distance  Voltage");
        for (int i = 0; i < places; i++) {
            for (Container neigh : Adjlist.get(i)) {
                System.out.println("(" + neigh.source + ", " + neigh.destination + ") --> " +
                        neigh.distance + " <--> " + neigh.voltage);
            }
        }
        for (String key : placeMap.keySet()) {
            System.out.println("Place: " + key + ", Index: " + placeMap.get(key));
        }
    }

    public boolean containsValue(String key) {
        return placeMap.containsKey(key);
    }

    public void addplace(String name, int num) {
        placeMap.put(name, num);
    }

    public void storeGraphToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < Adjlist.size(); i++) {
                List<Container> edges = Adjlist.get(i);
                for (Container edge : edges) {
                    writer.write(edge.source + "," + edge.destination + "," + edge.distance + "," +
                            edge.voltage + "," + edge.from + "," + edge.to + "," + place_names[edge.source] + "," +
                            place_names[edge.destination]);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
