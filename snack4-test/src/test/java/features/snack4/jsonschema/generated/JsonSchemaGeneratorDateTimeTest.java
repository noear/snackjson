package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 时间类型测试
 */
@DisplayName("JsonSchemaGenerator 时间类型测试")
class JsonSchemaGeneratorDateTimeTest {

    @Test
    @DisplayName("生成LocalDate类型模式")
    void testLocalDateType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(LocalDate.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertEquals("date", schema.get("format").getString());
    }

    @Test
    @DisplayName("生成LocalDateTime类型模式")
    void testLocalDateTimeType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(LocalDateTime.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertEquals("date-time", schema.get("format").getString());
    }

    @Test
    @DisplayName("生成LocalTime类型模式")
    void testLocalTimeType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(LocalTime.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertEquals("time", schema.get("format").getString());
    }

    @Test
    @DisplayName("生成Date类型模式")
    void testDateType() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(Date.class);
        ONode schema = generator.generate();

        assertEquals("string", schema.get("type").getString());
        assertEquals("date-time", schema.get("format").getString());
    }
}