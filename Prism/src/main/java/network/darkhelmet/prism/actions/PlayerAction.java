package network.darkhelmet.prism.actions;

public class PlayerAction extends GenericAction {

    private String extraInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNiceName() {
        if (extraInfo != null && !extraInfo.isEmpty()) {
            switch (getActionType().getName()) {
                case "player-join":
                    return "来自 " + extraInfo;

                case "xp-pickup":
                    return extraInfo + " 经验";

                case "bucket-fill":
                    switch (extraInfo) {
                        case "lava":
                            return "一个 熔岩桶";
                        case "water":
                            return "一个 水桶";
                        default:
                            return "一个 " + extraInfo + " 桶";
                    }

                default:
                    return extraInfo;
            }
        }

        return "";
    }

    @Override
    public boolean hasExtraData() {
        return extraInfo != null;
    }

    @Override
    public String serialize() {
        return extraInfo;
    }

    @Override
    public void deserialize(String data) {
        extraInfo = data;
    }
}