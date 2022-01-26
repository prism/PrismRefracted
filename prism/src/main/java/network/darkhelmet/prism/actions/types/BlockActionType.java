package network.darkhelmet.prism.actions.types;

import de.tr7zw.nbtapi.NBTContainer;

import network.darkhelmet.prism.actions.BlockStateAction;
import network.darkhelmet.prism.api.actions.ActionData;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.actions.types.ActionResultType;
import network.darkhelmet.prism.api.actions.types.ActionType;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

public class BlockActionType extends ActionType {
    /**
     * Construct a new block action type.
     *
     * @param key The key
     * @param resultType The result type
     * @param reversible If action is reversible
     */
    public BlockActionType(String key, ActionResultType resultType, boolean reversible) {
        super(key, resultType, reversible);
    }

    @Override
    public IAction createAction(ActionData actionData) {
        BlockData blockData = null;
        if (actionData.materialData() != null) {
            blockData = Bukkit.createBlockData(actionData.materialName() + actionData.materialData());
        }

        NBTContainer nbtContainer = null;
        if (actionData.customData() != null) {
            nbtContainer = new NBTContainer(actionData.customData());
        }

        return new BlockStateAction(this, actionData.location(), actionData.material(), blockData, nbtContainer);
    }
}
