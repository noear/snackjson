package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;
import org.noear.snack4.jsonschema.validate.JsonSchemaValidator;
import org.noear.snack4.jsonschema.JsonSchemaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 数组验证测试
 */
@DisplayName("JsonSchema 数组验证测试")
class JsonSchemaArrayValidationTest {

    @Test
    @DisplayName("验证数组类型")
    void testArrayType() {
        String schemaJson = "{\"type\": \"array\"}";
        JsonSchemaValidator schema = JsonSchema.DEFAULT.createValidator(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("[1, 2, 3]")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("[]")));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofJson("{\"not\": \"array\"}")));
    }

    @Test
    @DisplayName("验证数组项")
    void testArrayItems() {
        String schemaJson = "{" +
                "\"type\": \"array\"," +
                "\"items\": {\"type\": \"string\"}" +
                "}";
        JsonSchemaValidator schema = JsonSchema.DEFAULT.createValidator(schemaJson);

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofJson("[\"a\", 123, \"c\"]")));
    }

    @Test
    @DisplayName("验证数组长度约束")
    void testArrayLengthConstraints() {
        String schemaJson = "{" +
                "\"type\": \"array\"," +
                "\"minItems\": 2," +
                "\"maxItems\": 4" +
                "}";
        JsonSchemaValidator schema = JsonSchema.DEFAULT.createValidator(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("[1, 2]")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("[1, 2, 3, 4]")));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofJson("[1]")));
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofJson("[1, 2, 3, 4, 5]")));
    }
}