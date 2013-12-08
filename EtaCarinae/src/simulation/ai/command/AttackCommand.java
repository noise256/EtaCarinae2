package simulation.ai.command;

import simulation.object.PhysicalObject;

public class AttackCommand extends AgentCommand {
	private PhysicalObject target;
	
	public AttackCommand(PhysicalObject target) {
		super(CommandType.ATTACK_COMMAND);
		this.target = target;
	}
	
	public PhysicalObject getTarget() {
		return target;
	}
}
