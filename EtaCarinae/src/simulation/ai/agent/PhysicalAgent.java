package simulation.ai.agent;

import simulation.ai.command.AgentCommand;

public interface PhysicalAgent extends Agent {
	public void addInput(AgentCommand agentCommand);
}
