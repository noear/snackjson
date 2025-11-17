package org.noear.snack4.jsonschema;

import org.noear.eggg.TypeEggg;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.generate.JsonSchemaGenerator;
import org.noear.snack4.jsonschema.validate.JsonSchemaValidator;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 *
 * @author noear 2025/11/17 created
 * @since 4.0
 */
public class JsonSchemaConfig {
    public static final JsonSchemaConfig DEFAULT = JsonSchemaConfig.builder().build();

    private final SchemaVersion version;
    private final boolean enableDefinitions;
    private final boolean printVersion;

    public JsonSchemaConfig(SchemaVersion version, boolean enableDefinitions, boolean printVersion) {
        this.version = version;
        this.enableDefinitions = enableDefinitions;
        this.printVersion = printVersion;
    }

    public SchemaVersion getVersion() {
        return version;
    }

    public boolean isEnableDefinitions() {
        return enableDefinitions;
    }

    public boolean isPrintVersion() {
        return printVersion;
    }

    /**
     * 生成架构
     *
     */
    public ONode createSchema(Type type) {
        return new JsonSchemaGenerator(type, this).generate();
    }

    /**
     * 生成架构
     *
     */
    public ONode createSchema(TypeEggg type) {
        return new JsonSchemaGenerator(type, this).generate();
    }


    /**
     * 生成验证器
     */
    public JsonSchemaValidator createValidator(String jsonSchema) {
        if (Asserts.isEmpty(jsonSchema)) {
            throw new IllegalArgumentException("jsonSchema is empty");
        }
        return new JsonSchemaValidator(ONode.ofJson(jsonSchema));
    }

    /**
     * 生成验证器
     */
    public JsonSchemaValidator createValidator(ONode jsonSchema) {
        Objects.requireNonNull(jsonSchema, "jsonSchema");
        if (jsonSchema.isObject() == false) {
            throw new IllegalArgumentException("jsonSchema is invalid");
        }

        return new JsonSchemaValidator(jsonSchema);
    }

    /**
     * 生成验证器
     */
    public JsonSchemaValidator createValidator(Type type) {
        Objects.requireNonNull(type, "type");

        ONode oNode = createSchema(type);
        if (oNode == null) {
            throw new JsonSchemaException("The type jsonSchema generation failed: " + type.toString());
        }

        return new JsonSchemaValidator(oNode);
    }

    /**
     * 生成验证器
     */
    public JsonSchemaValidator createValidator(TypeEggg typeEggg) {
        Objects.requireNonNull(typeEggg, "typeEggg");

        ONode oNode = createSchema(typeEggg);
        if (oNode == null) {
            throw new JsonSchemaException("The type jsonSchema generation failed: " + typeEggg.toString());
        }

        return new JsonSchemaValidator(oNode);
    }


    /// ///////////////////

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SchemaVersion version = SchemaVersion.DRAFT_7;
        private boolean enableDefinitions;
        private boolean printVersion;

        public Builder version(SchemaVersion version) {
            this.version = version;
            return this;
        }

        public Builder enableDefinitions(boolean enableDefinitions) {
            this.enableDefinitions = enableDefinitions;
            return this;
        }

        public Builder printVersion(boolean printVersion) {
            this.printVersion = printVersion;
            return this;
        }

        public JsonSchemaConfig build() {
            return new JsonSchemaConfig(version, enableDefinitions, printVersion);
        }
    }
}