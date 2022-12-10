package frigatebird.terrainbuilder;

/**
 * A Tile object that stores its location in a TerrainMap object, it's current height, and whether it is pointy
 */
public class Tile {

	private int height;
	private int row;
	private int col;
	private boolean isPointy;
	private boolean isHexagonal;

	/**
	 * A Tile object that stores its location in a TerrainMap object, it's current height, and whether it is pointy
	 * 
	 * @param height - the integer value from 0 - 99 that represents the height of this Tile in 3D space
	 * @param row - the row this Tile object is located at in a TerrainMap object
	 * @param col - the column this Tile object is located at in a TerrainMap object
	 * @param isPointy - a boolean saying whether or not this tile is pointy in 3D space
	 */
	public Tile(int height, int row, int col, boolean isPointy, boolean isHexagonal) {
		this.height = height;
		this.row = row;
		this.col = col;
		this.isPointy = isPointy;
		this.isHexagonal=isHexagonal;
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
	
	public double getCenterX(double tileWidth) {
		if (!isHexagonal) {
			return (col + 0.5) * tileWidth;
		} else {
			if (row % 2 == 0) {
				return (col + 0.5) * tileWidth;				
			} else { // shifted right a half hexagon
				return (col + 1) * tileWidth;
			}
		}
	}
	public double getCenterY(double tileWidth) {
		if (!isHexagonal) {
			return (row + 0.5) * tileWidth;
		} else {
			double sideLen = tileWidth/Math.sqrt(3);
			double rowHeight = tileWidth * Math.sqrt(3) / 2;
			return sideLen + rowHeight*row;
		}
		
	}
	
	/**
	 * @param tileWidth - for scaling to the target coordinate system
	 * @return the polygon's x-coordinates in a clockwise order, starting from top left corner
	 */
	public double[] getPolygonXCoords(double tileWidth) {
		if (!isHexagonal) {
			double xLeft = col * tileWidth;
			double xRight = (col+1) * tileWidth;
			return new double[] { xLeft, xRight, xRight, xLeft };
		} else {
			double xLeft = col * tileWidth;
			double xMid = (col + 0.5) * tileWidth;
			double xRight = (col+1) * tileWidth;
			return new double[] { xLeft, xMid, xRight, xRight, xMid, xLeft}; 
		}
		
	}
	
	/**
	 * @param tileWidth - for scaling to the target coordinate system
	 * @return the polygon's y-coordinates in a clockwise order, starting from top left corner
	 */
	public double[] getPolygonYCoords(double tileWidth) {
		if (!isHexagonal) {
			double yTop = row * tileWidth;
			double yBottom = (row+1) * tileWidth;
			return new double[] {yTop, yTop, yBottom, yBottom };
		} else {
			double yTop = row * tileWidth;
			double yMidTop = (row * tileWidth) + (tileWidth/Math.sqrt(3))/2;
			double yMidBottom = yMidTop + (tileWidth/Math.sqrt(3))/2;
			double yBottom = yMidBottom + (tileWidth/Math.sqrt(3))/2;
			return new double[] {yTop, yMidTop, yMidBottom, yBottom};
			
		}
		
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
		Tile tile = new Tile(this.height, this.row, this.col, this.isPointy, this.isHexagonal);
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
