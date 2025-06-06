import networkx as nx
import matplotlib.pyplot as plt

G = nx.Graph()

with open("initial_grid.txt", "r") as file:
    data = file.readlines()

edges = [
    tuple(map(int, line.strip().split(',')[:3])) for line in data if line.strip()
]

for edge in edges:
    source, destination, distance = edge
    G.add_edge(source, destination, weight=distance)

pos = nx.spring_layout(G, seed=42)

nx.draw(G, pos, with_labels=False, node_size=700, font_size=10, font_color='black',
        node_color='skyblue', edge_color='black', width=2)

labels = nx.get_edge_attributes(G, 'weight')
nx.draw_networkx_edge_labels(G, pos, edge_labels=labels)

plt.title('Graph Visualization')
plt.axis('off')
plt.show()
