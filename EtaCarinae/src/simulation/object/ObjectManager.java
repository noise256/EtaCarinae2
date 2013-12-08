package simulation.object;

import java.util.ArrayList;
import java.util.Collection;

import javax.media.opengl.glu.GLU;

import renderer.camera.CameraManager;
import renderer.renderer.RenderEvent;
import renderer.scene.SceneNode;
import simulation.ai.agent.Agent;
import simulation.ai.command.AttackCommand;
import simulation.ai.command.MoveToCommand;
import simulation.ships.Ship;
import simulation.ui.UIPanel;
import simulation.ui.UIPanelManager;
import util.collision.CollisionManager;
import util.math.MathBox;

import com.jogamp.newt.event.MouseEvent;

import control.input.Input;
import control.input.InputType;
import control.input.MouseInputQueue;

public class ObjectManager {
	private GLU glu = new GLU();
	
	private ArrayList<Input> previousInput = new ArrayList<Input>();
	
	private ArrayList<GameObject> highlightedObjects = new ArrayList<GameObject>();
	private ArrayList<GameObject> selectedObjects = new ArrayList<GameObject>();
	
	private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	private ArrayList<GameObject> newGameObjects = new ArrayList<GameObject>();
	
	private ArrayList<Agent> agents = new ArrayList<Agent>();
	private ArrayList<Agent> newAgents = new ArrayList<Agent>();
	
	public void tick() {
		for (Agent agent : agents) {
			agent.update();
			
			for (GameObject newObject : agent.getGeneratedObjects()) {
				if (newObject instanceof Agent) {
					newAgents.add((Agent) newObject);
				}
				newGameObjects.add(newObject);
			}
			agent.clearGeneratedObjects();
		}
		
		gameObjects.addAll(newGameObjects);
		newGameObjects.clear();
		
		agents.addAll(newAgents);
		newAgents.clear();
		
		ArrayList<GameObject> deadObjects = new ArrayList<GameObject>();
		
		for (GameObject gameObject : gameObjects) {
			if (!gameObject.isAlive()) {
				deadObjects.add(gameObject);
			}
		}
		
		for (GameObject deadObject : deadObjects) {
			gameObjects.remove(deadObject);
			
			if (deadObject instanceof Agent) {
				agents.remove(deadObject);
			}
		}
	}
	
	public RenderEvent getObjectDisplayUpdateEvent() {
		ArrayList<SceneNode> objectNodes = new ArrayList<SceneNode>();

		for (GameObject gameObject : gameObjects) {
			gameObject.updateView();
			
			for (SceneNode sceneNode : gameObject.getView()) {
				if (sceneNode.isVisible()) {
					objectNodes.add(sceneNode);
				}
			}
		}
		
		Collection<UIPanel> uiPanels = UIPanelManager.getUIPanels();
		
		for (UIPanel uiPanel : uiPanels) {
			uiPanel.updateView();
			
			if (uiPanel.getVisible()) {
				for (SceneNode sceneNode : uiPanel.getView()) {
					if (sceneNode.isVisible()) {
						objectNodes.add(sceneNode);
					}
				}	
			}
		}
		
		return new RenderEvent(this, objectNodes);
	}
	
	public void handleMouseInput(MouseInputQueue mouseInputQueue) {
		if (mouseInputQueue.getInputQueueSize() == 0) {
			return;
		}
		else {
			Input currentInput = mouseInputQueue.getFirst();
			
			switch (currentInput.getInputType()) {
				case MOUSE_MOVED: 
					handleMouseMove(currentInput);
					break;
				case MOUSE_DRAGGED:
					handleMouseDrag(currentInput);
					break;
				case MOUSE_PRESSED:
					handleMousePress(currentInput);
					break;
				case MOUSE_RELEASED:
					handleMouseRelease(currentInput);
					break;
				case MOUSE_WHEEL_MOVED:
					handleMouseWheelMove(currentInput);
					break;
				default:
					break;  
			}
			
			handleMouseInput(mouseInputQueue);
		}
	}
	
	private void handleMouseMove(Input move) {
		int[] viewport = CameraManager.instance().getViewport();
		float[] modelview = CameraManager.instance().getModelView();
		float[] projection = CameraManager.instance().getProjection();
		float[] wcoord = new float[4];
		
		float winX = move.getCoordinates()[0];
		float winY = (float) viewport[3] - move.getCoordinates()[1];
		float winZ = CameraManager.instance().getMouseDepth();
		
		GLU glu = new GLU();
		glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, wcoord, 0);
		
		
		UIPanel uiCollider = getUICollision(new float[] {winX, winY});
		
