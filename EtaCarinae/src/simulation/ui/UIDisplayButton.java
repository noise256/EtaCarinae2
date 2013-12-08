package simulation.ui;

public class UIDisplayButton extends UIButton {
	private UITextPanel uiTextPanel;
	
	public UIDisplayButton(float[] position, float width, float height, String texturePath) {
		super(position, width, height, texturePath);
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
	
	public UITextPanel getUITextPanel() {
		return uiTextPanel;
	}
}
