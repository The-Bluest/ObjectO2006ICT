package org.example;

// Stub: always answers with the same canned piece name, no matter the index, replacing real randomness.
class StubSharedPieceSequence extends SharedPieceSequence {
    private final String pieceName;

    StubSharedPieceSequence(String pieceName) {
        this.pieceName = pieceName;
    }

    @Override
    public String getPiece(int index) {
        return pieceName;
    }
}
