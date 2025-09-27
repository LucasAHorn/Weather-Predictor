# evaluate_single_input.py

import torch
import numpy as np

# =========================
# 1. Define the input features
# =========================
# Replace this array with your live data features
live_features = np.array([1,2,3,4,5,6,7,8,9,10], dtype=np.float32)  # example

# Convert to tensor and add batch dimension
X_tensor = torch.tensor(live_features, dtype=torch.float32).unsqueeze(0)  # shape: [1, n_features]

# =========================
# 2. Normalize features
# =========================
# Use same mean/std as during training
# Replace these with your training normalization values
X_mean = torch.tensor([0.5, 2.0, 4.0, 6.0], dtype=torch.float32)  # example
X_std  = torch.tensor([1.0, 1.5, 2.0, 2.5], dtype=torch.float32)  # example

X_tensor_norm = (X_tensor - X_mean) / (X_std + 1e-8)

# =========================
# 3. Load the saved model
# =========================
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model_path = "PythonSrc/models/model_5_.pt"  # update with your model path
model = torch.jit.load(model_path, map_location=device)
model.to(device)
model.eval()

X_tensor_norm = X_tensor_norm.to(device)

# =========================
# 4. Predict
# =========================
with torch.no_grad():
    y_pred_norm = model(X_tensor_norm)

# =========================
# 5. Denormalize output
# =========================
# Use same mean/std as during training
y_mean = 20.0  # replace with training target mean
y_std  = 5.0   # replace with training target std

y_pred = y_pred_norm * y_std + y_mean

print(f"Predicted value: {y_pred.item():.2f}")
