package mosaic.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

import java.awt.Color;
import java.io.IOException;

/**
 * Used by the JSON deserializer for parsing Color objects
 */
public class ColorDeserializer extends JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode root = p.getCodec().readTree(p);
        IntNode r = (IntNode)root.get("r");
        IntNode g = (IntNode)root.get("g");
        IntNode b = (IntNode)root.get("b");
        return new Color(r.intValue(), g.intValue(), b.intValue());
    }
}
