package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchema 异常情况测试
 */
@DisplayName("JsonSchema 异常情况测试")
class JsonSchemaExceptionTest {

    @Test
    @DisplayName("测试无效模式格式")
    void testInvalidSchemaFormat() {
        // 非对象模式
        assertThrows(IllegalArgumentException.class, () ->
                JsonSchema.ofJson("\"not an object\""));

        // 空字符串
        assertThrows(IllegalArgumentException.class, () ->
                JsonSchema.ofJson(""));

        // 无效JSON
        assertThrows(RuntimeException.class, () ->
                JsonSchema.ofJson("{invalid json}"));
    }

    @Test
    @DisplayName("测试空类型")
    void testNullType() {
        assertThrows(NullPointerException.class, () ->
                JsonSchema.ofType(null));
    }

    @Test
    @DisplayName("测试工厂方法")
    void testFactoryMethods() {
        String schemaJson = "{\"type\": \"string\"}";
        JsonSchema schema1 = JsonSchema.ofJson(schemaJson);
        JsonSchema schema2 = JsonSchema.ofNode(ONode.ofJson(schemaJson));
        JsonSchema schema3 = JsonSchema.ofType(String.class);

        assertNotNull(schema1);
        assertNotNull(schema2);
        assertNotNull(schema3);

        // 验证所有schema都能正常工作
        assertDoesNotThrow(() -> schema1.validate(ONode.ofBean("test")));
        assertDoesNotThrow(() -> schema2.validate(ONode.ofBean("test")));
        assertDoesNotThrow(() -> schema3.validate(ONode.ofBean("test")));
    }
}