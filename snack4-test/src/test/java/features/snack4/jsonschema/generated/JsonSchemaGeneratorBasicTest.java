package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchemaConfig;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 基础类型测试
 */
@DisplayName("JsonSchemaGenerator 基础类型测试")
class JsonSchemaGeneratorBasicTest {

    @Test
    @DisplayName("生成字符串类型模式")
    void testStringType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(String.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成整数类型模式")
    void testIntegerType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Integer.class);
        ONode schema = generator.generate();

        assertEquals("integer", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成长整数类型模式")
    void testLongType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Long.class);
        ONode schema = generator.generate();

        assertEquals("integer", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成浮点数类型模式")
    void testDoubleType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Double.class);
        ONode schema = generator.generate();

        assertEquals("number", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成布尔类型模式")
    void testBooleanType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Boolean.class);
        ONode schema = generator.generate();

        assertEquals("boolean", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成字符类型模式")
    void testCharType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Character.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertEquals(1, schema.get("minLength").getInt());
        assertEquals(1, schema.get("maxLength").getInt());
    }

    @Test
    @DisplayName("生成BigInteger类型模式")
    void testBigIntegerType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(BigInteger.class);
        ONode schema = generator.generate();

        assertEquals("integer", schema.get("type").getString());
    }

    @Test
    @DisplayName("生成BigDecimal类型模式")
    void testBigDecimalType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(BigDecimal.class);
        ONode schema = generator.generate();

        assertEquals("number", schema.get("type").getString());
    }
}