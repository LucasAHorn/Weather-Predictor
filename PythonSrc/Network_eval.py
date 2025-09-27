import torch
import numpy as np


#0 - get data





# =========================
# 1. Live input features
# =========================
live_features = np.array([1.2, 3.4, 5.6, 7.8], dtype=np.float32)  # replace with your actual values
X_tensor = torch.tensor(live_features, dtype=torch.float32).unsqueeze(0)  # shape [1, n_features]

# =========================
# 2. Normalize using training stats
# =========================
# These must match the training values exactly
# X_mean = torch.tensor([X_mean_values_here], dtype=torch.float32)
# X_std  = torch.tensor([X_std_values_here], dtype=torch.float32)

y_mean = 20.0  # training target mean
y_std  = 5.0   # training target std

X_tensor_norm = (X_tensor - X_mean) / (X_std + 1e-8)

# =========================
# 3. Load the saved model
# =========================
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = torch.jit.load("PythonSrc/models/model_3.pt", map_location=device)
model.to(device)
model.eval()

X_tensor_norm = X_tensor_norm.to(device)

# =========================
# 4. Predict and denormalize
# =========================
with torch.no_grad():
    y_pred_norm = model(X_tensor_norm)
    y_pred = y_pred_norm * y_std + y_mean

print(f"Predicted value: {y_pred.item():.2f}")
