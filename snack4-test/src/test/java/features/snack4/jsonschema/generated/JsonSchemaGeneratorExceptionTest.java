package features.snack4.jsonschema.generated;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchema;
import org.noear.snack4.jsonschema.JsonSchemaException;

import java.lang.reflect.Type;

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
            JsonSchema.DEFAULT.createValidator((Type) null);
        });
    }

    @Test
    @DisplayName("测试void类型异常")
    void testVoidType() {
        assertThrows(JsonSchemaException.class, () -> {
            JsonSchema.DEFAULT.createValidator(void.class);
        });
    }
}