package org.webscraper.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Utility class for file operations, focusing on saving data to files in specific formats.
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Saves a set of objects to a GZIP compressed NDJSON file.
     *
     * @param data The set of data objects to be saved.
     * @param filePath The path to the file where the data should be saved.
     */
    public static void saveDataAsGzipNdjson(Set<?> data, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(bos)) {
            for (Object item : data) {
                String json = mapper.writeValueAsString(item) + "\n";
                gcos.write(json.getBytes());
            }
            logger.info("Data successfully written to {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving data to file {}: {}", filePath, e.getMessage(), e);
        }
    }
}
