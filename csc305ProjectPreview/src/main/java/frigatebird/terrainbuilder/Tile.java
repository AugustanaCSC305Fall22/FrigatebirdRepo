package frigatebird.terrainbuilder;

public class Tile {

	// consider row/column info too?
	private int height;
	private int row;
	private int col;

	public Tile(int height, int row, int col) {
		this.height = height;
		this.row = row;
		this.col = col;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public String toString() {
		return "Tile [height=" + height + ", row=" + row + ", col=" + col + "]";
	}	
}
