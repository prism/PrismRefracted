package network.darkhelmet.prism.actions;

import java.util.Locale;

import network.darkhelmet.prism.api.actions.ActionType;
import network.darkhelmet.prism.api.actions.IMaterialAction;

import org.bukkit.Material;

public abstract class MaterialAction extends Action implements IMaterialAction {
    /**
     * The material.
     */
    protected Material material;

    /**
     * Construct a new material action.
     *
     * @param type The action type
     * @param material The material
     */
    public MaterialAction(ActionType type, Material material) {
        super(type);

        this.material = material;
    }

    @Override
    public String serializeMaterial() {
        return material.toString().toLowerCase(Locale.ENGLISH);
    }
}
