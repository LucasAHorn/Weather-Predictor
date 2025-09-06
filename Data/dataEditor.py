import csv

input_file = "C:/gitfiles/weather-predictor/Data/largerData/rawMixedData_jan.csv"
output_file = "edited.csv"

# Read CSV into a list of rows
with open(input_file, newline='') as csvfile:
    reader = list(csv.reader(csvfile))

# Header + Data separation
header = reader[0]
data = reader[1:]

# Ensure we have enough rows to find "next hour"
if len(data) < 2:
    raise ValueError("Not enough data to add future temperature.")

# Add a new header column
header.append("future_temp")

# Loop through each row except the last one
for i in range(len(data) - 1):
    current_row = data[i]
    next_row = data[i + 1]
    future_temp = next_row[4]  # temp column is index 4
    current_row.append(future_temp)

# Last row has no "future" temp, so leave it blank
data[-1].append("")

# Write out to a new CSV
with open(output_file, "w", newline='') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(header)
    writer.writerows(data)

print(f"Updated file saved as {output_file}")
