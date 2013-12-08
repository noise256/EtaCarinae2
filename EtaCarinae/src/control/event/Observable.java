package control.event;

import java.util.EventObject;

public interface Observable<T extends EventObject> {
	public void addObserver(Observer<T> observer);

	public void removeObserver(Observer<T> observer);

	public void updateObservers(T event);
}
