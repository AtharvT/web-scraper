package org.webscraper.model;

import java.util.Set;

import java.util.Set;

/**
 * Record that encapsulates a collection of Restaurant objects and associated MetaData.
 * It serves as a container for data scraped from a source, organizing both the results and their metadata.
 */
public record ScrapedData(Set<Restaurant> restaurantSet, MetaData metaData) {
    /**
     * Creates a new instance of ScrapedData.
     *
     * @param restaurantSet A set of Restaurant objects, representing the scraped data.
     * @param metaData MetaData associated with the scraping process, capturing details like counts of null and non-null values.
     */
}

