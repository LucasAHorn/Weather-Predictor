# fine_tune_network.py

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

# Keep original target for error calculation
y_tensor_orig = torch.tensor(y, dtype=torch.float32)

# Convert features to tensor
X_tensor = torch.tensor(X, dtype=torch.float32)

# =========================
# 3. Normalize features (same as during training)
# =========================
X_mean, X_std = X_tensor.mean(0), X_tensor.std(0)
X_tensor_norm = (X_tensor - X_mean) / (X_std + 1e-8)

y_mean, y_std = y_tensor_orig.mean(), y_tensor_orig.std()
y_tensor_norm = (y_tensor_orig - y_mean) / (y_std + 1e-8)

# =========================
# 4. Create DataLoader for mini-batches
# =========================
dataset = TensorDataset(X_tensor_norm, y_tensor_norm)
batch_size = 128
train_loader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

# =========================
# 5. Load existing model
# =========================
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Using device: {device}")

# Load TorchScript model (recommended) or PyTorch model
model = torch.jit.load("PythonSrc/models/model_1.pt", map_location=device)
model.to(device)
model.train()  # set to training mode for fine-tuning

# =========================
# 6. Loss, optimizer, and scheduler
# =========================
criterion = nn.MSELoss()
optimizer = optim.Adam(model.parameters(), lr=0.0003, weight_decay=1e-5)  # smaller LR for fine-tuning
scheduler = optim.lr_scheduler.ReduceLROnPlateau(
    optimizer,
    mode='min', 
    factor=0.5,
    patience=50,
    threshold=1e-4,   
    threshold_mode='rel',
    cooldown=10, 
    min_lr=1e-8,
    eps=1e-8
)

# =========================
# 7. Fine-tuning loop
# =========================
fine_tune_epochs = 5000  # adjust based on desired extra training
for epoch in range(fine_tune_epochs):
    epoch_loss = 0.0

    for X_batch, y_batch in train_loader:
        X_batch, y_batch = X_batch.to(device), y_batch.to(device)

        optimizer.zero_grad()
        y_pred = model(X_batch)
        loss = criterion(y_pred, y_batch)
        loss.backward()
        optimizer.step()

        epoch_loss += loss.item() * X_batch.size(0)

    epoch_loss /= len(dataset)

    # Evaluate MAE over dataset
    model.eval()
    with torch.no_grad():
        y_pred_all = model(X_tensor_norm.to(device))
        mae = torch.mean(torch.abs(y_pred_all - y_tensor_norm.to(device))).item()
        rmse = torch.sqrt(torch.mean((y_pred_all - y_tensor_norm.to(device))**2)).item()
    model.train()

    # Adjust learning rate based on validation loss (here we use epoch_loss as proxy)
    scheduler.step(epoch_loss)

    if (epoch + 1) % (fine_tune_epochs // 20) == 0:
        print(f"{int(100*(epoch+1)/fine_tune_epochs)}%, Loss: {epoch_loss:.6f}, MAE: {mae:.4f}, RMSE: {rmse:.4f}, LR: {optimizer.param_groups[0]['lr']:.9f}")

# =========================
# 8. Save fine-tuned model
# =========================
model.eval()
scripted_model = torch.jit.script(model)
scripted_model.save("PythonSrc/models/retrained/model_3.pt")
print("Fine-tuned model saved successfully!")