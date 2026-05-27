package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.*;


public class JsonPathSubPathTest {

    // ---- subPath 基本场景 ----

    @Test
    public void test_basic_subPath() {
        JsonPath path = JsonPath.parse("$.o['j j']['k.k']");
        assertEquals(3, path.getSegmentCount());

        assertEquals("$.o", path.subPath(1).getExpression());
        assertEquals("$.o['j j']", path.subPath(2).getExpression());
        assertEquals("$.o['j j']['k.k']", path.subPath(3).getExpression());
    }

    @Test
    public void test_simple_dot_notation() {
        JsonPath path = JsonPath.parse("$.store.book");
        assertEquals(2, path.getSegmentCount());

        assertEquals("$.store", path.subPath(1).getExpression());
        assertEquals("$.store.book", path.subPath(2).getExpression());
    }

    @Test
    public void test_array_index() {
        JsonPath path = JsonPath.parse("$.orders[0].price");
        assertEquals(3, path.getSegmentCount());

        assertEquals("$.orders", path.subPath(1).getExpression());
        assertEquals("$.orders[0]", path.subPath(2).getExpression());
        assertEquals("$.orders[0].price", path.subPath(3).getExpression());
    }

    @Test
    public void test_wildcard() {
        JsonPath path = JsonPath.parse("$.store[*].name");
        assertEquals(3, path.getSegmentCount());

        assertEquals("$.store", path.subPath(1).getExpression());
        assertEquals("$.store[*]", path.subPath(2).getExpression());
    }

    @Test
    public void test_function() {
        JsonPath path = JsonPath.parse("$.list.length()");
        assertEquals(2, path.getSegmentCount());

        assertEquals("$.list", path.subPath(1).getExpression());
        assertEquals("$.list.length()", path.subPath(2).getExpression());
    }

    @Test
    public void test_relative_path() {
        JsonPath path = JsonPath.parse("@.a.b");
        assertEquals(2, path.getSegmentCount());

        assertEquals("@.a", path.subPath(1).getExpression());
        assertEquals("@.a.b", path.subPath(2).getExpression());
    }

    @Test
    public void test_descendant() {
        JsonPath path = JsonPath.parse("$..name");
        assertEquals(2, path.getSegmentCount());

        assertEquals("$..name", path.subPath(2).getExpression());
    }

    @Test
    public void test_descendant_with_bracket() {
        JsonPath path = JsonPath.parse("$..['name']");
        assertEquals(2, path.getSegmentCount());

        assertEquals("$..['name']", path.subPath(2).getExpression());
    }

    @Test
    public void test_descendant_deep() {
        JsonPath path = JsonPath.parse("$.store..price");
        assertEquals(3, path.getSegmentCount());

        assertEquals("$.store", path.subPath(1).getExpression());
        assertEquals("$.store..price", path.subPath(3).getExpression());
    }

    // ---- 边界检查 ----

    @Test
    public void test_descendant_cannot_be_last() {
        JsonPath path = JsonPath.parse("$..name");
        assertThrows(IllegalArgumentException.class, () -> path.subPath(1));
    }

    @Test
    public void test_invalid_level_zero() {
        JsonPath path = JsonPath.parse("$.a.b");
        assertThrows(IllegalArgumentException.class, () -> path.subPath(0));
    }

    @Test
    public void test_invalid_level_exceeds() {
        JsonPath path = JsonPath.parse("$.a.b");
        assertThrows(IllegalArgumentException.class, () -> path.subPath(3));
    }

    // ---- 功能一致性验证 ----

    @Test
    public void test_subPath_select_consistency() {
        String json = "{'o':{'j j':{'k.k':42}}}";
        ONode root = ONode.ofJson(json);

        JsonPath full = JsonPath.parse("$.o['j j']['k.k']");
        int expected = full.select(root).asNode().getInt();

        JsonPath sub = full.subPath(2);
        int actual = sub.select(root).asNode().get("k.k").getInt();

        assertEquals(expected, actual);
    }

    @Test
    public void test_subPath_select_simple() {
        String json = "{'store':{'book':[{'title':'A'},{'title':'B'}]}}";
        ONode root = ONode.ofJson(json);

        JsonPath full = JsonPath.parse("$.store.book[0].title");
        String expected = full.select(root).asNode().getString();

        // 用 subPath(1) 拿到 store 节点
        JsonPath sub1 = full.subPath(1);
        ONode storeNode = sub1.select(root).asNode();
        assertNotNull(storeNode);

        // 用 subPath(2) 拿到 book 数组
        JsonPath sub2 = full.subPath(2);
        ONode bookArray = sub2.select(root).asNode();
        assertNotNull(bookArray);

        // 完整路径查 title
        assertEquals("A", expected);
    }

    @Test
    public void test_subPath_same_as_original() {
        JsonPath path = JsonPath.parse("$.a.b.c");
        JsonPath sub = path.subPath(path.getSegmentCount());
        assertEquals(path.getExpression(), sub.getExpression());
    }

    // ---- getOriginalText 验证 ----

    @Test
    public void test_original_text_segments() {
        JsonPath path = JsonPath.parse("$.o['j j']['k.k']");

        assertEquals(".o", path.getSegments().get(0).getOriginalText());
        assertEquals("['j j']", path.getSegments().get(1).getOriginalText());
        assertEquals("['k.k']", path.getSegments().get(2).getOriginalText());
    }

    @Test
    public void test_original_text_bracket() {
        JsonPath path = JsonPath.parse("$[0]");

        assertEquals("[0]", path.getSegments().get(0).getOriginalText());
    }

    @Test
    public void test_original_text_function() {
        JsonPath path = JsonPath.parse("$.list.length()");

        assertEquals(".list", path.getSegments().get(0).getOriginalText());
        assertEquals(".length()", path.getSegments().get(1).getOriginalText());
    }

    @Test
    public void test_original_text_wildcard() {
        JsonPath path = JsonPath.parse("$[*]");

        assertEquals("[*]", path.getSegments().get(0).getOriginalText());
    }

    @Test
    public void test_original_text_descendant() {
        JsonPath path = JsonPath.parse("$..name");

        assertEquals("..", path.getSegments().get(0).getOriginalText());
        assertEquals(".name", path.getSegments().get(1).getOriginalText());
    }
}
