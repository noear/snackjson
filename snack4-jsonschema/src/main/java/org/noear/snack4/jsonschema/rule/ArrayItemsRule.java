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

/**
 * 数组项验证规则
 *
 * @author noear
 * @since 4.0
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