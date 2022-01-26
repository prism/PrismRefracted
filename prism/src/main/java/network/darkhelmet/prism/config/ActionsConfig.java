package network.darkhelmet.prism.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class ActionsConfig {
    @Comment("block-break is when a player or entity destroys a block (except from burn/explode).")
    private boolean blockBreak = true;

    @Comment("item-drop is when a player or block drops an item on the ground.")
    private boolean itemDrop = true;

    /**
     * Get if block break enabled.
     *
     * @return True if enabled
     */
    public boolean blockBreak() {
        return blockBreak;
    }

    /**
     * Get if item drop enabled.
     *
     * @return True if enabled
     */
    public boolean itemDrop() {
        return itemDrop;
    }
}
