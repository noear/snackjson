package org.noear.snack4.jsonschema.rule;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.JsonSchemaException;
import org.noear.snack4.jsonschema.PathTracker;
import org.noear.snack4.jsonschema.SchemaKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * propertyNames 约束验证规则
 *
 * @author noear
 * @since 4.0
 */
public class PropertyNamesRule implements ValidationRule {
    // 内部编译好的针对属性名的规则集
    private final List<ValidationRule> compiledNameRules = new ArrayList<>();

    public PropertyNamesRule(ONode schemaNode) {
        ONode propertyNamesSchema = schemaNode.get(SchemaKeyword.PROPERTY_NAMES);

        // 1. 类型规则（通常是 type: string）
        if (propertyNamesSchema.hasKey(SchemaKeyword.TYPE)) {
            compiledNameRules.add(new TypeRule(propertyNamesSchema.get(SchemaKeyword.TYPE)));
        }

        // 2. 字符串约束规则（minLength, maxLength, pattern, format）
        if (propertyNamesSchema.hasKey(SchemaKeyword.MIN_LENGTH) ||
                propertyNamesSchema.hasKey(SchemaKeyword.MAX_LENGTH) ||
                propertyNamesSchema.hasKey(SchemaKeyword.PATTERN) ||
                propertyNamesSchema.hasKey(SchemaKeyword.FORMAT)) { // 增加 format 检查

            // 依赖于 StringConstraintRule 已经支持 format 的前提
            compiledNameRules.add(new StringConstraintRule(propertyNamesSchema));
        }

        // 理论上 propertyNames 可以包含任何规则，但实际应用中通常只有类型和字符串约束
        // 如果需要支持更复杂的规则（如 enum, allOf, anyOf），则必须使用 JsonSchema 的递归编译方法
        // 为了简化和解耦，这里只支持 Type 和 String 约束。
    }

    @Override
    public void validate(ONode data, PathTracker path) throws JsonSchemaException {
        if (!data.isObject()) {
            return;
        }

        if (compiledNameRules.isEmpty()) {
            // 没有约束，直接通过
            return;
        }

        // 迭代对象的所有键名
        for (String propName : data.getObject().keySet()) {
            // 将属性名称包装成一个 String ONode，以便可以对其应用 ValidationRule
            ONode nameNode = ONode.ofBean(propName);

            // 当前路径（仅用于错误提示，不用于递归进入）
            String currentPath = path.currentPath();

            try {
                // 对所有编译好的属性名规则进行验证
                for (ValidationRule rule : compiledNameRules) {
                    // 对键名进行验证
                    rule.validate(nameNode, path);
                }
            } catch (JsonSchemaException e) {
                // 如果任何键名验证失败，抛出新的异常，提供详细的上下文
                // 关键是提供是哪个属性名失败了
                throw new JsonSchemaException(
                        "Property name '" + propName + "' failed validation. " + e.getMessage(),
                        currentPath,
                        propName
                );
            }
        }
    }

    @Override
    public String toString() {
        return "PropertyNamesRule{" +
                "rules=" + compiledNameRules.stream().map(ValidationRule::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}