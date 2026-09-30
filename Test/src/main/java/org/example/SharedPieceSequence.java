package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SharedPieceSequence {
    private final List<String> sequence = new ArrayList<>();
    private final Random random = new Random();
    public String getPiece(int index) {
        while (sequence.size() <= index) {
            sequence.add(generatePiece());
        }
        return sequence.get(index);
    }

    private String generatePiece() {
        int block = random.nextInt(100);
        if (block < 15) {
            return "l";
        } else if (block < 30) {
            return "ll";
        } else if (block < 45) {
            return "square";
        } else if (block < 60) {
            return "s";
        } else if (block < 70) {
            return "zig";
        } else if (block < 85) {
            return "t";
        } else {
            return "line";
        }
    }
}