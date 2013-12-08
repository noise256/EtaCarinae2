package simulation.main;

import java.util.ArrayList;

import renderer.newt.GWindow;
import renderer.profiles.CameraProfile;
import renderer.profiles.LightingProfile;
import renderer.renderer.RenderEvent;
import renderer.renderer.Renderer;
import simulation.ai.agent.Agent;
import simulation.object.GameObject;
import simulation.object.ObjectManager;
import util.timer.TimerManager;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.opengl.GLWindow;

import control.event.Observable;
import control.event.Observer;
import control.input.KeyManager;
import control.input.MouseInputQueue;

public abstract class SimulationManager implements Runnable, Observable<RenderEvent> {
	public static void main(String[] args) {
		Simulation simulation = new Simulation();
		simulation.generate();
		
		new Thread(simulation).run();
	}
	
	private static GLWindow glWindow;

	private MouseInputQueue mouseInputQueue = new MouseInputQueue();
	
	protected ObjectManager objectManager = new ObjectManager();
	protected ArrayList<Observer<RenderEvent>> observers = new ArrayList<Observer<RenderEvent>>();
	
	private int fps;
	private int lastFpsTime;
	
	public SimulationManager() {
		LightingProfile lightingProfile = new LightingProfile(
				new float[] {0.2f, 0.2f, 0.2f}, 
				new float[] { 1.0f, 0.0f, 1.0f, 0.0f },
				new float[] { 0.0f, 0.0f, 0.0f, 1.0f },
				new float[] { 0.5f, 0.5f, 0.5f, 1.0f },
				new float[] { 0.8f, 0.8f, 1.0f, 1.0f }
		);
		
		GWindow window = new GWindow();
		glWindow = window.getWindow();
		
		MouseInputQueue rendererMouseInputQueue = new MouseInputQueue();
		MouseInputQueue simulationMouseInputQueue = new MouseInputQueue();
		glWindow.addMouseListener(rendererMouseInputQueue);
		glWindow.addMouseListener(simulationMouseInputQueue);

		mouseInputQueue = simulationMouseInputQueue;
		
		KeyManager keyManager = KeyManager.instance();
		glWindow.addKeyListener(keyManager);
		
		CameraProfile cameraProfile = new CameraProfile(new float[] {0.0f, 0.0f, 499.0f}, new float[] {-Float.MAX_VALUE, -Float.MAX_VALUE, 100.0f}, new float[] {Float.MAX_VALUE, Float.MAX_VALUE, 6000.0f});
		Renderer renderer = Renderer.instance(lightingProfile, cameraProfile, rendererMouseInputQueue);
		
//		glWindow.setFullscreen(true);
		glWindow.addGLEventListener(renderer);
		
		// start game and add observers
		addObserver(renderer);
	}
	
	@Override
	public void run() {
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

		// keep looping round til the game ends
		while (true) {
			if (KeyManager.instance().isKeyDown(KeyEvent.VK_ESCAPE)) {
				glWindow.destroy();
				System.exit(0);
			}
			
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;

			// update the frame counter
			lastFpsTime += updateLength;
			fps++;

			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000000000) {
				glWindow.setTitle("FPS: " + fps);
				lastFpsTime = 0;
				fps = 0;
			}

			// render
			glWindow.display();
			
			// update the game logic
			tick();
			
			handleInput(mouseInputQueue);
			
			// sleep the current thread for the appropriate amount of time
			try {
				Thread.sleep(Math.max(0, (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000));
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void tick() {
		TimerManager.tickTimers();
		
		objectManager.tick();
		
		updateObservers(objectManager.getObjectDisplayUpdateEvent());
	}


	private void handleInput(MouseInputQueue mouseInputQueue) {
		if (mouseInputQueue.getInputQueueSize() > 0) {
			objectManager.handleMouseInput(mouseInputQueue);
		}
	}
	
	public <T extends GameObject> void addGameObject(T gameObject) {
		objectManager.addGameObject(gameObject);
	}
	
	public <T extends GameObject> void addGameObjects(ArrayList<T> gameObjects) {
		objectManager.addGameObjects(gameObjects);
	}
	
	public <T extends Agent> void addAgent(T agent) {
		objectManager.addAgent(agent);
	}
	
	public <T extends Agent> void addAgents(ArrayList<T> agents) {
		objectManager.addAgents(agents);
	}
	
	@Override
	public void addObserver(Observer<RenderEvent> observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer<RenderEvent> observer) {
		observers.remove(observer);
	}

	@Override
	public void updateObservers(RenderEvent eventObject) {
		for (Observer<RenderEvent> observer : observers) {
			observer.update(eventObject);
		}
	}
	
	protected abstract void generate();
}
