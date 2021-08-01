package me.botsko.prism.actions;

import me.botsko.prism.Prism;
import me.botsko.prism.api.ChangeResult;
import me.botsko.prism.api.ChangeResultType;
import me.botsko.prism.api.PrismParameters;
import me.botsko.prism.appliers.ChangeResultImpl;
import me.botsko.prism.utils.block.Utilities;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;

public class HangingItemAction extends GenericAction {

    private HangingItemActionData actionData;

    /**
     * Constructor.
     * @param hanging Hanging item
     */
    public void setHanging(Hanging hanging) {

        actionData = new HangingItemActionData();

        if (hanging != null) {
            if (hanging instanceof Painting) {
                this.actionData.type = "画";
            } else if (hanging instanceof LeashHitch) {
                this.actionData.type = "栓绳";
            } else if (hanging instanceof ItemFrame) {
                this.actionData.type = "物品展示框";
            }
            //TODO: Rothes - API版本
            /* else if (hanging instanceof GlowItemFrame) {
                this.actionData.type = "发光的物品展示框";
            }*/ else {
                this.actionData.type = hanging.getType().name().toLowerCase();
            }
            this.actionData.direction = hanging.getAttachedFace().name().toLowerCase();
            if (hanging instanceof Painting) {
                this.actionData.art = ((Painting) hanging).getArt().name();
            }
            setLoc(hanging.getLocation().getBlock().getLocation());
        }
    }

    @Override
    public boolean hasExtraData() {
        return actionData != null;
    }

    @Override
    public String serialize() {
        return gson().toJson(actionData);
    }

    @Override
    public void deserialize(String data) {
        if (data != null && data.startsWith("{")) {
            actionData = gson().fromJson(data, HangingItemActionData.class);
        }
    }

    /**
     * Get Type.
     *
     * @return String
     */
    @SuppressWarnings("WeakerAccess")
    public String getHangingType() {
        return actionData.type;
    }

    public String getArt() {
        return actionData.art;
    }

    /**
     * Get Direction.
     *
     * @return BlockFace
     */
    @SuppressWarnings("WeakerAccess")
    public BlockFace getDirection() {
        if (actionData.direction != null) {
            return BlockFace.valueOf(actionData.direction.toUpperCase());
        }
        return null;
    }

    @Override
    public String getNiceName() {
        return this.actionData.type != null ? this.actionData.type : "未知";
    }

    @Override
    public ChangeResult applyRollback(Player player, PrismParameters parameters, boolean isPreview) {
        return hangItem(player, parameters, isPreview);
    }

    @Override
    public ChangeResult applyRestore(Player player, PrismParameters parameters, boolean isPreview) {
        return hangItem(player, parameters, isPreview);
    }

    /**
     * Get A change result.
     * @param player Player
     * @param parameters Query params
     * @param isPreview is preview.
     * @return ChangeResult
     * @todo I am not sure this actual is used during preview?? also no rollback info is saved to undo this.
     */
    private ChangeResult hangItem(Player player, PrismParameters parameters, boolean isPreview) {
        if (actionData == null) {
            Prism.debug(parameters.getProcessType() + "已跳过 - 悬挂上行为数据为 null");
            return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
        }

        final BlockFace attachedFace = getDirection();

        final Location loc = getLoc().getBlock().getRelative(getDirection())
                .getLocation();

        // Ensure there's a block at this location that accepts an attachment
        if (Utilities.materialMeansBlockDetachment(loc.getBlock().getType())) {
            Prism.debug(parameters.getProcessType() + "悬挂上已跳过 - 方块会脱离: "
                    + loc.getBlock().getType());
            return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
        }
        try {
            if (getHangingType().equals("物品展示框")) {
                final Hanging hangingItem = getWorld().spawn(loc, ItemFrame.class);
                hangingItem.setFacingDirection(attachedFace, true);
                return new ChangeResultImpl(ChangeResultType.APPLIED, null); //no change recorded
            } else if (getHangingType().equals("画")) {
                final Painting hangingItem = getWorld().spawn(loc, Painting.class);
                hangingItem.setFacingDirection(getDirection(), true);
                Art art = Art.getByName(getArt());
                if (art != null) {
                    hangingItem.setArt(art);
                }
                return new ChangeResultImpl(ChangeResultType.APPLIED, null); //no change recorded
            }
        } catch (final IllegalArgumentException e) {
            // Something interfered with being able to place the painting
        }
        return new ChangeResultImpl(ChangeResultType.SKIPPED, null);
    }

    @SuppressWarnings("WeakerAccess")
    public static class HangingItemActionData {
        public String type;
        public String direction;
        public String art;
    }
}