package org.example;


import java.util.ArrayList;
import java.util.stream.IntStream;

public class boardeval {
    // stateless evaluator: one shared instance is enough for the whole app
    private static final boardeval INSTANCE = new boardeval();

    private boardeval() {
    }

    public static boardeval getInstance() {
        return INSTANCE;
    }

    public int columnHeight(int[][] board, int col){ //board is column-major: board[col][row], row 0 is the top
        int rows = board[0].length;
        for (int row = 0; row<rows; row++){
            if(board[col][row]!=0){return rows-row;}
        }
        return 0;
    }

    public int maximumHeight(int[][] board){
        return IntStream.range(0, board.length)
                .map(col -> columnHeight(board, col))
                .max()
                .orElse(0);
    }

    public int holes(int[][] board){ //counts empty cells that have a filled cell somewhere above them
        int count = 0;
        for(int col = 0; col<board.length;col++){
            boolean thingAbove=false;
            for(int row=0; row<board[0].length; row++ ){
                if(board[col][row]!=0){
                    thingAbove=true;
                } else if (thingAbove&&board[col][row]==0) {
                    count++;
                }
            }
        }
        return count;
    }

    public int bumpiness(int[][] board){
        return IntStream.range(0, board.length - 1)
                .map(col -> Math.abs(columnHeight(board, col) - columnHeight(board, col + 1)))
                .sum();
    }


    public int evaluate(int[][] board){
        //lines clear function here
        int linesClear=0;
        int full = 0;
        for (int i = 0; i < board[0].length; i++) {
            for (int[] ints : board) {
                if (ints[i] == 1)
                    full++;
            }
            if (full == board.length)
                linesClear++;
            full = 0;
        }


        return (6*linesClear)+(-bumpiness(board)*4)+(-holes(board)*3)+(-maximumHeight(board)*2);
    }
}

