package features.snack4.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.json.JsonReader;

/**
 *
 * @author noear 2026/3/15 created
 *
 */
public class RepairReadTest {
    @Test
    public void case1() throws Exception {
        // 模拟 LLM 输出流：前两个是完整的，最后一个是碎掉的碎片
        String streamContent = "{\"id\":3, \"val\":\"c";
        JsonReader reader = new JsonReader(streamContent, Options.of(Feature.Read_AutoRepair));

        // readLast 应该跳过 id:1，拿到 id:2，并忽略掉无法解析的 id:3 碎片
        ONode lastNode = reader.read();

        Assertions.assertNotNull(lastNode);
        Assertions.assertEquals(3, lastNode.get("id").getInt());
        Assertions.assertEquals("c", lastNode.get("val").getString());

        System.out.println("ReadLast Result: " + lastNode.toJson());
    }

    @Test
    public void case2() throws Exception {
        // 模拟 LLM 输出流：前两个是完整的，最后一个是碎掉的碎片
        String streamContent = "[{\"id\":3, \"val\":\"c";
        JsonReader reader = new JsonReader(streamContent, Options.of(Feature.Read_AutoRepair));

        // readLast 应该跳过 id:1，拿到 id:2，并忽略掉无法解析的 id:3 碎片
        ONode lastNode = reader.read();

        Assertions.assertNotNull(lastNode);

        Assertions.assertTrue(lastNode.isArray());
        lastNode = lastNode.get(0);

        Assertions.assertEquals(3, lastNode.get("id").getInt());
        Assertions.assertEquals("c", lastNode.get("val").getString());

        System.out.println("ReadLast Result: " + lastNode.toJson());
    }

    @Test
    public void case3() throws Exception{
        testRepair("{\"employees\":[\"John\", \"Anna\", \"Peter\"]} ","{\"employees\": [\"John\", \"Anna\", \"Peter\"]}");
        testRepair("{\"key\": \"value:value\"}","{\"key\": \"value:value\"}");
        testRepair("{\"text\": \"The quick brown fox,\"}","{\"text\": \"The quick brown fox,\"}");
        testRepair("{\"text\": \"The quick brown fox won\\'t jump\"}","{\"text\": \"The quick brown fox won\\'t jump\"}");
        testRepair("{\"key\": \"\"","{\"key\": \"\"}");
        testRepair("{\"key1\": {\"key2\": [1, 2, 3]}}","{\"key1\": {\"key2\": [1, 2, 3]}}");
        testRepair("{\"key\": 12345678901234567890}","{\"key\": 12345678901234567890}");
        testRepair("{\"key\": \"value\u263a\"}","{\"key\": \"value\\u263a\"}");
        testRepair("{\"key\": \"value\\\\nvalue\"}","{\"key\": \"value\\\\nvalue\"}");

        testRepair("[]{}","[]");
//        testRepair("[]{\"key\":\"value\"}","{\"key\": \"value\"}");
//        testRepair("{\"key\":\"value\"}[1,2,3,True]","[{\"key\": \"value\"}, [1, 2, 3, true]]");
//        testRepair("{\"key\":\"value\"} [1,2,3,True]","xxx");
//        testRepair("[{\"key\":\"value\"}][{\"key\":\"value_after\"}]","[{\"key\": \"value_after\"}]");

//        testRepair("{\"key\": true, \"key2\": false, \"key3\": null}","{\"key\": true, \"key2\": false, \"key3\": null}");
//        testRepair("xxx","xxx");
//        testRepair("xxx","xxx");
//        testRepair("xxx","xxx");
//        testRepair("xxx","xxx");
    }

    public void testRepair(String json1, String json2) throws Exception{
        ONode oNode = JsonReader.read(json1, Options.of(Feature.Read_AutoRepair));

        Assertions.assertEquals(ONode.ofJson(json2).toJson(), oNode.toJson());
    }

    @Test
    public void case_key_truncated() throws Exception {
        String json = "{\"id\":1, \"na"; // Key 还没写完
        ONode node =  new JsonReader(json, Options.of(Feature.Read_AutoRepair)).read();
        Assertions.assertEquals(1, node.get("id").getInt());
    }

    @Test
    public void case_number_truncated() {
        String json = "{\"val\": 12."; // 小数点后没数字了
        // 预期：要么解析为 12，要么忽略这个 key
    }

    @Test
    public void case_keyword_truncated() throws Exception {
        String json = "{\"flag\": tru"; // true 没写完
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
        Assertions.assertTrue(node.get("flag").isBoolean());
    }

    @Test
    public void case_deep_nesting() throws Exception {
        String json = "{\"a\":{\"b\":{\"c\":1"; // 多层未闭合
        ONode node =  new JsonReader(json, Options.of(Feature.Read_AutoRepair)).read();
        Assertions.assertEquals(1, node.get("a").get("b").get("c").getInt());
    }

    @Test
    public void case_array_item_truncated() throws Exception {
        String json = "[1, 2, {\"a\":1"; // 数组最后一个元素是一个破碎的对象
        ONode node = new JsonReader(json, Options.of(Feature.Read_AutoRepair)).read();
        Assertions.assertEquals(2, node.get(1).getInt());
        Assertions.assertEquals(1, node.get(2).get("a").getInt());
    }

    @Test
    public void case_key_incomplete_quote() throws Exception {
        // 场景：Key 的双引号都没闭合
        String json = "{\"id\":1, \"name";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
        Assertions.assertEquals(1, node.get("id").getInt());
        Assertions.assertFalse(node.hasKey("name"));
    }

    @Test
    public void case_value_escape_truncated() throws Exception {
        // 场景：转义字符中途断掉
        String json = "{\"val\":\"abc\\u00";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
        Assertions.assertEquals("abc\u0000", node.get("val").getString());
    }

    @Test
    public void case_number_dot_only() throws Exception {
        // 场景：数字小数点后缺失
        String json = "{\"price\": 12.";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
        Assertions.assertEquals(12, node.get("price").getInt());
    }

    @Test
    public void case_extremely_broken() throws Exception {
        // 场景：只有半个对象起始符
        String json = "{ \"a\" ";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
        Assertions.assertTrue(node.isObject());
    }

    @Test
    public void case_array_trailing_comma_repair() throws Exception {
        // 场景：逗号后没有任何内容就断了
        String json = "[1, 2, ";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
        Assertions.assertEquals(3, node.size());
    }

}
