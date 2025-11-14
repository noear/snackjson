package org.noear.snack4.jsonschema.codec.encode;

import org.noear.eggg.TypeEggg;
import org.noear.snack4.ONode;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.jsonschema.codec.SchemaUtil;
import org.noear.snack4.jsonschema.codec.TypeSchemaBuilder;

/**
 *
 * @author noear 2025/11/14 created
 *
 */
public class BooleanSchemaBuilder implements TypeSchemaBuilder {
    private static final BooleanSchemaBuilder _instance = new BooleanSchemaBuilder();
    public static BooleanSchemaBuilder getInstance() {
        return _instance;
    }

    @Override
    public ONode encode(ONodeAttrHolder att, TypeEggg typeEggg, ONode target) {
        return target.set("type", SchemaUtil.TYPE_BOOLEAN);
    }
}
