
import javafx.scene.shape.Rectangle;
//probably redundant, just merged it into tetris.java
public class movej{
    public static final int move = tetris.move;
    public static final int size = tetris.size;
    public static int xMax = tetris.xMax;
    public static int yMax = tetris.yMax;
    public static int[][] mesh = tetris.mesh;
    	private void Down(Rectangle rect) {
		if (rect.getY() + move < yMax)
			rect.setY(rect.getY() + move);

	}

	public void Right(Rectangle rect) {
		if (rect.getX() + move <= xMax - size)
			rect.setX(rect.getX() + move);
	}

	public void Left(Rectangle rect) {
		if (rect.getX() - move >= 0)
			rect.setX(rect.getX() - move);
	}
}