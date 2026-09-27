package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class HighScoreManager {
    private static final String FILE = "./thing.txt";
    private static final int MAX_SCORES = 5;

    public static ArrayList<Integer> loadScores() {
        ArrayList<Integer> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(Integer.parseInt(line.trim()));
            }
        } catch (Exception ignored) {}
        return scores;
    }

    public static void updateHighScore(int score) {
        ArrayList<Integer> scores = loadScores();
        scores.add(score);
        scores.sort(Collections.reverseOrder());
        if (scores.size() > MAX_SCORES)
            scores = new ArrayList<>(scores.subList(0, MAX_SCORES));
        saveScores(scores);
    }

    public static void saveScores(ArrayList<Integer> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            for (Integer score : scores) {
                writer.write(score.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
