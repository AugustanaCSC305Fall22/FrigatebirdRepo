package frigatebird.terrainbuilder;

/**
 * A Tile object that stores its location in a TerrainMap object, it's current height, and whether it is pointy
 */
public class Tile {

	private int height;
	private int row;
	private int col;
	private boolean isPointy;

	/**
	 * A Tile object that stores its location in a TerrainMap object, it's current height, and whether it is pointy
	 * 
	 * @param height - the integer value from 0 - 99 that represents the height of this Tile in 3D space
	 * @param row - the row this Tile object is located at in a TerrainMap object
	 * @param col - the column this Tile object is located at in a TerrainMap object
	 * @param isPointy - a boolean saying whether or not this tile is pointy in 3D space
	 */
	public Tile(int height, int row, int col, boolean isPointy) {
		this.height = height;
		this.row = row;
		this.col = col;
		this.isPointy = isPointy;
	}

	/**
	 * Sets the value of the Tile's height
	 * 
	 * @param height - the integer value to set this Tile's height to
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Returns the height value of this Tile
	 * 
	 * @return - the integer height value of this Tile
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the integer value representing what row this Tile is located at in a TerrainMap object
	 * 
	 * @return - the integer row representing what row this Tile is located at in a TerrainMap object
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the integer value of row representing what row this Tile is located at in a TerrainMap object
	 * 
	 * @param row - the value to set the row variable of this Tile to based on its location in a TerrainMap
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Returns the integer value representing what column this Tile is located at in a TerrainMap object
	 * 
	 * @return - the integer value column representing what column this Tile is located at in a TerrainMap object
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Sets the integer value of col representing what column this Tile is located at in a TerrainMap object
	 * 
	 * @param col - the value to set the col variable of this Tile to based on its location in a TerrainMap
	 */
	public void setCol(int col) {
		this.col = col;
	}
	
	/**
	 * Returns whether this Tile is pointy or not in 3D space
	 * 
	 * @return - boolean isPointy, which says whether this Tile is supposed to be pointy when in a 3D TerrainMap
	 */
	public boolean getIsPointy() {
		return isPointy;
	}

	/**
	 * Sets whether this Tile is pointy or not in 3D space
	 * 
	 * @param isPointy - a boolean to set the isPointy variable of a Tile object to
	 */
	public void setIsPointy(boolean isPointy) {
		this.isPointy = isPointy;
	}
	
	/**
	 * Makes a new Tile object which is a copy of the current Tile object and returns it
	 * 
	 *@return - a new Tile object which is a copy of the current Tile object
	 */
	public Tile clone() {
		Tile tile = new Tile(this.height, this.row, this.col, this.isPointy);
		return tile;
	}

	/**
	 *Overrides the toString() method to display Tile objects as a string with their height, row, column, and isPointy values
	 *
	 *@return - a string with a Tile's height, row, column, and isPointy values included
	 */
	@Override
	public String toString() {
		return "Tile [height=" + height + ", row=" + row + ", col=" + col + ", isPointy=" + isPointy + "]";
	}	
}
