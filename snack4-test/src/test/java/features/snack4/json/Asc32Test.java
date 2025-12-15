package features.snack4.json;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2025/12/15 created
 *
 */
public class Asc32Test {
    @Test
    public void csae1() throws Exception {
        //String json = ResourceUtil.getResourceAsString("json/error.json");
        String json = "{\n" +
                "  \"input_desc\" : \"2534102083697188\",\n" +
                "  \"opUserName\" : \"songhang\",\n" +
                "  \"groupId\" : \"\"\n\1" +
                "}\0\0\0\0";

        ONode oNode = ONode.ofJson(json);

        System.out.println(oNode.toJson());
    }
}
