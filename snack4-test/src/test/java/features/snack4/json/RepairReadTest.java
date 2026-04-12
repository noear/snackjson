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
        // 基础补全测试
        testRepair("{\"status\": tru","{\"status\": true}");
        testRepair("{\"a\":{\"b\":1","{\"a\":{\"b\":1}}");
        testRepair("[1, 2, ]","[1, 2]");
        testRepair("[1, 2, ","[1, 2]");

        // 结构异常补全测试
        testRepair("{\"key\":","{\"key\": null}");
        testRepair("{\"a\":1} #comment","{\"a\":1}");

        // 补充：极端截断场景
        testRepair("{", "{}");
        testRepair("[", "[]");
        testRepair("{\"id\":1,", "{\"id\":1}"); // 逗号后截断，应忽略后续缺失的 key
        testRepair("{\"a\":1, \"b\": { \"c\": [1,2", "{\"a\":1, \"b\":{\"c\":[1,2]}}");
    }

    public void testRepair(String json1, String json2) throws Exception{
        ONode oNode = JsonReader.read(json1, Options.of(Feature.Read_AutoRepair, Feature.Read_AllowComment));

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
        System.out.println(node.toJson());
        Assertions.assertEquals(2, node.size());
    }

    @Test
    public void case_number_boundary_truncated() throws Exception {
        // 场景 1：停在负号上
        testRepair("{\"val\": -", "{\"val\": 0}");

        // 场景 2：停在小数点上
        testRepair("{\"price\": 12.", "{\"price\": 12}");

        // 场景 3：指数符号截断
        testRepair("{\"exp\": 1.2e", "{\"exp\": 1.2}");
        testRepair("{\"exp\": 1.2e-", "{\"exp\": 1.2}");
        testRepair("{\"exp\": 1.2e+", "{\"exp\": 1.2}");
    }

    @Test
    public void case_string_escape_truncated() throws Exception {
        // 场景 1：Unicode 只有两位
        testRepair("{\"msg\":\"abc\\u00", "{\"msg\":\"abc\\u0000\"}");

        // 场景 2：转义符本身被截断
        testRepair("{\"path\":\"C:\\", "{\"path\":\"C:\"}");

        // 场景 3：双引号内部截断且包含控制字符
        testRepair("{\"text\":\"hello\nworld", "{\"text\":\"hello\\nworld\"}");
    }

    @Test
    public void case_keyword_mixed_truncated() throws Exception {
        testRepair("{\"f1\": fal", "{\"f1\": false}");
        testRepair("{\"f2\": nul", "{\"f2\": null}");
        testRepair("{\"f3\": un", "{\"f3\": null}"); // undefined 修复为 null
    }

    @Test
    public void case_date_truncated() throws Exception {
        // 场景：new Date( 还没写完
        testRepair("{\"time\": new Da", "{\"time\": null}");

        // 场景：时间戳写了一半
        testRepair("{\"time\": new Date(1710", "{\"time\":1710}"); // 视具体的 Date 处理逻辑而定
    }

    @Test
    public void case_illegal_char_infinite_loop() throws Exception {
        // 在对象 Key 之后，本该是冒号的地方出现了非法字符 @
        String json = "{\"a\" @ \"b\"}";
        // 预期：不抛出异常，能识别出 a 即可，或者直接返回已解析的部分
        Assertions.assertDoesNotThrow(() -> {
            ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));
            System.out.println("Result: " + node.toJson());
        });
    }

    @Test
    public void case_truncate_after_colon() throws Exception {
        // 场景：写了冒号，但 Value 一点都没写就断了
        String json = "{\"id\":1, \"name\":";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));

        Assertions.assertEquals(1, node.get("id").getInt());
        // 这里的行为取决于实现：是忽略 name，还是将 name 设为 null/undefined
        Assertions.assertTrue(node.get("name").isNull() || node.get("name").isUndefined());
    }

    @Test
    public void case_number_invalid_format() throws Exception {
        // 场景 1：多个小数点
        testRepair("{\"val\": 1.2.3}", "{\"val\": 1.2}");

        // 场景 2：正负号连用
        testRepair("{\"val\": +-123}", "{\"val\": -123}");
    }

    @Test
    public void case_string_end_with_backslash() throws Exception {
        // 场景：字符串以转义符结尾，后面直接 EOF
        String json = "{\"path\": \"C:\\";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));

        // 预期：不要尝试去读反斜杠后面的字符（会导致 EOF 错误），而是直接结束字符串
        Assertions.assertNotNull(node.get("path").getString());
    }

    @Test
    public void case_date_truncated_refined() throws Exception {
        // 场景：new Date( 后面没数字了
        String json = "{\"time\": new Date(";
        ONode node = JsonReader.read(json, Options.of(Feature.Read_AutoRepair));

        // 此时 parseDate 内部 parseNumber 返回 null，函数返回 new ONode(opts)
        Assertions.assertTrue(node.get("time").isUndefined());
    }
}
