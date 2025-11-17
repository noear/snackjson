package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchemaConfig;
import org.noear.snack4.jsonschema.validate.JsonSchemaValidator;

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
                JsonSchemaConfig.DEFAULT.createValidator("\"not an object\""));

        // 空字符串
        assertThrows(IllegalArgumentException.class, () ->
                JsonSchemaConfig.DEFAULT.createValidator(""));

        // 无效JSON
        assertThrows(RuntimeException.class, () ->
                JsonSchemaConfig.DEFAULT.createValidator("{invalid json}"));
    }

    @Test
    @DisplayName("测试空类型")
    void testNullType() {
        assertThrows(NullPointerException.class, () ->
                JsonSchemaConfig.DEFAULT.createValidator((Class)null));
    }

    @Test
    @DisplayName("测试工厂方法")
    void testFactoryMethods() {
        String schemaJson = "{\"type\": \"string\"}";
        JsonSchemaValidator schema1 = JsonSchemaConfig.DEFAULT.createValidator(schemaJson);
        JsonSchemaValidator schema2 = JsonSchemaConfig.DEFAULT.createValidator(ONode.ofJson(schemaJson));
        JsonSchemaValidator schema3 = JsonSchemaConfig.DEFAULT.createValidator(String.class);

        assertNotNull(schema1);
        assertNotNull(schema2);
        assertNotNull(schema3);

        // 验证所有schema都能正常工作
        assertDoesNotThrow(() -> schema1.validate(ONode.ofBean("test")));
        assertDoesNotThrow(() -> schema2.validate(ONode.ofBean("test")));
        assertDoesNotThrow(() -> schema3.validate(ONode.ofBean("test")));
    }
}