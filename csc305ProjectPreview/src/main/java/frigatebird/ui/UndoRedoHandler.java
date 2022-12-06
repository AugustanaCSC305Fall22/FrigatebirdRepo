package frigatebird.ui;

import java.util.Stack;

/**
 * This class is responsible for all the information needed in order to undo or
 * redo an action in the drawing canvas.
 * 
 * @author Dale Skrien (ported to JavaFX by Forrest Stonedahl)
 * @version 1.0 August 2005 (updated to JavaFX Oct. 2018)
 */
public class UndoRedoHandler {
	private Stack<GridEditingCanvas.State> undoStack, redoStack;
	// invariant: The top state of the undoStack always is a copy of the
	// current state of the canvas.
	private GridEditingCanvas canvas;

	/**
	 * constructor
	 * 
	 * @param canvas the DrawingCanvas whose changes are saved for later
	 *               restoration.
	 */
	public UndoRedoHandler(GridEditingCanvas canvas) {
		undoStack = new Stack<GridEditingCanvas.State>();
		redoStack = new Stack<GridEditingCanvas.State>();
		this.canvas = canvas;
		// store the initial state of the canvas on the undo stack
		getUndoStack().push(canvas.createMemento());
	}

	/**
	 * saves the current state of the drawing canvas for later restoration
	 */
	public void saveState() {
		GridEditingCanvas.State canvasState = canvas.createMemento();
		getUndoStack().push(canvasState);
		redoStack.clear();
	}

	/**
	 * restores the state of the drawing canvas to what it was before the last
	 * change. Nothing happens if there is no previous state (for example, when the
	 * application first starts up or when you've already undone all actions since
	 * the startup state).
	 */
	public void undo() {
		if (getUndoStack().size() == 1) // only the current state is on the stack
			return;

		GridEditingCanvas.State canvasState = getUndoStack().pop();
		redoStack.push(canvasState);
		canvas.restoreState(getUndoStack().peek());
	}

	public Stack<GridEditingCanvas.State> getUndoStack() {
		return undoStack;
	}

	/**
	 * restores the state of the drawing canvas to what it was before the last undo
	 * action was performed. If some change was made to the state of the canvas
	 * since the last undo, then this method does nothing.
	 */
	public void redo() {
		if (redoStack.isEmpty())
			return;

		GridEditingCanvas.State canvasState = redoStack.pop();
		getUndoStack().push(canvasState);
		canvas.restoreState(canvasState);
	}
}
