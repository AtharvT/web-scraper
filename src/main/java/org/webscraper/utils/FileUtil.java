package org.webscraper.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void saveDataAsGzipNdjson(Set<?> data, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try (FileOutputStream fos = new FileOutputStream(filePath);
             GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(fos)) {
            for (Object item : data) {
                String json = mapper.writeValueAsString(item);
                gcos.write((json + "\n").getBytes());
            }
            logger.info("Data successfully written to {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving data to file {}: {}", filePath, e.getMessage(), e);
        }
    }
}
