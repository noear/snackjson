package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchemaConfig;
import org.noear.snack4.jsonschema.validate.JsonSchemaValidator;
import org.noear.snack4.jsonschema.JsonSchemaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 对象验证测试
 */
@DisplayName("JsonSchema 对象验证测试")
class JsonSchemaObjectValidationTest {

    @Test
    @DisplayName("验证对象属性")
    void testObjectProperties() {
        String schemaJson = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "  \"name\": {\"type\": \"string\"}," +
                "  \"age\": {\"type\": \"integer\"}" +
                "}" +
                "}";
        JsonSchemaValidator schema = JsonSchemaConfig.DEFAULT.createValidator(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("{\"name\": \"John\", \"age\": 30}")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("{\"name\": \"John\"}"))); // age 可选

        // 无效用例
        assertThrows(JsonSchemaException.class, () ->
                schema.validate(ONode.ofJson("{\"name\": \"John\", \"age\": \"thirty\"}")));
    }

    @Test
    @DisplayName("验证必需属性")
    void testRequiredProperties() {
        String schemaJson = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "  \"name\": {\"type\": \"string\"}," +
                "  \"age\": {\"type\": \"integer\"}" +
                "}," +
                "\"required\": [\"name\"]" +
                "}";
        JsonSchemaValidator schema = JsonSchemaConfig.DEFAULT.createValidator(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("{\"name\": \"John\"}")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("{\"name\": \"John\", \"age\": 30}")));

        // 无效用例 - 缺少必需属性
        assertThrows(JsonSchemaException.class, () ->
                schema.validate(ONode.ofJson("{\"age\": 30}")));
    }

    @Test
    @DisplayName("验证additionalProperties")
    void testAdditionalProperties() {
        String schemaJson = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "  \"name\": {\"type\": \"string\"}" +
                "}," +
                "\"additionalProperties\": false" +
                "}";
        JsonSchemaValidator schema = JsonSchemaConfig.DEFAULT.createValidator(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson("{\"name\": \"John\"}")));

        // 无效用例 - 有额外属性
        assertThrows(JsonSchemaException.class, () ->
                schema.validate(ONode.ofJson("{\"name\": \"John\", \"age\": 30}")));
    }
}