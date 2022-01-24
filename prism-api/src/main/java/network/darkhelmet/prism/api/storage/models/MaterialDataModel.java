package network.darkhelmet.prism.api.storage.models;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

public class MaterialDataModel {
    /**
     * The material key.
     */
    protected final String materialKey;

    /**
     * The data, if any.
     */
    protected final String data;

    /**
     * Construct a new material/data model.
     *
     * @param materialKey The material key
     */
    public MaterialDataModel(String materialKey) {
        this.materialKey = materialKey;
        this.data = null;
    }

    /**
     * Construct a new material/data model.
     *
     * @param materialKey The material key
     * @param data The data
     */
    public MaterialDataModel(String materialKey, String data) {
        this.materialKey = materialKey;
        this.data = data;
    }

    /**
     * Get the material key.
     *
     * @return The key
     */
    public String materialKey() {
        return materialKey;
    }

    /**
     * Get the data.
     *
     * @return The data, if any
     */
    public @Nullable String data() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        var that = (MaterialDataModel) obj;
        return Objects.equals(this.materialKey, that.materialKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialKey, data);
    }

    @Override
    public String toString() {
        return "MaterialStateModel["
            + "materialKey=" + materialKey + ","
            + "data=" + data + ']';
    }
}