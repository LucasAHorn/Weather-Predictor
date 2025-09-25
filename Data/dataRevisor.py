import pandas as pd

# Input and output file paths
input_file = "Data\largeData\edited.csv"
output_file = "Data\largeData\Revised.csv"

# Read CSV
df = pd.read_csv(input_file)

# Columns to drop
cols_to_drop = [ "snow", "coco", "tsun", "future", "wpgt" ]

# Drop them (ignore if missing just in case)
df = df.drop(columns=cols_to_drop, errors="ignore")

# Save cleaned CSV
df.to_csv(output_file, index=False)

print(f"Cleaned CSV written to {output_file}")
