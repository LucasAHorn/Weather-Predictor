import torch
import torch.nn as nn
import torch.optim as optim
import csv
import numpy as np

# =========================
# 1. Load CSV data
# =========================
data_list = []

with open("Data/largeData/revised.csv", newline='') as csv_file:
    csv_reader = csv.reader(csv_file)
    
    header = next(csv_reader)  # skip header
    
    for row in csv_reader:
        # Replace empty strings with 0 and convert to float
        cleaned_row = [float(x) if x != '' else 0.0 for x in row]
        data_list.append(cleaned_row)

# Convert to NumPy array first
data_array = np.array(data_list, dtype=np.float32)

# =========================
# 2. Split features and target
# =========================
X = data_array[:, :-1]  # all columns except last
y = data_array[:, -1:]  # last column (target), keep 2D shape

# Convert to PyTorch tensors
X_tensor = torch.tensor(X, dtype=torch.float32)
y_tensor = torch.tensor(y, dtype=torch.float32)

# =========================
# 3. Define neural network
# =========================
class TempPredictor(nn.Module):
    def __init__(self, input_size):
        super().__init__()
        self.model = nn.Sequential(
            nn.Linear(input_size, 32),
            nn.ReLU(),
            nn.Linear(32, 16),
            nn.ReLU(),
            nn.Linear(16,10),
            nn.ReLU(),
            nn.Linear(10, 1)  # output is single temperature
        )
    
    def forward(self, x):
        return self.model(x)

model = TempPredictor(input_size=X_tensor.shape[1])

# =========================
# 4. Loss and optimizer
# =========================
criterion = nn.MSELoss()  # regression task
optimizer = optim.Adam(model.parameters(), lr=0.001)

# =========================
# 5. Training loop
# =========================
epochs = 1000
for epoch in range(epochs):
    model.train()
    
    # Forward pass
    y_pred = model(X_tensor)
    loss = criterion(y_pred, y_tensor)
    
    # Backward pass
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()
    
    if (epoch+1) % 50 == 0:
        print(f"Epoch {epoch+1}/{epochs}, Loss: {loss.item():.4f}")

# =========================
# 6. Evaluation / prediction
# =========================
model.eval()
with torch.no_grad():
    predictions = model(X_tensor)

print("First 5 predictions vs true values:")
for i in range(5):
    print(f"Pred: {predictions[i].item():.2f}\tTrue: {y_tensor[i].item():.2f}\tDiff: {(predictions[i].item() - y_tensor[i].item()):.2f}")

# =========================
# 6. SAVE MODEL
# =========================

torch.save(model.state_dict(), "PythonSrc/models/temp_model.pth")
