package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 集合类型测试
 */
@DisplayName("JsonSchemaGenerator 集合类型测试")
class JsonSchemaGeneratorCollectionTest {

    @Test
    @DisplayName("生成List类型模式")
    void testListType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(List.class);
        ONode schema = generator.generate();

        assertEquals("array", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成Set类型模式")
    void testSetType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Set.class);
        ONode schema = generator.generate();

        assertEquals("array", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成数组类型模式")
    void testArrayType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(String[].class);
        ONode schema = generator.generate();

        assertEquals("array", schema.get("type").getString());
        assertTrue(schema.get("items").isObject());
        assertEquals("string", schema.get("items").get("type").getString());
    }

    @Test
    @DisplayName("生成Map类型模式")
    void testMapType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Map.class);
        ONode schema = generator.generate();

        assertEquals("object", schema.get("type").getString());
        assertTrue(schema.get("additionalProperties").getBoolean());
    }

    @Test
    @DisplayName("生成参数化List类型模式")
    void testParameterizedListType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(new ArrayList<String>(){}.getClass().getGenericSuperclass());
        ONode schema = generator.generate();

        assertEquals("array", schema.get("type").getString());
    }
}