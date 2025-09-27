from meteostat import Point, Hourly
from datetime import datetime, timedelta

# Location (lat, lon)
location = Point(42.0347, -93.6199)  # Ames IA

# Current date and time
now = datetime.now()

# Meteostat expects an interval (start, end), so just give a 1-hour range
data = Hourly(location, now - timedelta(hours=1), now + timedelta(hours=4))
data = data.fetch()

# Print relevant columns
print(data[['temp','dwpt','rhum','prcp','wdir','wspd','pres']])