package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 边界情况测试
 */
@DisplayName("JsonSchema 边界情况测试")
class JsonSchemaEdgeCaseTest {

    @Test
    @DisplayName("验证空对象")
    void testEmptyObject() {
        String schemaJson = "{\"type\": \"object\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("{}")));
    }

    @Test
    @DisplayName("验证空数组")
    void testEmptyArray() {
        String schemaJson = "{\"type\": \"array\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("[]")));
    }

    @Test
    @DisplayName("验证空字符串")
    void testEmptyString() {
        String schemaJson = "{\"type\": \"string\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("")));
    }

    @Test
    @DisplayName("验证零值")
    void testZeroValue() {
        String schemaJson = "{\"type\": \"number\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(0)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(0.0)));
    }
}