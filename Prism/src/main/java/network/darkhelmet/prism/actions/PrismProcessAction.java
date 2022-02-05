package network.darkhelmet.prism.actions;

import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.api.actions.PrismProcessType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class PrismProcessAction extends GenericAction {

    /**
     * The extra data.
     */
    private PrismProcessActionData actionData;

    /**
     * Process.
     * @param processType PrismProcessType
     * @param parameters String
     */
    public void setProcessData(PrismProcessType processType, String parameters) {

        actionData = new PrismProcessActionData();

        if (processType != null) {
            actionData.params = parameters;
            actionData.processType = processType.name().toLowerCase();
            actionData.localedType = processType.getLocale();
        }
    }

    @Override
    public boolean hasExtraData() {
        return actionData != null;
    }

    @Override
    public String serialize() {
        return PrismProcessActionData.gson.toJson(actionData);
    }

    @Override
    public void deserialize(String data) {
        if (data != null && !data.isEmpty()) {
            actionData = PrismProcessActionData.gson.fromJson(data, PrismProcessActionData.class);
            actionData.localedType = PrismProcessType.valueOf(actionData.processType.toUpperCase()).getLocale();
        }
    }

    /**
     * Get Type.
     * @return String
     */
    public String getProcessChildActionType() {
        return Prism.getActionRegistry().getAction("prism-" + actionData.processType).getName();
    }

    /**
     * Get nice name.
     */
    @Override
    public String getNiceName() {
        return actionData.localedType + " (" + actionData.params + ")";
    }

    public static class PrismProcessActionData {
        public static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        @Expose
        public String params = "";
        @Expose
        public String processType;
        @Expose(serialize = false, deserialize = false)
        public String localedType;
    }
}