package frigatebird.terrainbuilder;

public class Tile {

	// consider row/column info too?
	private int height;
	
	public Tile() {
		this.height = 0;
	}
	
	public Tile(int height) {
		this.height = height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return this.height;
	}

	@Override
	public String toString() {
		return "Tile [height=" + height + "]";
	}	
}
