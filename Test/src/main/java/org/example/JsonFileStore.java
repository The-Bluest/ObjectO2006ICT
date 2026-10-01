package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Generic atomic JSON read/write for a single value of type T.
 * Extracted from Settings/HighScoreManager, which had identical temp-file-then-rename logic.
 */
public class JsonFileStore<T> {
    private final Gson gson;
    private final Type type;

    public JsonFileStore(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    public T load(Path file, T fallback) {
        if (!Files.exists(file)) {
            return fallback;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            T loaded = gson.fromJson(reader, type);
            return loaded == null ? fallback : loaded;
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            System.err.println("Could not load " + file + ": " + exception.getMessage());
            return fallback;
        }
    }

    public void save(Path file, T value) {
        Path absoluteFile = file.toAbsolutePath();
        Path parent = absoluteFile.getParent();
        Path temporaryFile = absoluteFile.resolveSibling(
                absoluteFile.getFileName() + ".tmp");

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(temporaryFile)) {
                gson.toJson(value, type, writer);
            }

            moveIntoPlace(temporaryFile, absoluteFile);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw new IllegalStateException(
                    "Could not save to " + file,
                    exception
            );
        }
    }

    private static void moveIntoPlace(Path temporaryFile, Path targetFile)
            throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    temporaryFile,
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}
