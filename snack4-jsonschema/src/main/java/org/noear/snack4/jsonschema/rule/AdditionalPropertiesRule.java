package org.noear.snack4.jsonschema.rule;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.JsonSchemaException;

/**
 * 额外属性验证规则
 */
public class AdditionalPropertiesRule implements ValidationRule {
    private final boolean allowAdditional;
    private final ONode additionalSchema;

    public AdditionalPropertiesRule(ONode schemaNode) {
        if (schemaNode.hasKey("additionalProperties")) {
            ONode additionalPropsNode = schemaNode.get("additionalProperties");
            if (additionalPropsNode.isBoolean()) {
                this.allowAdditional = additionalPropsNode.getBoolean();
                this.additionalSchema = null;
            } else {
                this.allowAdditional = true;
                this.additionalSchema = additionalPropsNode;
            }
        } else {
            this.allowAdditional = true;
            this.additionalSchema = null;
        }
    }

    @Override
    public void validate(ONode data) throws JsonSchemaException {
        if (!data.isObject() || allowAdditional) {
            return; // 只验证对象类型且不允许额外属性时
        }

        // 获取当前对象的 schema 节点（通过 parent 获取）
        ONode currentSchema = data.parent();
        if (currentSchema == null || !currentSchema.isObject()) {
            return;
        }

        // 获取定义的属性
        ONode propertiesNode = currentSchema.get("properties");
        if (propertiesNode == null || !propertiesNode.isObject()) {
            return;
        }

        // 检查是否有额外属性
        for (String key : data.getObject().keySet()) {
            if (!propertiesNode.getObject().containsKey(key)) {
                throw new JsonSchemaException("Additional property '" + key + "' is not allowed");
            }
        }
    }
}