package pene.gc.ToggleCons;

import emu.grasscutter.plugin.Plugin;
import pene.gc.ToggleCons.commands.Toggleconstellations;

/**
 * The Grasscutter plugin template.
 * This is the main class for the plugin.
 */
public final class ToggleCons extends Plugin {
    /* Turn the plugin into a singleton. */
    private static ToggleCons instance;

    /**
     * Gets the plugin instance.
     * @return A plugin singleton.
     */
    public static ToggleCons getInstance() {
        return instance;
    }

    /**
     * This method is called immediately after the plugin is first loaded into system memory.
     */
    @Override public void onLoad() {
        // Set the plugin instance.
        instance = this;
    }
    /**
     * This method is called before the servers are started, or when the plugin enables.
     */
    @Override public void onEnable() {
        
        // Register commands.
        this.getHandle().registerCommand(new Toggleconstellations());

        // Log a plugin status message.
        this.getLogger().info("The ToggleConstellations plugin has been enabled.");
    }
}
