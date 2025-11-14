package org.noear.snack4.jsonschema.rule;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.JsonSchemaException;
import org.noear.snack4.jsonschema.PathTracker;

/**
 * 数组项验证规则
 */
public class ArrayItemsRule implements ValidationRule {
    private final ONode itemsSchema;

    public ArrayItemsRule(ONode schemaNode) {
        this.itemsSchema = schemaNode.hasKey("items") ? schemaNode.get("items") : null;
    }

    @Override
    public void validate(ONode data) throws JsonSchemaException {
        // 这个规则主要用于标记需要数组项验证
        // 实际的验证在 JsonSchema.validateArrayItems 中完成
    }
}