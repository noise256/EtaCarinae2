package control.event;

import java.util.EventObject;

public abstract class UpdateEvent extends EventObject {
	private static final long serialVersionUID = 1970498624412501578L;

	private UpdateEventType eventType;

	public UpdateEvent(Object source, UpdateEventType eventType) {
		super(source);
		this.setEventType(eventType);
	}

	public UpdateEventType getEventType() {
		return eventType;
	}

	public void setEventType(UpdateEventType eventType) {
		this.eventType = eventType;
	}
}
