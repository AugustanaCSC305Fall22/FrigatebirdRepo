package frigatebird.ui;

import javafx.scene.shape.Polygon;

public class HexTile extends Polygon {
	
	private double r;
    private double n;
    private double tileHeight;
    private double tileWidth;
	
    HexTile(double x, double y, double r) {
    	r = 20; // the inner radius from hexagon center to outer corner
        n = Math.sqrt(r * r * 0.75); // the inner radius from hexagon center to middle of the axis
        tileHeight = 2 * r;
        tileWidth = 2 * n;
        // creates the polygon using the corner coordinates
        getPoints().addAll(
                x, y,
                x, y + r,
                x + n, y + r * 1.5,
                x + tileWidth, y + r,
                x + tileWidth, y,
                x + n, y - r * 0.5
        );
        /*
        // set up the visuals and a click listener for the tile
        setFill(Color.BLACK);
        setStrokeWidth(1);
        setStroke(Color.LIGHTBLUE);
        setOnMouseClicked(e -> System.out.println("Clicked: " + this));
        */
    }
}