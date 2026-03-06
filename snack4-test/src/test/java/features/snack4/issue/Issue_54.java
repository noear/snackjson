package features.snack4.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.SnackException;

/**
 *
 * @author noear 2025/12/29 created
 *
 */
public class Issue_54 {
    String json = "{\n" +
            "  \"openapi\": \"3.1.0\",\n" +
            "  \"info\": {\n" +
            "    \"title\": \"API with a paged collection\",\n" +
            "    \"version\": \"1.0.0\"\n" +
            "  },\n" +
            "  \"paths\": {\n" +
            "    \"/items\": {\n" +
            "      \"get\": {\n" +
            "        \"x-oai-traits\": [\n" +
            "          \"paged\"\n" +
            "        ],\n" +
            "        \"responses\": {\n" +
            "          \"200\": {\n" +
            "            \"description\": \"OK\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    public void case1() {
        ONode dom = ONode.ofJson(json);
        ONode oNode;

        oNode = dom.select("$.paths.*.get");
        System.out.println(oNode.toJson());


        oNode = dom.select("$.paths[?@.get.responses].get");
        System.out.println(oNode.toJson());
        assert "[{\"x-oai-traits\":[\"paged\"],\"responses\":{\"200\":{\"description\":\"OK\"}}}]".equals(oNode.toJson());
    }
}