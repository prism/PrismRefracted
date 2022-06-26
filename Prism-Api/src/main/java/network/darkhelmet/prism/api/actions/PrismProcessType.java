package network.darkhelmet.prism.api.actions;

public enum PrismProcessType {
    LOOKUP("查询"),
    ROLLBACK("回滚"),
    RESTORE("还原"),
    DRAIN("排水"),
    EXTINGUISH("灭火"),
    UNDO("撤销"),
    DELETE("删除");

    private final String localization;

    PrismProcessType(String localization) {
        this.localization = localization;
    }

    public String getLocale() {
        return localization;
    }

}