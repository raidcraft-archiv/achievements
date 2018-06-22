package de.raidcraft.betonquestbridge;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.betonquestbridge.conditions.RequirementCondition;
import de.raidcraft.betonquestbridge.events.ActionApiEvent;
import pl.betoncraft.betonquest.BetonQuest;

/**
 * @author mdoering
 */
public class RCBetonQuestBridgePlugin extends BasePlugin {


    @Override
    public void enable() {

        getLogger().info("Enabling RaidCraft <-> BetonQuest Bridge");
        BetonQuest betonQuest = BetonQuest.getInstance();
        betonQuest.registerEvents("rcact", ActionApiEvent.class);
        betonQuest.registerConditions("rcreq", RequirementCondition.class);
    }

    @Override
    public void disable() {
    }
}
