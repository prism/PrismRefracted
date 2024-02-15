package network.darkhelmet.prism.actions.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ItemNiceNameIgnoreEmptyAdapter extends TypeAdapter<String> {

    @Override
    public String read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return "";
        }

        return in.nextString();
    }

    @Override
    public void write(JsonWriter out, String data) throws IOException {
        if (data == null || data.isEmpty()) {
            out.nullValue();
            return;
        }

        out.value(data);
    }
}