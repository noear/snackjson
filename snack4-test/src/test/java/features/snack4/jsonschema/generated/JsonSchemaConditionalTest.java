package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;
import org.noear.snack4.jsonschema.JsonSchemaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 条件验证测试
 */
@DisplayName("JsonSchema 条件验证测试")
class JsonSchemaConditionalTest {

    @Test
    @DisplayName("验证anyOf条件")
    void testAnyOf() {
        String schemaJson = "{" +
                "\"anyOf\": [" +
                "  {\"type\": \"string\"}," +
                "  {\"type\": \"number\"}" +
                "]" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例 - 满足任一条件
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("hello")));
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean(123)));

        // 无效用例 - 不满足任何条件
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(true)));
    }

    @Test
    @DisplayName("验证allOf条件")
    void testAllOf() {
        String schemaJson = "{" +
                "\"allOf\": [" +
                "  {\"type\": \"string\"}," +
                "  {\"minLength\": 3}" +
                "]" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例 - 满足所有条件
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("abc")));

        // 无效用例 - 不满足所有条件
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("ab"))); // 长度不足
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(123))); // 类型错误
    }

    @Test
    @DisplayName("验证oneOf条件")
    void testOneOf() {
        String schemaJson = "{" +
                "\"oneOf\": [" +
                "  {\"type\": \"string\", \"maxLength\": 5}," +
                "  {\"type\": \"string\", \"minLength\": 10}" +
                "]" +
                "}";
        JsonSchema schema = JsonSchema.ofJson(schemaJson);

        // 有效用例 - 恰好满足一个条件
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("short"))); // 第一个条件
        assertDoesNotThrow(() -> schema.validate(ONode.ofBean("very long string"))); // 第二个条件

        // 无效用例 - 满足多个条件或不满足任何条件
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean("medium"))); // 两个条件都不满足
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofBean(123))); // 类型错误
    }
}