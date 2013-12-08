package control.event;

import com.jogamp.newt.event.MouseEvent;

public class ExtendedMouseEvent extends MouseEvent {
	private static final long serialVersionUID = -968263200754192643L;
	private float[] worldPosition;

	public ExtendedMouseEvent(MouseEvent mouseEvent, float[] worldPosition) {
		super(mouseEvent.getEventType(), mouseEvent.getSource(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.getButton(), mouseEvent.getWheelRotation());
		this.worldPosition = worldPosition;
	}

	public float[] getWorldPosition() {
		return worldPosition;
	}
}
