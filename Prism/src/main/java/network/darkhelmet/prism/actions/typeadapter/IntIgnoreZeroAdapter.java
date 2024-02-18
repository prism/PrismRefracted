package network.darkhelmet.prism.actions.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IntIgnoreZeroAdapter extends TypeAdapter<Integer> {
    private static final Integer INT_ZERO = 0;

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0;
        }

        return in.nextInt();
    }

    @Override
    public void write(JsonWriter out, Integer data) throws IOException {
        if (data == null || data.equals(INT_ZERO)) {
            out.nullValue();
            return;
        }

        out.value(data.intValue());
    }
}