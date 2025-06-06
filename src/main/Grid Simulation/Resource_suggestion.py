import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

def calculate_distance(point1, point2):
    return np.sqrt((point1[0] - point2[0])*2 + (point1[1] - point2[1])*2)

user_power_consumption = [100, 200, 150]
user_locations = [(0, 0), (1, 2), (3, 1)]
grid_location = (0, 0)

renewable_capacity = 500
non_renewable_capacity = 800

distances_to_grid = [calculate_distance(user_loc, grid_location) for user_loc in user_locations]

def renewable_generation(time_of_day):
    if 8 <= time_of_day <= 16:
        return 300  # Example: kW
    else:
        return 100  # Example: kW

transmission_loss_factor = 0.02

optimal_renewable_allocation = [200, 400, 300]  # Example: kW
optimal_non_renewable_allocation = [100, 200, 150]  # Example: kW

df = pd.DataFrame({
    'User': range(1, len(user_power_consumption) + 1),
    'Power Consumption (kW)': user_power_consumption,
    'Distance to Grid (km)': distances_to_grid,
    'Renewable Allocation (kW)': optimal_renewable_allocation,
    'Non-Renewable Allocation (kW)': optimal_non_renewable_allocation
})

fig, axs = plt.subplots(2, 1, figsize=(10, 8))

df.plot(kind='bar', x='User', y=['Power Consumption (kW)', 'Renewable Allocation (kW)', 'Non-Renewable Allocation (kW)'], ax=axs[0])
axs[0].set_title('Power Consumption and Allocation')
axs[0].set_ylabel('Power (kW)')
axs[0].set_xlabel('User')

df.plot(kind='scatter', x='Distance to Grid (km)', y='Renewable Allocation (kW)', color='green', label='Renewable Allocation', ax=axs[1])
df.plot(kind='scatter', x='Distance to Grid (km)', y='Non-Renewable Allocation (kW)', color='red', label='Non-Renewable Allocation', ax=axs[1])
axs[1].set_title('Allocation vs Distance to Grid')
axs[1].set_ylabel('Allocation (kW)')
axs[1].set_xlabel('Distance to Grid (km)')

plt.tight_layout()
plt.show()