		if (uiCollider == null) {
			GameObject objectCollider = getGameObjectCollision(new float[] {wcoord[0], wcoord[1]});
			
			for (GameObject highlightedObject : highlightedObjects) {
				highlightedObject.setHighlighted(false);
			}
			highlightedObjects.clear();
			
			if (selectedObjects.isEmpty()) {
				UIPanelManager.getUIPanel("SelectedPanel").setVisible(false);
				UIPanelManager.getUIPanel("SelectedPanel").setTextSource(null);
			}
			else {
				UIPanelManager.getUIPanel("SelectedPanel").setVisible(true);
				UIPanelManager.getUIPanel("SelectedPanel").setTextSource(selectedObjects.get(0));
			}
			
			if (objectCollider != null) {
				objectCollider.setHighlighted(true);
				highlightedObjects.add(objectCollider);
				
				UIPanelManager.getUIPanel("SelectedPanel").setVisible(true);
				UIPanelManager.getUIPanel("SelectedPanel").setTextSource(objectCollider);
			}
		}
		
		if (previousInput.isEmpty()) {
			previousInput.add(move);
			return;
		}
		else if (previousInput.get(previousInput.size() - 1).getInputType() == InputType.MOUSE_MOVED) {
			previousInput.remove(previousInput.size() - 1);
		}
		
