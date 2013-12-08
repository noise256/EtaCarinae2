package control.input;

import java.util.concurrent.ConcurrentHashMap;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyManager implements KeyListener {
	private static KeyManager instance;
	
	private ConcurrentHashMap<Integer, Long> keyPresses = new ConcurrentHashMap<Integer, Long>();
	private ConcurrentHashMap<Integer, Long> keyReleases = new ConcurrentHashMap<Integer, Long>();
	
	public static KeyManager instance() {
		if (instance == null) {
			instance = new KeyManager();
		}
		return instance;
	}
	
	private KeyManager(){
	}

	public boolean isKeyDown(int key) {
		if (keyPresses.containsKey(new Integer(key)) && !keyReleases.containsKey(new Integer(key))) {
			return true;
		}
		else if (keyPresses.containsKey(new Integer(key)) && keyReleases.containsKey(new Integer(key))) {
			if (keyPresses.get(new Integer(key)).compareTo(keyReleases.get(new Integer(key))) > 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (0 == (InputEvent.AUTOREPEAT_MASK & e.getModifiers())) {
			keyPresses.put(new Integer(e.getKeyCode()), new Long(System.currentTimeMillis()));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (0 == (InputEvent.AUTOREPEAT_MASK & e.getModifiers())) {
			keyReleases.put(new Integer(e.getKeyCode()), new Long(System.currentTimeMillis()));
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//Unused
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException(); 
	}
}
