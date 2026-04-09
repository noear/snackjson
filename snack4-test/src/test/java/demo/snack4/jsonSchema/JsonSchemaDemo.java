package demo.snack4.jsonSchema;

import demo.snack4._models.BookModel;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonschema.JsonSchema;

/**
 *
 * @author noear 2026/4/9 created
 *
 */
public class JsonSchemaDemo {
    public void case1(){
        JsonSchema jsonSchema = JsonSchema.builder().build();
        //jsonSchema.addSchemaMapper();
        //jsonSchema.addTypeMapper();

        //ONode jsonSchemaNode =  jsonSchema.createGenerator(BookModel.class).generate();
        ONode jsonSchemaNode =  jsonSchema.generate(BookModel.class);

        //jsonSchema.createValidator(BookModel.class).validate(ONode.ofJson("{}"));
        //jsonSchema.createValidator(jsonSchemaNode).validate(ONode.ofJson("{}"));

        jsonSchema.validate(BookModel.class, ONode.ofJson("{}"));
        jsonSchema.validate(jsonSchemaNode, ONode.ofJson("{}"));
    }
}
