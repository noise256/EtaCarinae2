package simulation.ai.command;

public class MoveToCommand extends AgentCommand {
	private float[] destination;
	private float[] orientation;
	
	public MoveToCommand(float[] destination, float[] orientation) {
		super(CommandType.MOVE_TO_COMMAND);
		
		this.destination = destination;
		this.orientation = orientation;
	}
	
	public float[] getDestination() {
		return destination;
	}
	
	public float[] getOrientation() {
		return orientation;
	}
}
