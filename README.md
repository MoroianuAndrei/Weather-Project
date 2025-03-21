# 🌤️ Weather-Project

## 📌 Overview  
SimpleWeatherApplication is a **Java-based client-server application** that interacts with a **PostgreSQL database** to provide weather-related data. The system supports two types of users: **Admin** and **Regular Client**.  

## 🛠️ Features  

### **🔹 Admin Functionalities**  
The **Admin** has full control over the database, allowing them to:  
- **Manage city records**: Add or delete locations.  
- **Manage weather conditions**:  
  - Store **weather data** for different cities, including:  
    - **Date**  
    - **Temperature**  
    - **Weather state** (e.g., rainy, sunny, windy, etc.)  
  - **Delete weather records** for a specific location.  
  - **View the entire database**, including all recorded cities and weather conditions.  

### **🔹 Regular Client Functionalities**  
A **regular client** can:  
- **Retrieve weather forecasts** for **the next three days** based on their location.  
- **Receive weather information** for the nearest recorded city based on the provided latitude and longitude.  

### **🔹 Database Connection Management**  
- The application establishes a connection to the **PostgreSQL database** at runtime.  
- **The connection is closed when exiting the application** to prevent resource leaks.  

## 🔧 Technologies Used  
- **Java** for the client-server architecture.  
- **PostgreSQL** for database storage and management.  
- **JPA (Jakarta Persistence API)** for database operations.  
- **Gson** for data serialization and deserialization.  

## 🚀 Future Improvements  
- Implement a graphical user interface (GUI) for easier interaction.  
- Include historical weather data analysis.  
