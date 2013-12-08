package control.input;

import com.jogamp.newt.event.MouseEvent;

public enum InputType {
	MOUSE_PRESSED(false, 'p'), MOUSE_RELEASED(false, 'r'), MOUSE_MOVED(true, 'm'), MOUSE_DRAGGED(true, 'd'), MOUSE_WHEEL_MOVED(true, 'w');

	private boolean replace;
	
	private char code;
	
	private InputType(boolean replace, char code) {
		this.replace = replace;
		this.code = code;
	}

	public boolean getReplace() {
		return replace;
	}
	
	public char getCode() {
		return code;
	}
	
	public static InputType getInputType(int mouseEventType) {
		if (mouseEventType == MouseEvent.EVENT_MOUSE_PRESSED) {
			return InputType.MOUSE_PRESSED;
		}
		else if (mouseEventType == MouseEvent.EVENT_MOUSE_RELEASED) {
			return InputType.MOUSE_RELEASED;
		}
		else if (mouseEventType == MouseEvent.EVENT_MOUSE_MOVED) {
			return InputType.MOUSE_MOVED;
		}
		else if (mouseEventType == MouseEvent.EVENT_MOUSE_DRAGGED) {
			return InputType.MOUSE_DRAGGED;
		}
		else if (mouseEventType == MouseEvent.EVENT_MOUSE_WHEEL_MOVED) {
			return InputType.MOUSE_WHEEL_MOVED;
		}
		return null;
	}
}