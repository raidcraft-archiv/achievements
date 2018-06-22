package de.raidcraft.betonquestbridge.conditions;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowType;
import de.raidcraft.api.action.flow.parsers.ActionApiFlowParser;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import de.raidcraft.api.action.requirement.Requirement;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class RequirementCondition extends Condition {

    private final Requirement<Player> requirement;

    public RequirementCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);

        String instructionString = instruction.getInstruction().replace("$(ID)", instruction.getID().getFullID());
        ActionApiFlowParser actionApiParser = new ActionApiFlowParser();
        if (!actionApiParser.accept(instructionString)) {
            throw new InstructionParseException("Cannot parse instructions into valid ActionApi Flow pattern.");
        }

        try {
            ActionAPIType actionAPIType = actionApiParser.parse();

            if (actionAPIType.getFlowType() != FlowType.REQUIREMENT) {
                throw new InstructionParseException("The given action api flow instruction is not a requirement. Quest conditions can only check requirements.");
            }

            this.requirement = ActionAPI
                    .createRequirement(instruction.getID().getFullID(), actionAPIType.getTypeId(), actionAPIType.getConfiguration(), Player.class)
                    .orElseThrow(() -> new InstructionParseException("The action " + actionAPIType.getTypeId() + " is not a player action."));
        } catch (FlowException e) {
            throw new InstructionParseException(e.getMessage());
        }
    }

    @Override
    public boolean check(String playerId) {
        return requirement.test(PlayerConverter.getPlayer(playerId));
    }
}
