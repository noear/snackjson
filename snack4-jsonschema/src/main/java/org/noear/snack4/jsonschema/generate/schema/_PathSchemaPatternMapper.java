package org.noear.snack4.jsonschema.generate.schema;

import org.noear.eggg.TypeEggg;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.SchemaFormat;
import org.noear.snack4.jsonschema.SchemaKeyword;
import org.noear.snack4.jsonschema.SchemaType;
import org.noear.snack4.jsonschema.generate.SchemaPatternMapper;

import java.nio.file.Path;

/**
 *
 * @author noear 2026/6/13 created
 *
 */
public class _PathSchemaPatternMapper implements SchemaPatternMapper<Path> {
    @Override
    public boolean supports(TypeEggg typeEggg) {
        return Path.class.isAssignableFrom(typeEggg.getType());
    }

    @Override
    public ONode mapSchema(TypeEggg typeEggg, ONode target) {
        return target.set(SchemaKeyword.TYPE, SchemaType.STRING)
                .set(SchemaKeyword.FORMAT, SchemaFormat.URI);
    }
}
