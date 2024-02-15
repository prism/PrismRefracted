package network.darkhelmet.prism.actions.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class BoolIgnoreFalseAdapter extends TypeAdapter<Boolean> {
    private static final Boolean BOOL_FALSE = false;

    @Override
    public Boolean read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return false;
        }

        return in.nextBoolean();
    }

    @Override
    public void write(JsonWriter out, Boolean data) throws IOException {
        if (data == null || data.equals(BOOL_FALSE)) {
            out.nullValue();
            return;
        }

        out.value(data.booleanValue());
    }
}