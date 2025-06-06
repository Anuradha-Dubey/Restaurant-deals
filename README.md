# Restaurant Deals API

A simple **restaurant deals API** built with Spring Boot WebFlux that lets you retrieve active deals for restaurants, discover the peak deal time window, and handle errors gracefully.

---

## Features

- **Fetch active deals:** Retrieve deals available for all restaurants at a specific time.
- **Find peak deal window:** Discover the time period when the most deals overlap.
- **Global Exception Handling:** Consistent error responses for invalid input, service issues, etc.
- **Reactive and modern:** Uses Project Reactor and Spring WebFlux.
- **Thorough unit testing:** High code coverage with JUnit and Mockito.
- **Simple configuration:** Runs locally with no DB needed (reads from JSON endpoint).

---

## Requirements

- Java 17+
- Maven 3.8+
- Spring Boot 3.x
- Spring WebFlux
- JUnit 5, Mockito (for tests)
- Lombok, MapStruct

---

## Running Locally

1. **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/restaurant-deals.git
    cd restaurant-deals
    ```

2. **Build the project:**
    ```bash
    mvn clean install
    ```

3. **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
   By default, the application runs on port **8080**.

---

## API Endpoints

### 1. Fetch Active Deals

**Request:**  
`GET /api/deals?timeOfDay=5:00pm`

- **Query Param:** `timeOfDay` (required, format: `3:00pm`, `7:30pm`, etc.)

**Example:**

1. GET http://localhost:8080/api/deals?timeOfDay=5:00pm


**Sample Response:**
```json
{
  "deals": [
    {
      "restaurantObjectId": "abc123",
      "restaurantName": "Sushi House",
      "dealObjectId": "xyz789",
      "discount": "20%",
      "dineIn": true,
      "lightning": false,
      "qtyLeft": 4
    }
  ]
}


2. Find Peak Deal Time Window
Request:
GET /api/deals/peak-time

Example:

GET http://localhost:8080/api/deals/peak-time

Sample Response:

{
  "peakTimeStart": "5:00pm",
  "peakTimeEnd": "7:00pm"
}

Error Handling:

| HTTP Status               | Scenario                                |
| ------------------------- | --------------------------------------- |
| 400 BAD REQUEST           | Invalid time format (`timeOfDay` query) |
| 503 SERVICE UNAVAILABLE   | Restaurant data source not available    |
| 500 INTERNAL SERVER ERROR | Unexpected errors                       |
| 204 NO CONTENT            | No overlapping peak time window found   |

Error responses:

{
  "error": "Descriptive error message"
}

Testing
Run all tests:

mvn test

Controller tests: Use WebTestClient for endpoint coverage.

Service tests: Mock WebClient and cover both success and failure cases.

Code Coverage (Jacoco):

mvn jacoco:report
# Open target/site/jacoco/index.html in your browser

Project Structure

src/main/java/com/customer/restaurantdeals
  ├── configuration    # WebClient & deserializer config
  ├── controller       # REST controllers
  ├── dto              # Response DTOs
  ├── exception        # Custom exceptions & global handler
  ├── mapper           # MapStruct mappers
  ├── model            # Restaurant, Deal, etc.
  ├── service          # Business logic
  ├── util             # Constants, helpers
src/test/java/com/customer/restaurantdeals
  ├── controller       # Controller tests
  ├── service          # Service tests

Notes
Time format for timeOfDay: Use format like 5:00pm (case-insensitive, no space).

Upstream Data: The service fetches restaurant data from a static JSON endpoint.
You can change the source in RestaurantDealsConstant.

No authentication required.


