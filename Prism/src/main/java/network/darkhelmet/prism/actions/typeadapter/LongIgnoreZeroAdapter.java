package network.darkhelmet.prism.actions.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LongIgnoreZeroAdapter extends TypeAdapter<Long> {
    private static final Long LONG_ZERO = 0L;

    @Override
    public Long read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0L;
        }

        return in.nextLong();
    }

    @Override
    public void write(JsonWriter out, Long data) throws IOException {
        if (data == null || data.equals(LONG_ZERO)) {
            out.nullValue();
            return;
        }

        out.value(data.intValue());
    }
}