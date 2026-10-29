package org.firstinspires.ftc.teamcode.training.flowers;

import java.util.Objects;

/** Training-only flower subtotal for BIOBUZZ TU06. No penalties or match certification. */
public final class FlowerScoring {
    public enum Piece { POLLEN, RED_NECTAR, BLUE_NECTAR }
    public static final class Score {
        public final int red, blue;
        private Score(int red, int blue) { this.red = red; this.blue = blue; }
    }
    private FlowerScoring() {}
    /** All entries must already satisfy the scoring-volume criteria. Order: bottom to top. */
    public static Score score(Piece... bottomToTop) {
        Objects.requireNonNull(bottomToTop, "pieces");
        Piece bottom = null, owner = null;
        for (Piece p : bottomToTop) {
            Objects.requireNonNull(p, "piece");
            if (p != Piece.POLLEN) {
                if (bottom == null) bottom = p;
                owner = p;
            }
        }
        int red = owner == Piece.RED_NECTAR ? 2 * bottomToTop.length : 0;
        int blue = owner == Piece.BLUE_NECTAR ? 2 * bottomToTop.length : 0;
        if (bottom == Piece.RED_NECTAR) red += 5;
        if (bottom == Piece.BLUE_NECTAR) blue += 5;
        return new Score(red, blue);
    }
}
