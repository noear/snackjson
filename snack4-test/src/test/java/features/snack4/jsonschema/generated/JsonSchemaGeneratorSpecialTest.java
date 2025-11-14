package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 特殊类型测试
 */
@DisplayName("JsonSchemaGenerator 特殊类型测试")
class JsonSchemaGeneratorSpecialTest {

    @Test
    @DisplayName("生成URI类型模式")
    void testURIType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(URI.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertEquals("uri", schema.get("format").getString());
    }

    enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }

    @Test
    @DisplayName("生成枚举类型模式")
    void testEnumType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(TestEnum.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertTrue(schema.get("enum").isArray());
        assertEquals(3, schema.get("enum").getArray().size());
        assertTrue(schema.get("enum").getArray().stream()
                .anyMatch(node -> "VALUE1".equals(node.getString())));
    }
}