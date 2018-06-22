package de.raidcraft.betonquestbridge.events;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowType;
import de.raidcraft.api.action.flow.parsers.ActionApiFlowParser;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class ActionApiEvent extends QuestEvent {

    private final Action<Player> action;

    public ActionApiEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);

        String instructionString = instruction.getInstruction().replace("$(ID)", instruction.getID().getFullID());
        ActionApiFlowParser actionApiParser = new ActionApiFlowParser();
        if (!actionApiParser.accept(instructionString)) {
            throw new InstructionParseException("Cannot parse instructions into valid ActionApi Flow pattern.");
        }

        try {
            ActionAPIType actionAPIType = actionApiParser.parse();

            if (actionAPIType.getFlowType() != FlowType.ACTION) {
                throw new InstructionParseException("The given action api flow instruction is not an action. Quest events can only execute actions.");
            }

            this.action = ActionAPI
                    .createAction(actionAPIType.getTypeId(), actionAPIType.getConfiguration(), Player.class)
                    .orElseThrow(() -> new InstructionParseException("The action " + actionAPIType.getTypeId() + " is not a player action."));
        } catch (FlowException e) {
            throw new InstructionParseException(e.getMessage());
        }
    }

    @Override
    public void run(String playerId) {

        action.accept(PlayerConverter.getPlayer(playerId));
    }
}
