import torch
import csv
import numpy as np
import torch.nn as nn

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
# These values should match the training normalization
X_mean, X_std = X_tensor.mean(0), X_tensor.std(0)
X_tensor_norm = (X_tensor - X_mean) / (X_std + 1e-8)

# If target was normalized during training
y_mean, y_std = y_tensor_orig.mean(), y_tensor_orig.std()
y_tensor_norm = (y_tensor_orig - y_mean) / (y_std + 1e-8)

# =========================
# 4. Load the saved model
# =========================
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Using device: {device}")

# TorchScript model
model = torch.jit.load("PythonSrc/models/model_2.pt", map_location=device)
model.to(device)
model.eval()

X_tensor_norm = X_tensor_norm.to(device)
y_tensor_orig = y_tensor_orig.to(device)  # original scale for final errors

# =========================
# 5. Evaluate function with denormalization
# =========================
def evaluate_model(model, X_norm, y_orig, y_mean, y_std):
    model.eval()
    with torch.no_grad():
        # Model prediction (normalized)
        pred_norm = model(X_norm)
        # Denormalize
        pred_orig = pred_norm * y_std + y_mean

        # Errors in original units
        rmse = torch.sqrt(torch.mean((pred_orig - y_orig) ** 2))
        mae = torch.mean(torch.abs(pred_orig - y_orig))
    return rmse.item(), mae.item(), pred_orig

# =========================
# 6. Calculate errors
# =========================
rmse, mae, predictions_orig = evaluate_model(model, X_tensor_norm, y_tensor_orig, y_mean, y_std)

print(f"Average error (RMSE): {rmse:.4f}  - sensitive to outliers")
print(f"Mean Absolute Error (MAE): {mae:.4f}  - average error")

# =========================
# 7. Inspect first 5 predictions
# =========================
print("\nFirst 5 predictions vs true values:")
for i in range(5):
    print(f"Pred: {predictions_orig[i].item():.2f}\tTrue: {y_tensor_orig[i].item():.2f}\tDiff: {(predictions_orig[i] - y_tensor_orig[i]).item():.2f}")

# =========================
# 8. Visualize predictions (add this here)
# =========================
import matplotlib.pyplot as plt

# Convert tensors to numpy
y_true = y_tensor_orig.cpu().numpy().flatten()
y_pred = predictions_orig.cpu().numpy().flatten()

# Scatter plot: Predicted vs True
plt.figure(figsize=(6,6))
plt.scatter(y_true, y_pred, alpha=0.1, color='teal')
plt.plot([y_true.min(), y_true.max()],
         [y_true.min(), y_true.max()],
         'r--', lw=2)  # perfect prediction line
plt.xlabel("True Values")
plt.ylabel("Predicted Values")
plt.title("Predicted vs True Values")
plt.grid(True)
plt.show()

# Histogram: Prediction error
errors = y_pred - y_true
plt.figure(figsize=(6,4))
plt.hist(errors, bins=50, color='coral', alpha=0.7)
plt.xlabel("Prediction Error (Predicted - True)")
plt.ylabel("Frequency")
plt.title("Prediction Error Distribution")
plt.grid(True)
plt.show()