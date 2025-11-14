package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.noear.snack4.jsonschema.JsonSchema;
import org.noear.snack4.jsonschema.JsonSchemaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 约束验证测试
 */
@DisplayName("JsonSchema 约束验证测试")
class JsonSchemaConstraintTest {

    @Test
    @DisplayName("验证数值范围")
    void testNumberRange() {
        String schemaJson = "{" +
                "\"type\": \"number\"," +
                "\"minimum\": 0," +
                "\"maximum\": 100" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(0)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(50)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(100)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(-1)));
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(101)));
    }

    @Test
    @DisplayName("验证整数范围")
    void testIntegerRange() {
        String schemaJson = "{" +
                "\"type\": \"integer\"," +
                "\"minimum\": -10," +
                "\"maximum\": 10" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(-10)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(0)));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(10)));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(-11)));
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(11)));
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(5.5)));
    }

    @Test
    @DisplayName("验证字符串长度")
    void testStringLength() {
        String schemaJson = "{" +
                "\"type\": \"string\"," +
                "\"minLength\": 2," +
                "\"maxLength\": 5" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("ab")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("abcde")));

        // 无效用例
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("a")));
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("abcdef")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "a1b2c3", "test-123"})
    @DisplayName("验证字符串模式匹配 - 有效用例")
    void testStringPatternValid(String value) {
        String schemaJson = "{" +
                "\"type\": \"string\"," +
                "\"pattern\": \"^[a-z0-9-]+$\"" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(value)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "a b", "test_123"})
    @DisplayName("验证字符串模式匹配 - 无效用例")
    void testStringPatternInvalid(String value) {
        String schemaJson = "{" +
                "\"type\": \"string\"," +
                "\"pattern\": \"^[a-z0-9-]+$\"" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(value)));
    }
}