package com.ironman.partyreference.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonFileReader {

  private static final ObjectMapper objectMapper =
      new ObjectMapper().registerModule(new JavaTimeModule());

  public static <T> T read(String path, TypeReference<T> typeReference) {
    var classLoader = JsonFileReader.class.getClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream(path)) {

      if (inputStream == null) {
        throw new IllegalArgumentException("File not found: " + path);
      }

      return objectMapper.readValue(inputStream, typeReference);
    } catch (IOException e) {
      throw new UncheckedIOException("Error reading JSON file: " + path, e);
    }
  }
}
