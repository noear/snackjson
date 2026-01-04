package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.encode.StringEncoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author noear 2026/1/3 created
 *
 */
public class MarkingDemo {
    public void demo() {
        Options options = Options.of();
        options.addEncoder(String.class, new StringEncoder() {
            @Override
            public ONode encode(EncodeContext ctx, String value, ONode target) {
                //if (ctx.getAttr().isMasking()) {
                    //打码（脱每处理）
                    //return target.setValue(value.replace("xxx", "***"));
                //}

                return super.encode(ctx, value, target);
            }
        });

        ONode.serialize(new User(), options);
    }

    public static class User {
        String name = "aaa";

        //@ONodeAttr(masking = true)
        String password = "xxx";
    }
}
