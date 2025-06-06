package main.java.smartgrid.model;

public class Container {
    public int source, destination, distance;
    public double voltage;
    public char from, to;

    public Container(int s, int d, int dist, double volt, char from, char to) {
        this.source = s;
        this.destination = d;
        this.distance = dist;
        this.voltage = volt;
        this.from = from;
        this.to = to;
    }
}
