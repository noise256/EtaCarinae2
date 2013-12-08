package simulation.object;

public interface Interactable {
	public void setSelected(boolean selected);
	
	public boolean isSelected();
	
	public void setHighlighted(boolean highlighted);
	
	public boolean isHighlighted();
}
