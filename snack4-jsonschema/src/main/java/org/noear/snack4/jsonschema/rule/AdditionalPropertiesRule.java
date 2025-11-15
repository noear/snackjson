/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.jsonschema.rule;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.JsonSchemaException;
import org.noear.snack4.jsonschema.PathTracker;

/**
 * 额外属性验证规则
 *
 * @author noear
 * @since 4.0
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
    public void validate(ONode data, PathTracker path) throws JsonSchemaException {
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