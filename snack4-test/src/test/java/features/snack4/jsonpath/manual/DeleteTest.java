package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ONode delete方法测试类
 */
public class DeleteTest {

    @Test
    public void testDeleteSimpleProperty() {
        // 测试删除简单属性
        ONode root = new ONode().set("name", "John").set("age", 30);
        root.delete("$.name");
        
        assertFalse(root.hasKey("name"));
        assertTrue(root.hasKey("age"));
        assertEquals(30, root.get("age").getInt());
    }

    @Test
    public void testDeleteNestedProperty() {
        // 测试删除嵌套属性
        ONode root = new ONode()
            .set("user", new ONode()
                .set("name", "John")
                .set("age", 30)
                .set("address", new ONode()
                    .set("city", "Beijing")
                    .set("street", "Main St")));
        
        root.delete("$.user.address.city");
        
        assertTrue(root.hasKey("user"));
        assertTrue(root.get("user").hasKey("name"));
        assertTrue(root.get("user").hasKey("address"));
        assertFalse(root.get("user").get("address").hasKey("city"));
        assertTrue(root.get("user").get("address").hasKey("street"));
    }

    @Test
    public void testDeleteArrayElementByIndex() {
        // 测试通过索引删除数组元素
        ONode root = new ONode()
            .set("users", new ONode()
                .add(new ONode().set("name", "John"))
                .add(new ONode().set("name", "Jane"))
                .add(new ONode().set("name", "Bob")));
        
        root.delete("$.users[1]");
        
        assertEquals(2, root.get("users").size());
        assertEquals("John", root.get("users").get(0).get("name").getString());
        assertEquals("Bob", root.get("users").get(1).get("name").getString());
    }

    @Test
    public void testDeleteArrayElementByNegativeIndex() {
        // 测试通过负索引删除数组元素
        ONode root = new ONode()
            .set("users", new ONode()
                .add(new ONode().set("name", "John"))
                .add(new ONode().set("name", "Jane"))
                .add(new ONode().set("name", "Bob")));
        
        root.delete("$.users[-1]"); // 删除最后一个元素
        
        assertEquals(2, root.get("users").size());
        assertEquals("John", root.get("users").get(0).get("name").getString());
        assertEquals("Jane", root.get("users").get(1).get("name").getString());
    }

    @Test
    public void testDeleteWithWildcard() {
        // 测试使用通配符删除
        ONode root = new ONode()
            .set("users", new ONode()
                .add(new ONode().set("name", "John").set("age", 30))
                .add(new ONode().set("name", "Jane").set("age", 25)));
        
        root.delete("$.users[*].age");
        
        assertEquals(2, root.get("users").size());
        assertFalse(root.get("users").get(0).hasKey("age"));
        assertFalse(root.get("users").get(1).hasKey("age"));
        assertTrue(root.get("users").get(0).hasKey("name"));
        assertTrue(root.get("users").get(1).hasKey("name"));
    }

    @Test
    public void testDeleteRecursive() {
        // 测试递归删除
        ONode root = new ONode()
            .set("store", new ONode()
                .set("books", new ONode()
                    .add(new ONode().set("title", "Book1").set("price", 10))
                    .add(new ONode().set("title", "Book2").set("price", 20)))
                .set("magazines", new ONode()
                    .add(new ONode().set("title", "Mag1").set("price", 5))));
        
        root.delete("$..price");
        
        assertFalse(root.get("store").get("books").get(0).hasKey("price"));
        assertFalse(root.get("store").get("books").get(1).hasKey("price"));
        assertFalse(root.get("store").get("magazines").get(0).hasKey("price"));
        assertTrue(root.get("store").get("books").get(0).hasKey("title"));
    }

    @Test
    public void testDeleteNonExistentPath() {
        // 测试删除不存在的路径
        ONode root = new ONode().set("name", "John");
        
        assertDoesNotThrow(() -> root.delete("$.age")); // 不应该抛出异常
        assertEquals("John", root.get("name").getString());
        assertEquals(1, root.size());
    }

    @Test
    public void testDeleteRootProperty() {
        // 测试删除根属性
        ONode root = new ONode().set("temp", "value");
        
        root.delete("$.temp");
        
        assertFalse(root.hasKey("temp"));
        assertEquals(0, root.size());
    }

    @Test
    public void testDeleteEntireArray() {
        // 测试删除整个数组
        ONode root = new ONode()
            .set("data", new ONode().add("item1").add("item2").add("item3"));
        
        root.delete("$.data");
        
        assertFalse(root.hasKey("data"));
        assertEquals(0, root.size());
    }

    @Test
    public void testDeleteWithFilter() {
        // 测试带过滤器的删除
        ONode root = new ONode()
            .set("users", new ONode()
                .add(new ONode().set("name", "John").set("age", 30))
                .add(new ONode().set("name", "Jane").set("age", 25))
                .add(new ONode().set("name", "Bob").set("age", 35)));
        
        // 注意：这里我们测试一个简单的过滤器场景
        // 实际的过滤器支持可能依赖于具体实现
        root.delete("$.users[?(@.age == 25)]");
        
        // 验证Jane被删除了（age=25）
        assertEquals(2, root.get("users").size());
        assertEquals("John", root.get("users").get(0).get("name").getString());
        assertEquals("Bob", root.get("users").get(1).get("name").getString());
    }
}