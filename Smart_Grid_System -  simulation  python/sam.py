import random

# Define lists of districts, cities, grid stations, and villages
districts = ['Chennai', 'Madurai', 'Coimbatore', 'Trichy', 'Salem', 'Erode', 'Vellore', 'Tirunelveli', 'Tiruppur', 'Thanjavur']
cities = ['Chennai', 'Madurai', 'Coimbatore', 'Trichy', 'Salem', 'Erode', 'Vellore', 'Tirunelveli', 'Tiruppur', 'Thanjavur']
grid_stations = ['Station A', 'Station B', 'Station C', 'Station D', 'Station E']
villages = ['Village {}'.format(i) for i in range(1, 10001)]

# Initialize an empty list to store the dataset
dataset = []

# Generate 100,000 entries
for _ in range(100000):
    district = random.choice(districts)
    city = random.choice(cities)
    grid_station = random.choice(grid_stations)
    village = random.choice(villages)
    distance = random.randint(5, 50)  # Random distance between 5 and 50 km
    dataset.append([district, city, grid_station, village, distance])

# Write the dataset to a CSV file
with open('tamil_nadu_dataset.csv', 'w') as f:
    f.write('District,City,Grid Station,Village,Distance (km)\n')
    for entry in dataset:
        f.write(','.join(map(str, entry)) + '\n')

print("Dataset generated successfully.")