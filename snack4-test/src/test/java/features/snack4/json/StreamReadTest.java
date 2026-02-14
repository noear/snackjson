package features.snack4.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.json.JsonReader;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式读取测试用例
 *
 * @author noear 2026/2/14 created
 */
public class StreamReadTest {

    @Test
    public void test_readNext() throws Exception {
        // 包含完整 JSON、单引号 JSON、以及非 JSON 后缀
        JsonReader reader = new JsonReader("{\"name\":\"noear\"}{'a':1} [1,2] \"end\"");

        List<ONode> nodes = new ArrayList<>();
        while (true) {
            ONode oNode = reader.readNext();
            if (oNode == null) break;
            nodes.add(oNode);
        }

        Assertions.assertEquals(4, nodes.size());
        Assertions.assertEquals("noear", nodes.get(0).get("name").getString());
        Assertions.assertEquals(1, nodes.get(1).get("a").getInt());
        Assertions.assertTrue(nodes.get(2).isArray());
        Assertions.assertEquals("end", nodes.get(3).getString());
    }

    @Test
    public void test_readLast() throws Exception {
        // 模拟 LLM 输出流：前两个是完整的，最后一个是碎掉的碎片
        String streamContent = "{\"id\":1, \"val\":\"a\"} {\"id\":2, \"val\":\"b\"} {\"id\":3, \"val\":\"c";
        JsonReader reader = new JsonReader(streamContent);

        // readLast 应该跳过 id:1，拿到 id:2，并忽略掉无法解析的 id:3 碎片
        ONode lastNode = reader.readLast();

        Assertions.assertNotNull(lastNode);
        Assertions.assertEquals(2, lastNode.get("id").getInt());
        Assertions.assertEquals("b", lastNode.get("val").getString());

        System.out.println("ReadLast Result: " + lastNode.toJson());
    }

    @Test
    public void test_readLast_onlyOne() throws Exception {
        JsonReader reader = new JsonReader("{\"name\":\"single\"}");
        ONode lastNode = reader.readLast();

        Assertions.assertNotNull(lastNode);
        Assertions.assertEquals("single", lastNode.get("name").getString());
    }

    @Test
    public void test_iterableNext() throws Exception {
        JsonReader reader = new JsonReader("{\"val\":1} {\"val\":2} {\"val\":3}");

        int count = 0;
        for (ONode node : reader.iterableNext()) {
            count++;
            Assertions.assertEquals(count, node.get("val").getInt());
        }

        Assertions.assertEquals(3, count);
    }

    @Test
    public void test_mixed_with_comments() throws Exception {
        // 测试在流式读取中混合注释的处理
        String json = "{\"a\":1} // line comment \n {\"a\":2} /* block \n comment */ {\"a\":3}";
        JsonReader reader = new JsonReader(json, Options.of(Feature.Read_AllowComment));

        int total = 0;
        for (ONode node : reader.iterableNext()) {
            total += node.get("a").getInt(); // 1, 2, 3
        }

        Assertions.assertEquals(6, total);
    }

    @Test
    public void test_partial_content_handling() throws Exception {
        // 模拟彻底的脏数据：第一个合法，后面全乱码
        JsonReader reader = new JsonReader("{\"ok\":true} !@#$%^&*");

        ONode first = reader.readNext();
        Assertions.assertNotNull(first);
        Assertions.assertTrue(first.get("ok").getBoolean());

        // 第二次尝试读取应该因为报错返回 null
        ONode second = reader.readNext();
        Assertions.assertNull(second);
    }
}