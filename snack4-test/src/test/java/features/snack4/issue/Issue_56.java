package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2026/3/12 created
 *
 */
public class Issue_56 {
    String json = "{\n" +
            "  \"openapi\": \"3.0.1\",\n" +
            "  \"info\": {\n" +
            "    \"title\": \"OpenAPI definition\",\n" +
            "    \"version\": \"v0\"\n" +
            "  },\n" +
            "  \"servers\": [\n" +
            "    {\n" +
            "      \"url\": \"http://localhost:8080\",\n" +
            "      \"description\": \"Generated server url\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"paths\": {\n" +
            "    \"/api/overlay\": {\n" +
            "      \"get\": {\n" +
            "        \"tags\": [\"overlay-rest\"],\n" +
            "        \"operationId\": \"get\",\n" +
            "        \"parameters\": [\n" +
            "          {\n" +
            "            \"name\": \"x\",\n" +
            "            \"in\": \"query\",\n" +
            "            \"required\": true,\n" +
            "            \"schema\": {\n" +
            "              \"type\": \"string\"\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"name\": \"y\",\n" +
            "            \"in\": \"query\",\n" +
            "            \"required\": true,\n" +
            "            \"schema\": {\n" +
            "              \"type\": \"string\"\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"responses\": {\n" +
            "          \"200\": {\n" +
            "            \"description\": \"OK\",\n" +
            "            \"content\": {\n" +
            "              \"*/*\": {\n" +
            "                \"schema\": {\n" +
            "                  \"type\": \"string\"\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"components\": {}\n" +
            "}";

    @Test
    public void case1() {
        ONode oNode = ONode.ofJson(json);
        ONode rst = null;

        rst = oNode.select("$['paths']['/api/overlay']['get']['parameters']");
        System.out.println(rst.toJson());

        rst = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name]");
        System.out.println(rst.toJson());

        ONode rst11 = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name = 'x']");
        System.out.println(rst11.toJson());

        ONode rst12 = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name='x']");
        System.out.println(rst12.toJson());

        ONode rst21 = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name == 'x']");
        System.out.println(rst21.toJson());

        ONode rst22 = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name=='x']");
        System.out.println(rst22.toJson());

        assert rst11.toJson().equals(rst12.toJson());
        assert rst21.toJson().equals(rst22.toJson());
        assert rst11.toJson().equals(rst22.toJson());

        assert "[{\"name\":\"x\",\"in\":\"query\",\"required\":true,\"schema\":{\"type\":\"string\"}}]"
                .equals(rst22.toJson());
    }

    @Test
    public void case2() {
        ONode oNode = ONode.ofJson(json);

        ONode rst21 = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name != 'x']");
        System.out.println(rst21.toJson());

        ONode rst22 = oNode.select("$['paths']['/api/overlay']['get']['parameters'][?@.name!='x']");
        System.out.println(rst22.toJson());

        assert rst21.toJson().equals(rst22.toJson());
        assert "[{\"name\":\"y\",\"in\":\"query\",\"required\":true,\"schema\":{\"type\":\"string\"}}]"
                .equals(rst22.toJson());
    }
}