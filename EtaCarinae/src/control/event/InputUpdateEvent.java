package control.event;

import com.jogamp.newt.event.InputEvent;

public class InputUpdateEvent extends UpdateEvent {
	private static final long serialVersionUID = -5664227470359945979L;

	private InputEvent inputEvent;

	public InputUpdateEvent(Object source, InputEvent inputEvent, UpdateEventType eventType) {
		super(source, eventType);
		this.inputEvent = inputEvent;
	}

	public InputEvent getInputEvent() {
		return inputEvent;
	}
}
