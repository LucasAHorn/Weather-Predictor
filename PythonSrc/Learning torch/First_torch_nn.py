# This will estimate x**3 with a linear line

import torch
import torch.nn as nn

# Generate some data
x = torch.linspace(-10, 10, 200).view(-1, 1)  # 100 points from -2 to 2
y = x**3 - 5*x  # the target function


model = nn.Linear(1,1)


criterion = nn.MSELoss()  # measures squared difference
optimizer = torch.optim.Adam(model.parameters(), lr=0.01)


for epoch in range(1000):
    # Forward pass
    y_pred = model(x)

    # Compute loss
    loss = criterion(y_pred, y)

    # Backpropagation
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()

    if epoch % 100 == 0:
        print(f"Epoch {epoch}, Loss: {loss.item():.4f}")



import matplotlib.pyplot as plt

with torch.no_grad():
    y_pred = model(x)

# Show x=0 and y=0 axes
plt.axhline(0, color='black', linewidth=1)  # y=0 line
plt.axvline(0, color='black', linewidth=1)  # x=0 line
plt.scatter(x, y, label="True")
plt.plot(x, y_pred, label="Predicted", color="red")
plt.legend()
plt.show()