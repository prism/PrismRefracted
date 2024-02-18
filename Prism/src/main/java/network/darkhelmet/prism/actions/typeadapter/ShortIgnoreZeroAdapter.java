package network.darkhelmet.prism.actions.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ShortIgnoreZeroAdapter extends TypeAdapter<Short> {
    private static final Short SHORT_ZERO = 0;

    @Override
    public Short read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0;
        }

        return (short) in.nextInt();
    }

    @Override
    public void write(JsonWriter out, Short data) throws IOException {
        if (data == null || data.equals(SHORT_ZERO)) {
            out.nullValue();
            return;
        }

        out.value(data.intValue());
    }
}