		previousInput.add(move);
	}
	
	private void handleMouseDrag(Input drag) {
		String inputSequence = "";
		for (int i = 0; i < previousInput.size(); i++) {
			inputSequence = inputSequence + previousInput.get(i).getInputType().getCode();
		}
		
		if (!selectedObjects.isEmpty() && inputSequence.contains("pd")) {
			int[] viewport = CameraManager.instance().getViewport();
			float[] modelview = CameraManager.instance().getModelView();
			float[] projection = CameraManager.instance().getProjection();
			
			float[] dragWCoord = new float[4];
			
			float winX = drag.getCoordinates()[0];
			float winY = (float) viewport[3] - drag.getCoordinates()[1];
			float winZ = CameraManager.instance().getMouseDepth();
			
			glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, dragWCoord, 0);
			
			Input press = getPreviousInput(InputType.MOUSE_PRESSED);
			
			float[] pressWCoord = new float[4];
			
			winX = press.getCoordinates()[0];
			winY = (float) viewport[3] - press.getCoordinates()[1];
			winZ = CameraManager.instance().getMouseDepth();
			
			glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, pressWCoord, 0);
			
			UIPanel uiCollider = getUICollision(new float[] {winX, winY});
			
			if (uiCollider == null) {
				if (press.getButton() == MouseEvent.BUTTON3) {
					float[] orientation = MathBox.normaliseVector(MathBox.subtractVectors(new float[] {dragWCoord[0], dragWCoord[1]}, new float[] {pressWCoord[0], pressWCoord[1]}));
					
					for (GameObject selected : selectedObjects) {
						if (selected instanceof Ship) {
							((Ship) selected).setPlanned(true);
							((Ship) selected).addPlan(pressWCoord, orientation);
						}
					}
				}
			}
		}
		
		if (previousInput.isEmpty()) {
			previousInput.add(drag);
			return;
		}
		else if (previousInput.get(previousInput.size() - 1).getInputType() == InputType.MOUSE_DRAGGED) {
			previousInput.remove(previousInput.size() - 1);
		}
		previousInput.add(drag);
	}

	private void handleMousePress(Input press) {
		previousInput.add(press);
	}
	
	private void handleMouseRelease(Input release) {
		int[] viewport = CameraManager.instance().getViewport();
		float[] modelview = CameraManager.instance().getModelView();
		float[] projection = CameraManager.instance().getProjection();
		float[] releaseWCoord = new float[4];
		
		float winX = release.getCoordinates()[0];
		float winY = (float) viewport[3] - release.getCoordinates()[1];
		float winZ = CameraManager.instance().getMouseDepth();
		
		glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, releaseWCoord, 0);
		
		UIPanel uiCollider = getUICollision(new float[] {winX, winY});
		
		if (uiCollider != null) {
			//TODO write UI collision code
		}
		else {
			GameObject releaseCollider = getGameObjectCollision(new float[] {releaseWCoord[0], releaseWCoord[1]});
			
			if (release.getButton() == MouseEvent.BUTTON1) {
				if (!release.isShiftDown()) {
					for (GameObject selectedObject : selectedObjects) {
						selectedObject.setSelected(false);
					}
					selectedObjects.clear();
					
					UIPanelManager.getUIPanel("SelectedPanel").setVisible(false);
					UIPanelManager.getUIPanel("SelectedPanel").setTextSource(null);
				}
				if (releaseCollider != null) {
					releaseCollider.setSelected(true);
					selectedObjects.add(releaseCollider);
					
					UIPanelManager.getUIPanel("SelectedPanel").setVisible(true);
					UIPanelManager.getUIPanel("SelectedPanel").setTextSource(releaseCollider);
				}
			}
			else if (release.getButton() == MouseEvent.BUTTON3) {
				if (!selectedObjects.isEmpty()) {
					Input press = getPreviousInput(InputType.MOUSE_PRESSED);

					if (press != null) {
						float[] pressWCoord = new float[4];
						winX = press.getCoordinates()[0];
						winY = (float) viewport[3] - press.getCoordinates()[1];
						winZ = CameraManager.instance().getMouseDepth();
						
						glu.gluUnProject(winX, winY, winZ, modelview, 0, projection, 0, viewport, 0, pressWCoord, 0);
						
						if (releaseCollider != null && releaseCollider instanceof PhysicalObject) {
							for (GameObject selected : selectedObjects) {
								if (selected instanceof Ship) {
									((Ship) selected).addInput(new AttackCommand((PhysicalObject) releaseCollider));
									((Ship) selected).setPlanned(false);
								}
							}
						}
						else {
							String inputSequence = "";
							for (int i = 0; i < previousInput.size(); i++) {
								inputSequence = inputSequence + previousInput.get(i).getInputType().getCode();
							}
							
							float[] orientation = null;
							if (inputSequence.contains("pd")) {
								orientation = MathBox.normaliseVector(MathBox.subtractVectors(new float[] {releaseWCoord[0], releaseWCoord[1]}, new float[] {pressWCoord[0], pressWCoord[1]}));
							}
							
							for (GameObject selected : selectedObjects) {
								if (selected instanceof Ship) {
									((Ship) selected).addInput(new MoveToCommand(new float[] {pressWCoord[0], pressWCoord[1]}, orientation));
									((Ship) selected).setPlanned(false);
								}
							}
						}
					}
				}
			}
		}
		
		previousInput.clear();
	}
	
	private void handleMouseWheelMove(Input currentInput) {
		return;
	}
	
	private UIPanel getUICollision(float[] screenCoordinates) {
		ArrayList<UIPanel> colliders = new ArrayList<UIPanel>();
		for (UIPanel uiPanel : UIPanelManager.getUIPanels()) {
			if (uiPanel.getVisible()) {
				if (CollisionManager.checkCollision(uiPanel, screenCoordinates)) {
					colliders.add(uiPanel);
				}
			}
		}
		
		UIPanel closest = null;
		float distance = Float.MAX_VALUE;
		for (UIPanel collider : colliders) {
			float newDistance = MathBox.getVectorDistance(collider.getPosition(), screenCoordinates);
			if (newDistance < distance) {
				closest = collider;
				distance = newDistance;
			}
		}
		
		return closest;
	}
	
	private GameObject getGameObjectCollision(float[] worldCoordinates) {
		ArrayList<GameObject> colliders = new ArrayList<GameObject>();
		for (GameObject gameObject : gameObjects) {
			if (CollisionManager.checkCollision(gameObject, worldCoordinates)) {
				colliders.add(gameObject);
			}
		}
		
		GameObject closest = null;
		float distance = Float.MAX_VALUE;
		for (GameObject collider : colliders) {
			float newDistance = MathBox.getVectorDistance(collider.getAbsolutePosition(), new float[] {worldCoordinates[0], worldCoordinates[1]});
			if (newDistance < distance) {
				closest = collider;
				distance = newDistance;
			}
		}
		
		return closest;
	}
	
	private Input getPreviousInput(InputType inputType) {
		for (Input input : previousInput) {
			if (input.getInputType() == inputType) {
				return input;
			}
		}
		return null;
	}
	public void addGameObject(GameObject gameObject) {
		newGameObjects.add(gameObject);
	}
	
	public <T extends GameObject> void addGameObjects(ArrayList<T> gameObjects) {
		newGameObjects.addAll(gameObjects);
	}
	
	public <T extends Agent> void addAgent(T agent) {
		newAgents.add(agent);
	}
	
	public <T extends Agent> void addAgents(ArrayList<T> agents) {
		newAgents.addAll(agents);
	}
}
