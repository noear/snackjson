package features.snack4.jsonschema.generated;

import org.noear.snack4.ONode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.noear.snack4.jsonschema.JsonSchemaConfig;
import org.noear.snack4.jsonschema.SchemaVersion;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonSchemaGenerator 配置选项测试
 */
@DisplayName("JsonSchemaGenerator 配置选项测试")
class JsonSchemaGeneratorConfigTest {

    @Test
    @DisplayName("测试启用版本信息")
    void testWithVersion() {
        ONode schema = JsonSchemaConfig.builder()
                .printVersion(true)
                .build()
                .createSchema(String.class);

        assertTrue(schema.hasKey("$schema"));
        assertEquals(SchemaVersion.DRAFT_7.getIdentifier(), schema.get("$schema").getString());
    }

    @Test
    @DisplayName("测试启用定义")
    void testWithDefinitions() {
        ONode schema = JsonSchemaConfig.builder()
                .enableDefinitions(true)
                .build()
                .createSchema(String.class);

        assertTrue(schema.hasKey("definitions") || schema.hasKey("$defs"));
    }

    @Test
    @DisplayName("测试不同版本模式")
    void testDifferentVersions() {
        for (SchemaVersion version : SchemaVersion.values()) {
            ONode schema = JsonSchemaConfig.builder()
                    .version(version)
                    .printVersion(true)
                    .build()
                    .createSchema(String.class);

            assertEquals(version.getIdentifier(), schema.get("$schema").getString());
        }
    }
}