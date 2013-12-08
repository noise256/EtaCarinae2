package simulation.ai.command;

public abstract class AgentCommand {
	public enum CommandType {
		MOVE_COMMAND, MOVE_TO_COMMAND, ATTACK_COMMAND;
	}
	
	private CommandType commandType;
	
	public AgentCommand(CommandType commandType) {
		this.commandType = commandType;
	}
	
	public CommandType getCommandType() {
		return commandType;
	}
}
