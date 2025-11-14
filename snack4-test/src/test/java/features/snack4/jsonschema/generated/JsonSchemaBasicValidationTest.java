package features.snack4.jsonschema.generated;


import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;
import org.noear.snack4.jsonschema.JsonSchemaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 基础验证测试
 */
@DisplayName("JsonSchema 基础验证测试")
class JsonSchemaBasicValidationTest {

    @Test
    @DisplayName("验证字符串类型")
    void testStringValidation() {
        String schemaJson = "{\"type\": \"string\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("hello")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("")));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(123)));
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(true)));
    }

    @Test
    @DisplayName("验证数字类型")
    void testNumberValidation() {
        String schemaJson = "{\"type\": \"number\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(123.45)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(100)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("123")));
    }

    @Test
    @DisplayName("验证整数类型")
    void testIntegerValidation() {
        String schemaJson = "{\"type\": \"integer\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(100)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(0)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(123.45)));
    }

    @Test
    @DisplayName("验证布尔类型")
    void testBooleanValidation() {
        String schemaJson = "{\"type\": \"boolean\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(true)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(false)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("true")));
    }

    @Test
    @DisplayName("验证null类型")
    void testNullValidation() {
        String schemaJson = "{\"type\": \"null\"}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(null)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("null")));
    }

    @Test
    @DisplayName("验证多类型")
    void testMultipleTypes() {
        String schemaJson = "{\"type\": [\"string\", \"number\"]}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("hello")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(123)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(true)));
    }

    @Test
    @DisplayName("验证枚举值")
    void testEnumValidation() {
        String schemaJson = "{\"enum\": [\"red\", \"green\", \"blue\"]}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("red")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("green")));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("yellow")));
    }

    @Test
    @DisplayName("验证混合类型枚举")
    void testMixedTypeEnum() {
        String schemaJson = "{\"enum\": [\"hello\", 42, true, null]}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("hello")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(42)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(true)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(null)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("world")));
    }
}