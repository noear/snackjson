package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.SchemaVersion;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 配置选项测试
 */
@DisplayName("JsonSchemaGenerator 配置选项测试")
class JsonSchemaGeneratorConfigTest {

    @Test
    @DisplayName("测试启用版本信息")
    void testWithVersion() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(String.class)
                .withPrintVersion(true);
        ONode schema = generator.generate();

        assertTrue(schema.hasKey("$schema"));
        assertEquals(SchemaVersion.DRAFT_7.getIdentifier(), schema.get("$schema").getString());
    }

    @Test
    @DisplayName("测试启用定义")
    void testWithDefinitions() {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(String.class)
                .withEnableDefinitions(true);
        ONode schema = generator.generate();

        assertTrue(schema.hasKey("definitions") || schema.hasKey("$defs"));
    }

    @Test
    @DisplayName("测试不同版本模式")
    void testDifferentVersions() {
        for (SchemaVersion version : SchemaVersion.values()) {
            JsonSchemaGenerator generator = new JsonSchemaGenerator(String.class)
                    .withVersion(version)
                    .withPrintVersion(true);
            ONode schema = generator.generate();

            assertEquals(version.getIdentifier(), schema.get("$schema").getString());
        }
    }
}