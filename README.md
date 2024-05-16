# Restaurant Data Scraper

This project includes a set of Java classes designed to scrape, process, and store restaurant data from grab's search based on location. The architecture is built to handle scraping tasks concurrently, utilize geocoding to enrich data, and efficiently manage data serialization and storage.


## Features

- **Concurrent Data Scraping**: Utilizes multi-threading to scrape data from multiple locations concurrently. Used Completable Futures here
- **Geocoding Utility**: Converts physical addresses into geographic coordinates (latitude and longitude). *(Currently not in use due to accuracy concerns)*
- **Data Serialization**: Serializes restaurant data into NDJSON format and compresses it using GZIP for efficient storage.

## Components

- `MultiLocationScrapingService`: Manages scraping tasks across multiple threads.
- `RestaurantScrapingService`: Handles the scraping logic for individual tasks.
- `GeoCodingUtil`: Provides geocoding services to fetch geographic coordinates from addresses.
- `FileUtil`: Handles the serialization of data into NDJSON format and compresses it into GZIP files.
- `Restaurant`: Represents the data structure for storing restaurant information.

## Getting Started

### Prerequisites

- Java 14 or higher (as we are using records)
- Maven for dependency management

### Installation

1. Clone the repository:
   git clone https://github.com/AtharvT/web-scraper.git
2. Open the project on Intellij (JAVA 14+)
3. Build using maven
4. Run the main folder.
5. A file named "restaurants.ndjson.gz" will automatically be generated and contain the restaurant and its details extract.
6. Meta data would be printed in the console.

### Some Key Points
1. Run this when the majority of restaurants are online as the filter to check those restaurants that are inactive needs to be added.
2. Be responsible with API and not increase the page size by a lot.
