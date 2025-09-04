import pandas as pd

# Input and output file paths
input_file = "Data/weather_with_future.csv"
output_file = "Data/weather_cleaned.csv"

# Read CSV
df = pd.read_csv(input_file)

# Columns to drop
cols_to_drop = ["year", "month", "day", "snow", "tsun"]

# Drop them (ignore if missing just in case)
df = df.drop(columns=cols_to_drop, errors="ignore")

# Save cleaned CSV
df.to_csv(output_file, index=False)

print(f"Cleaned CSV written to {output_file}")
