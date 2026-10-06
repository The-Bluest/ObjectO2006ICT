package org.example;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileStoreTest {

    @TempDir
    Path tempDir;

    private final JsonFileStore<String> store = new JsonFileStore<>(new Gson(), String.class);

    @Test
    void loadReturnsFallbackWhenFileIsMissing() {
        Path file = tempDir.resolve("missing.json");

        assertEquals("fallback", store.load(file, "fallback"));
    }

    @Test
    void saveThenLoadRoundTripsTheValue() {
        Path file = tempDir.resolve("value.json");

        store.save(file, "hello world");

        assertEquals("hello world", store.load(file, null));
    }

    @Test
    void saveCreatesMissingParentDirectories() {
        Path file = tempDir.resolve("nested/dir/value.json");

        store.save(file, "nested value");

        assertTrue(Files.exists(file));
        assertEquals("nested value", store.load(file, null));
    }

    @Test
    void saveOverwritesAnExistingFile() {
        Path file = tempDir.resolve("value.json");

        store.save(file, "first");
        store.save(file, "second");

        assertEquals("second", store.load(file, null));
    }

    @Test
    void loadReturnsFallbackWhenJsonIsMalformed() throws Exception {
        Path file = tempDir.resolve("broken.json");
        Files.writeString(file, "{ not valid json ]");

        assertEquals("fallback", store.load(file, "fallback"));
    }
}
