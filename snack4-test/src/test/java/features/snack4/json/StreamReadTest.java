package features.snack4.json;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.json.JsonReader;

import java.io.StringReader;

/**
 *
 * @author noear 2026/2/2 created
 *
 */
public class StreamReadTest {
    @Test
    public void case1() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("{\"name\":\"noear\"}{'a':1}aaa"));

        while (true) {
            ONode oNode = reader.readNext();

            if (oNode == null) {
                break;
            } else {
                System.out.println(oNode.toJson());
            }
        }
    }

    @Test
    public void case2() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("{\"name\":\"noear\"}{'a':1}"));

        while (true) {
            ONode oNode = reader.readNext();

            if (oNode == null) {
                break;
            } else {
                System.out.println(oNode.toJson());
            }
        }
    }
}
