package network.darkhelmet.prism.actions;

import org.bukkit.Location;

public class BlockFallAction extends BlockChangeAction {

    private BlockFallActionData actionData;

    public void setFrom(Location from) {

        // Build an object for the specific details of this action
        actionData = new BlockFallActionData();

        // Store information for the action
        if (from != null) {
            actionData.x = from.getBlock().getX();
            actionData.y = from.getBlock().getY();
            actionData.z = from.getBlock().getZ();
            actionData.start = false;

        } else {
            actionData.start = true;
        }

    }

    public boolean isStartFalling() {
        return actionData.start;
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
            actionData = gson().fromJson(data, BlockFallActionData.class);
        }
    }

    @Override
    public String getNiceName() {
        String extraInfo = "未知坐标";
        if (actionData != null) {
            if (actionData.start) {
                extraInfo = "(开始坠落)";
            } else {
                extraInfo = "原坐标 " + actionData.x + " " + actionData.y + " " + actionData.z;
            }
        }

        return extraInfo;
    }

    public static class BlockFallActionData {
        boolean start;
        int x;
        int y;
        int z;
    }
}
