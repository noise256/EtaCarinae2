package control.input;

import com.jogamp.newt.event.InputEvent;


public class Input {
	private InputType inputType;
	
	private float[] coordinates;
	
	private int button;
	
	private int modifiers;
	
	private int wheelRotation;
	
	public Input(InputType inputType, float[] coordinates, int button, int modifiers, int wheelRotation) {
		this.inputType = inputType;
		this.coordinates = coordinates;
		this.button = button;
		this.modifiers = this.modifiers | modifiers;
		this.wheelRotation = wheelRotation;
	}
	
	public InputType getInputType() {
		return inputType;
	}
	
	public float[] getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(float[] coordinates) {
		this.coordinates = coordinates;
	}
	
	public int getButton() {
		return button;
	}
	
	public int getWheelRotation() {
		return wheelRotation;
	}
	
	public void setWheelRotation(int wheelRotation) {
		this.wheelRotation = wheelRotation;
	}
	
	public boolean isShiftDown() {
		if ((modifiers & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
			return true;
		}
		return false;
	}
	
	public boolean isCtrlDown() {
		if ((modifiers & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
			return true;
		}
		return false;
	}
	
	public boolean isAltDown() {
		if ((modifiers & InputEvent.ALT_MASK) == InputEvent.ALT_MASK) {
			return true;
		}
		return false;
	}
}
