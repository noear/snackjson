package features.snack4.jsonschema.generated;

import lombok.Data;
import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;
import org.noear.snack4.jsonschema.JsonSchemaException;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator; // 假设路径正确
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator propertyNames 生成与验证测试
 * * 假设：
 * 1. JsonSchema.of(Type) 内部使用了 JsonSchemaGenerator。
 * 2. JsonSchemaGenerator 处理 Map<Integer, T> 时，会在 propertyNames 中添加 { "type": "integer" }。
 */
@DisplayName("JsonSchemaGenerator propertyNames 生成与验证测试")
class JsonSchemaGeneratorPropertyNamesTest {
    @Test
    @DisplayName("Generator: Map<Integer, String> 应生成 propertyNames: {type: integer}")
    void testGeneratorProducesIntKeyConstraint() {
        // 1. 生成 Schema
        ONode schemaNode = new JsonSchemaGenerator(IntKeyMapBean.class).generate();

        // 期望的 propertyNames 约束
        ONode expectedPropertyNames = new ONode().set("type", "integer");

        // 2. 验证生成的 Schema 结构
        assertNotNull(schemaNode, "生成的Schema不应为null");

        // 验证 data 属性的 Schema
        ONode dataSchema = schemaNode.get("properties").get("data");
        assertNotNull(dataSchema, "Schema应包含data属性");
        assertEquals("object", dataSchema.get("type").getString(), "data属性类型应为object");

        // 验证 propertyNames 关键字
        ONode actualPropertyNames = dataSchema.get("propertyNames");
        assertNotNull(actualPropertyNames, "Map<Integer, String>应包含propertyNames约束");
        assertEquals(expectedPropertyNames.toJson(), actualPropertyNames.toJson(), "propertyNames约束应为{type: integer}");

        // 3. 使用生成的 Schema 进行验证
        JsonSchema schema = JsonSchema.ofType(IntKeyMapBean.class);

        // 有效用例：键是数字字符串（符合 integer 约束）
        String validJson = "{\"data\": {\"101\": \"valueA\", \"2\": \"valueB\"}}";
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson(validJson)), "数字字符串键应通过验证");

        // 无效用例：键不是数字字符串（不符合 integer 约束）
        String invalidJson = "{\"data\": {\"101\": \"valueA\", \"key_a\": \"valueC\"}}";
        assertThrows(JsonSchemaException.class, () -> schema.validate(ONode.ofJson(invalidJson)), "非数字字符串键应抛出异常");
    }


    @Test
    @DisplayName("Generator: Map<String, String> 不应生成 propertyNames (或只生成默认约束)")
    void testGeneratorDoesNotProduceSpecificStringKeyConstraint() {
        // 1. 生成 Schema
        ONode schemaNode = new JsonSchemaGenerator(StringKeyMapBean.class).generate();

        // 2. 验证生成的 Schema 结构
        assertNotNull(schemaNode, "生成的Schema不应为null");

        // 验证 data 属性的 Schema
        ONode dataSchema = schemaNode.get("properties").get("data");

        // 验证 propertyNames 关键字
        ONode actualPropertyNames = dataSchema.get("propertyNames");

        // 期望：由于键已经是 String，Generator 应该省略 propertyNames
        // 或者如果 Generator 强制生成，它应该只包含 { "type": "string" }
        if (actualPropertyNames.isObject()) {
            // 如果生成了 propertyNames，检查它是否为默认的 string 类型
            assertEquals("string", actualPropertyNames.get("type").getString(),
                    "Map<String, T>如果生成propertyNames，应为{type: string}");
        } else {
            // 更好的做法是完全省略该关键字
            assertNull(actualPropertyNames.getValue(), "Map<String, String>不应生成propertyNames约束");
        }

        // 3. 使用生成的 Schema 进行验证
        JsonSchema schema = JsonSchema.ofType(StringKeyMapBean.class);

        // 有效用例：任意字符串键都应通过验证
        String validJson = "{\"data\": {\"any-key-1\": \"valueA\", \"222\": \"valueB\"}}";
        assertDoesNotThrow(() -> schema.validate(ONode.ofJson(validJson)), "任意字符串键应通过验证");
    }

    // 模拟一个带有 Integer 键 Map 的数据结构
    static class IntKeyMapBean {
        public Map<Integer, String> data;
    }

    // 模拟一个带有 String 键 Map 的数据结构
    static class StringKeyMapBean {
        public Map<String, String> data;
    }

    // 假设这是您的业务代码
    @Data
    static class ConfigData {
        // 期望：生成 Schema 时，propertyNames 约束键必须是 integer
        public Map<Integer, String> intKeyConfig;

        // 期望：生成 Schema 时，无需 propertyNames 约束（或只约束为 string）
        public Map<String, String> stringKeyConfig;

        // 假设您使用 Lombok 或类似的工具生成 getter/setter
        // 为了简化测试，我们只使用 public 字段
    }
}