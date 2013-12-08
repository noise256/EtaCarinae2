package control.input;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

public class MouseInputQueue implements MouseListener {
	private ArrayDeque<Input> inputQueue = new ArrayDeque<Input>();
	
	public void addInput(MouseEvent mouseEvent) {
		InputType inputType = InputType.getInputType(mouseEvent.getEventType());
		
		if (inputType == null) {
			return;
		}
		
		if (inputQueue.isEmpty() || !inputType.getReplace()) {
			inputQueue.push(new Input(inputType, new float[] {mouseEvent.getX(), mouseEvent.getY()}, mouseEvent.getButton(), mouseEvent.getModifiers(), mouseEvent.getWheelRotation()));
		}
		else if (inputQueue.peek().getInputType() == inputType) {
			try {
				Input first = inputQueue.pop();
				first.setCoordinates(new float[] {mouseEvent.getX(), mouseEvent.getY()});
				first.setWheelRotation(mouseEvent.getWheelRotation());
				inputQueue.push(first);
			}
			catch (NoSuchElementException e) {
				System.err.println("No element was found in MouseInputQueue while trying to replace an older Mouse Move or Drag element with a newer one.");
				e.printStackTrace();
			}
		}
		else {
			inputQueue.push(new Input(inputType, new float[] {mouseEvent.getX(), mouseEvent.getY()}, mouseEvent.getButton(), mouseEvent.getModifiers(), mouseEvent.getWheelRotation()));
		}
	}
	
	public Input peekFirst() {
		return inputQueue.peek();
	}
	
	public Input getFirst() {
		return inputQueue.pop();
	}
	
	/**
	 * Peeks and returns the last mouse event to be added to the queue with an InputType 'inputType'.
	 * 
	 * @param inputType The InputType to search for.
	 */
	public Input peekInput(InputType inputType) {
		Iterator<Input> it = inputQueue.iterator();
		
		while (it.hasNext()) {
			Input next = it.next();
			if (next.getInputType() == inputType) {
				return next;
			}
		}
		
		return null;
	}
	
	public ArrayDeque<Input> getInput() {
		return inputQueue;
	}
	
	/**
	 * Peeks and returns the last mouse event to be added to the queue with an InputType 'inputType'.
	 * 
	 * @param inputType The InputType to search for.
	 */
	public Input getInput(InputType inputType) {
		Iterator<Input> it = inputQueue.iterator();
		
		Input match = null;
		while (it.hasNext()) {
			Input next = it.next();
			if (next.getInputType() == inputType) {
				match = next;
			}
		}
		
		if (match != null) {
			inputQueue.remove(match);
			return match;
		}
		
		return null;
	}

	public int getInputQueueSize() {
		return inputQueue.size();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		addInput(e);
	}

	@Override
	public void mouseWheelMoved(MouseEvent e) {
		addInput(e);
	}
}
