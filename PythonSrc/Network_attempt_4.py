import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import TensorDataset, DataLoader
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
        cleaned_row = [float(x) if x != '' else 0.0 for x in row]
        data_list.append(cleaned_row)

data_array = np.array(data_list, dtype=np.float32)

# =========================
# 2. Split features and target
# =========================
X = data_array[:, :-1]
y = data_array[:, -1:]

# Convert to PyTorch tensors
X_tensor = torch.tensor(X, dtype=torch.float32)
y_tensor = torch.tensor(y, dtype=torch.float32)

# =========================
# 3. Normalize inputs and target
# =========================
X_mean, X_std = X_tensor.mean(0), X_tensor.std(0)
X_tensor = (X_tensor - X_mean) / (X_std + 1e-8)

y_mean, y_std = y_tensor.mean(), y_tensor.std()
y_tensor = (y_tensor - y_mean) / (y_std + 1e-8)

# =========================
# 4. Create Dataset and DataLoader (mini-batches)
# =========================
dataset = TensorDataset(X_tensor, y_tensor)
batch_size = 128
train_loader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

# =========================
# 5. Define neural network
# =========================
class TempPredictor(nn.Module):
    def __init__(self, input_size):
        super().__init__()
        self.model = nn.Sequential(
            nn.Linear(input_size, 32),
            nn.ReLU(),
            nn.Linear(32, 16),
            nn.ReLU(),
            nn.Dropout(0.1),
            nn.Linear(16, 10),
            nn.ReLU(),
            nn.Linear(10, 1)
        )
    def forward(self, x):
        return self.model(x)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = TempPredictor(input_size=X_tensor.shape[1]).to(device)

# =========================
# 6. Loss and optimizer
# =========================
criterion = nn.MSELoss()
optimizer = optim.Adam(model.parameters(), lr=0.0003, weight_decay=1e-5)

# StepLR scheduler: decay by 0.5 every 50k epochs
scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=50000, gamma=0.5)

# =========================
# 7. Training loop
# =========================
epochs = 200_000  # fewer epochs for mini-batch stability
for epoch in range(epochs):
    model.train()
    epoch_loss = 0.0
    for X_batch, y_batch in train_loader:
        X_batch, y_batch = X_batch.to(device), y_batch.to(device)

        optimizer.zero_grad()
        y_pred = model(X_batch)
        loss = criterion(y_pred, y_batch)
        loss.backward()
        optimizer.step()

        epoch_loss += loss.item() * X_batch.size(0)

    # Step the scheduler once per epoch
    scheduler.step()

    # Average loss for the epoch
    epoch_loss /= len(dataset)

    if (epoch + 1) % (epochs // 20) == 0:
        # Calculate MAE over dataset
        model.eval()
        with torch.no_grad():
            y_pred_all = model(X_tensor.to(device))
            mae = torch.mean(torch.abs(y_pred_all - y_tensor.to(device))).item()
        model.train()

        print(f"{int(100*(epoch+1)/epochs)}%, Loss: {epoch_loss:.4f}, MAE: {mae:.4f}, LR: {scheduler.get_last_lr()[0]:.6f}")

# =========================
# 8. Save model (TorchScript)
# =========================
model.eval()
scripted_model = torch.jit.script(model)
scripted_model.save("PythonSrc/models/model_3.pt")
