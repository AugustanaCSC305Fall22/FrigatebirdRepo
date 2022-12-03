package frigatebird.ui;

public class ToolBox {

	public static enum Tool {
		SELECT, TWO_POINT_SELECT, HEIGHT, PASTE, POINTY, FILL
	}

	private Tool currentTool;

	public ToolBox(Tool startTool) {
		this.currentTool = startTool;
	}
	
	public Tool getCurrentTool() {
		return this.currentTool;
	}

	public void setCurrentTool(Tool tool) {
		this.currentTool = tool;
	}


}
