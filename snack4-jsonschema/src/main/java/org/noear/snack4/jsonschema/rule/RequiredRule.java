package org.noear.snack4.jsonschema.rule;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.JsonSchemaException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 必需字段验证规则
 */
public class RequiredRule implements ValidationRule {
    private final List<String> requiredFields;

    public RequiredRule(ONode requiredNode) {
        this.requiredFields = requiredNode.getArray()
                .stream()
                .map(ONode::getString)
                .collect(Collectors.toList());
    }

    @Override
    public void validate(ONode data) throws JsonSchemaException {
        if (!data.isObject()) {
            return; // 只验证对象类型
        }

        for (String field : requiredFields) {
            if (!data.getObject().containsKey(field)) {
                throw new JsonSchemaException("Missing required field: " + field);
            }
        }
    }
}