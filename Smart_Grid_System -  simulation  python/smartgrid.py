import csv

class SmartGrid:
    def __init__(self, vertices):
        self.places = vertices
        self.Adjlist = [[] for _ in range(vertices)]
        self.path = [0] * (vertices + 1)
        self.voltpath = [0.0] * (vertices + 1)
        self.temp_distance = 0
        self.rene_res = ['solar Power','wind Energy','Biomass','Hydro Power']
        self.nonrene_res = ['coal','Natural Gas','Oil','Nuclear Power']
        self.placeMap = {"grid": 0}
        self.voltdict = {30: 33.0, 50: 48.0, 60: 50.0, 80: 60.0, 90: 66.0, 20: 0.44, 70: 49.0, 40: 40.0,
                         85: 63.0, 120: 345.0, 100: 345.0, 200: 400.0, 300: 450.0, 400: 490.0, 190: 380.0, 160: 360.0}
        self.place_names = [""] * vertices

    class Container:
        def __init__(self, s, d, dist, volt, from_, to):
            self.source = s
            self.destination = d
            self.distance = dist
            self.voltage = volt
            self.from_ = from_
            self.to = to

    def add_edge(self, src, des, dist, volt, from_, to):
        c = self.Container(src, des, dist, volt, from_, to)
        self.Adjlist[src].append(c)

    def remove_edge(self, src, des):
        for neigh in self.Adjlist[src]:
            if neigh.destination == des:
                self.Adjlist[src].remove(neigh)
                print("edge removed:", src, "---", des)
                break

    def first_find_path(self, src, des, p_src):
        for neighbor in self.Adjlist[src]:
            self.temp_distance += neighbor.distance
            self.path[p_src] = neighbor.destination
            self.voltpath[p_src] = neighbor.voltage
            if neighbor.destination == des:
                return self.temp_distance
            dist = self.first_find_path(neighbor.destination, des, p_src + 1)
            if dist == 0:
                self.temp_distance -= neighbor.distance
                self.path[p_src] = 0
                self.voltpath[p_src] = 0
            else:
                return dist
        return 0

    def second_find_path(self, src, des):
        for neighbor in self.Adjlist[src]:
            self.temp_distance += neighbor.distance
            if neighbor.destination == des:
                return self.temp_distance
            dist = self.second_find_path(neighbor.destination, des)
            if dist == 0:
                self.temp_distance -= neighbor.distance
            else:
                return dist
        return 0

    def grid_optimization(self, s, d, dist):
        for i in range(self.places):
            self.path[i] = 0

        for i in range(self.places):
            self.voltpath[i] = 0.0

        getvolt = self.voltdict[dist]

        min_distance = self.first_find_path(0, d, 1)
        self.temp_distance = 0
        org_dis = self.second_find_path(0, s)
        sum_ = org_dis + dist

        if sum_ > min_distance:
            print("min-distance:", "0--", d, "is", min_distance)
        else:
            print("min-distance:", "0--", d, "is", sum_)
            j = self.places - 1
            while j > 0:
                if self.path[j] == 0:
                    j -= 1
                else:
                    self.remove_edge(self.path[j - 1], self.path[j])
                    self.add_edge(s, d, dist, getvolt, 'c', 'c')
                    print("edge added:", s, "---", d)
                    j = 0

    def print_grid(self):
        print("S  d      dis      volt")
        for i in range(self.places):
            for neigh in self.Adjlist[i]:
                print("(" + str(neigh.source) + ", " + str(neigh.destination) + ")" + " --> " + str(neigh.distance) +
                      " <--> " + str(neigh.voltage))
        for key, value in self.placeMap.items():
            print("Key:", key, ", Value:", value)

    def contains_value(self, key):
        return key in self.placeMap

    def add_place(self, name, num):
        self.placeMap[name] = num

    def store_graph_to_file(self, filename):
        with open(filename, 'w') as file:
            writer = csv.writer(file)
            for i in range(len(self.Adjlist)):
                edges = self.Adjlist[i]
                for edge in edges:
                    writer.writerow([edge.source, edge.destination, edge.distance, edge.voltage, edge.from_, edge.to,
                                     self.place_names[edge.source], self.place_names[edge.destination]])


def main():
    g = SmartGrid(count_destinations_from_file())

    read_places_from_file(g)
    read_edges_from_file(g)
    g.store_graph_to_file("initial_grid.txt")
    read_grid_optimization_from_file(g)
    g.print_grid()
    g.store_graph_to_file("Efficient_grid.txt")
    check_destination_from_grid("Krishnapuram")



def count_destinations_from_file():
    destinations = set()

    with open("edges.txt", 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            destinations.add(row[1])

    return len(destinations) + 1


def read_edges_from_file(g):
    with open("edges.txt", 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            src = row[0]
            des = row[1]
            dist = int(row[2])
            volt = float(row[3])
            from_ = row[4]
            to = row[5]
            if g.contains_value(src) and g.contains_value(des):
                a = g.placeMap[src]
                b = g.placeMap[des]
                g.add_edge(a, b, dist, volt, from_, to)


def read_grid_optimization_from_file(g):
    with open("Additionaledges.txt", 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            src = row[0]
            des = row[1]
            dist = int(row[2])
            if g.contains_value(src) and g.contains_value(des):
                a = g.placeMap[src]
                b = g.placeMap[des]
                g.grid_optimization(a, b, dist)


def read_places_from_file(g):
    num = 1
    g.place_names[0] = "grid"
    with open("edges.txt", 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            des = row[1]
            if des not in g.placeMap:
                g.add_place(des, num)
                g.place_names[num] = des
                num += 1

def check_destination_from_grid(d):
    try:
        with open("new_dataset.txt", "r") as file:
            found = False

            for line in file:
                parts = line.strip().split(",")

                if len(parts) >= 2:
                    des = parts[1]
                    src = parts[0]
                    if des == d:
                        found = True  # Update the flag when the destination is found
                        break

            if found:
                print("destination found:", d)
            else:
                print("destination not found")

    except FileNotFoundError as e:
        print(e)




if __name__ == "__main__":
    main()
