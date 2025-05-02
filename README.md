# MovieSearch

A service for searching and browsing movies by city, theaters, genre, language, and location. This application leverages DynamoDB for data storage with geospatial querying capabilities.

## Technology Stack

- **Java 17**
- **Spring Boot**
- **AWS DynamoDB** - NoSQL database with geospatial support
- **Maven** - Dependency management and build tool
- **Spring DevTools** - Development productivity tools

## Features

- Search movies by city
- Search movies by theater within a city
- Search movies by genre
- Find nearby theaters and movies based on geolocation
- Create and manage movie schedules with detailed information

## Setup and Installation

### Prerequisites

- Java 17+
- Maven
- AWS account with DynamoDB access or local DynamoDB setup
- AWS CLI configured with appropriate credentials

### Configuration

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd MovieSearch
   ```

2. Configure AWS credentials in `application.properties` or via environment variables:
   ```
   aws.accessKey=your-access-key
   aws.secretKey=your-secret-key
   aws.region=your-region
   ```

3. Create DynamoDB table:
   ```
   MovieScheduleGeoRecord
   ```
   With indexes:
    - CityIndex (GSI on city field)
    - TheatreIndex (GSI on theatreName field)
    - MovieNameIndex (GSI on movieName field)
    - MovieLanguageIndex (GSI on language field)
    - MovieFormatIndex (GSI on format field)

### Building and Running

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Run the application:
   ```bash
   java -jar target/MovieSearch-0.0.1-SNAPSHOT.jar
   ```

   Or using Maven:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=false"
   ```

## API Documentation

### Movie Search APIs

#### Search Movies by City
```
GET /api/movies?city=New%20York
```

#### Search Movies by Theater
```
GET /api/movies/theatres?city=New%20York&theatreName=AMC%20Empire%2025
```

#### Search Nearby Movies
```
GET /api/movies/nearby?city=New%20York&latitude=40.7128&longitude=-74.0060&radius=5
```

#### Search Movies by Genre
```
GET /api/movies/genres?city=New%20York&genre=Action
```

### Create Movie Schedule

```
POST /api/movies/schedules
Content-Type: application/json

{
  "scheduleId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "city": "New York",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "theatreName": "AMC Empire 25",
  "details": {
    "theatre": {
      "id": "321bf86a-7e8f-49f0-a2cf-a471e3c52cf2",
      "location": "234 W 42nd St, New York, NY 10036"
    },
    "movie": {
      "id": "b294a3a5-6212-4744-ac63-e07926f2fc56",
      "name": "Inception",
      "duration": 148,
      "description": "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
      "imageUrl": "https://example.com/inception-poster.jpg",
      "trailerUrl": "https://example.com/inception-poster.jpg",
      "language": "English",
      "releaseDate": "2010-07-16",
      "format": "Standard 2D", 
      "genres": [
        {
          "name": "Action",
          "description": "Action film genre"
        },
        {
          "name": "Sci-Fi",
          "description": "Science fiction genre"
        }
      ],
      "cast": [
        {
          "actorName": "Leonardo DiCaprio",
          "characterName": "Dom Cobb",
          "role": "Protagonist",
          "profileImageUrl": "https://example.com/dicaprio.jpg",
          "description": "Known for Titanic"
        },
        {
          "actorName": "Joseph Gordon-Levitt",
          "characterName": "Arthur",
          "role": "Antogonist",
          "profileImageUrl": "https://example.com/gordon-levitt.jpg",
          "description": "Known for xyz"
        }
      ]
    },
    "showDate": "2023-10-12",
    "screens": [
      {
        "id": "d6ddef79-8b8f-4774-9f07-6256b18a0037",
        "name": "Screen 3",
        "description": "Wide IMAX Digital Dolby",
        "capacity": 150,
        "seatZonePricings": [
          {
            "zoneName": "VIP",
            "capacity": 130,
            "availableSeats": 30,
            "price": 14.99,
            "currency": "USD",
            "startTime": "18:30",
            "endTime": "21:00"
          },
          {
            "zoneName": "VVIP",
            "capacity": 20,
            "availableSeats": 2,
            "price": 18.99,
            "currency": "USD",
            "startTime": "18:30",
            "endTime": "21:00"
          }
        ]
      }
    ]
  }
}
```
### Build
Trigger Build 2
