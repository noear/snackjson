package features.snack4.jsonschema.generated;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchemaException;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 异常情况测试
 */
@DisplayName("JsonSchemaGenerator 异常情况测试")
class JsonSchemaGeneratorExceptionTest {

    @Test
    @DisplayName("测试空类型异常")
    void testNullType() {
        assertThrows(NullPointerException.class, () -> {
            new JsonSchemaGenerator(null);
        });
    }

    @Test
    @DisplayName("测试void类型异常")
    void testVoidType() {
        assertThrows(JsonSchemaException.class, () -> {
            new JsonSchemaGenerator(void.class);
        });
    }
}