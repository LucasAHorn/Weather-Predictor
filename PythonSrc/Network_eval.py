import torch
import csv
import numpy as np
import torch.nn as nn
import matplotlib.pyplot as plt

# =========================
# 1. Load CSV data
# =========================
data_list = []
with open("Data/largeData/revised.csv", newline='') as csv_file:
    csv_reader = csv.reader(csv_file)
    header = next(csv_reader)
    for row in csv_reader:
        cleaned_row = [float(x) if x != '' else 0.0 for x in row]
        data_list.append(cleaned_row)

data_array = np.array(data_list, dtype=np.float32)

# =========================
# 2. Split features and target
# =========================
X = data_array[:, :-1]
y = data_array[:, -1:]

# Tensors
X_tensor = torch.tensor(X, dtype=torch.float32)
y_tensor = torch.tensor(y, dtype=torch.float32)

# Keep a copy for original target values
y_tensor_orig = y_tensor.clone()

# =========================
# 3. Normalize evaluation set
# =========================
X_mean, X_std = X_tensor.mean(0), X_tensor.std(0)
X_tensor_norm = (X_tensor - X_mean) / (X_std + 1e-8)

y_mean, y_std = y_tensor.mean(), y_tensor.std()
y_tensor_norm = (y_tensor - y_mean) / (y_std + 1e-8)

# =========================
# 4. Load saved TorchScript model
# =========================
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Using device: {device}")

model = torch.jit.load("PythonSrc/models/best_model_5_.pt", map_location=device)
model.to(device)
model.eval()

X_tensor_norm = X_tensor_norm.to(device)
y_tensor_orig = y_tensor_orig.to(device)

# =========================
# 5. Evaluate function
# =========================
def evaluate_model(model, X_norm, y_orig, y_mean, y_std, batch_size=1024):
    model.eval()
    pred_list = []
    with torch.no_grad():
        for i in range(0, len(X_norm), batch_size):
            X_batch = X_norm[i:i+batch_size]
            pred_norm = model(X_batch)
            pred_list.append(pred_norm.cpu())
    pred_norm_all = torch.cat(pred_list)
    # Denormalize using evaluation stats
    pred_orig_all = pred_norm_all * y_std + y_mean

    rmse = torch.sqrt(torch.mean((pred_orig_all - y_orig.cpu())**2))
    mae = torch.mean(torch.abs(pred_orig_all - y_orig.cpu()))
    return rmse.item(), mae.item(), pred_orig_all

rmse, mae, predictions_orig = evaluate_model(model, X_tensor_norm, y_tensor_orig, y_mean, y_std)

print(f"Average error (RMSE): {rmse:.4f}  - sensitive to outliers")
print(f"Mean Absolute Error (MAE): {mae:.4f}  - average error")

# =========================
# 6. Inspect first 5 predictions
# =========================

predictions_orig = predictions_orig.to(y_tensor_orig.device)
print("\nFirst 5 predictions vs true values:")
for i in range(5):
    print(f"Pred: {predictions_orig[i].item():.2f}\tTrue: {y_tensor_orig[i].item():.2f}\tDiff: {(predictions_orig[i] - y_tensor_orig[i]).item():.2f}")

# =========================
# 7. Visualization
# =========================
y_true = y_tensor_orig.cpu().numpy().flatten()
y_pred = predictions_orig.cpu().numpy().flatten()

# Scatter plot with semi-transparent points
plt.figure(figsize=(6,6))
plt.scatter(y_true, y_pred, alpha=0.2, color='teal')
plt.plot([y_true.min(), y_true.max()],
         [y_true.min(), y_true.max()],
         'r--', lw=2, label='Perfect Prediction')

# Best-fit line
slope, intercept = np.polyfit(y_true, y_pred, 1)
plt.plot(y_true, slope*y_true + intercept, 'g-', lw=2, label=f'Fit y={slope:.2f}x+{intercept:.2f}')

plt.xlabel("True Values")
plt.ylabel("Predicted Values")
plt.title("Predicted vs True Values")
plt.legend()
plt.grid(True)
plt.show()

# Prediction error histogram
errors = y_pred - y_true
plt.figure(figsize=(6,4))
plt.hist(errors, bins=50, color='coral', alpha=0.7)
plt.xlabel("Prediction Error (Predicted - True)")
plt.ylabel("Frequency")
plt.title("Prediction Error Distribution")
plt.grid(True)
plt.show()
