package de.raidcraft.achievements.api.trigger;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Triggerable {

    public void process(Trigger trigger);
}
