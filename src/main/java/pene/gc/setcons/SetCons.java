package pene.gc.setcons;

import emu.grasscutter.plugin.Plugin;
import pene.gc.setcons.commands.Setconstellations;

/**
 * The Grasscutter plugin template.
 * This is the main class for the plugin.
 */
public final class SetCons extends Plugin {
    /* Turn the plugin into a singleton. */
    private static SetCons instance;

    /**
     * Gets the plugin instance.
     * @return A plugin singleton.
     */
    public static SetCons getInstance() {
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
        this.getHandle().registerCommand(new Setconstellations());

        // Log a plugin status message.
        this.getLogger().info("The SetConstellations plugin has been enabled.");
    }
}
