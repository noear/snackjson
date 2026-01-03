package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author noear 2026/1/3 created
 *
 */
public class AnnotationDemo {
    public void demo() {
        Options options = Options.of();
        options.addEncoder(new ObjectPatternEncoder<Object>() {
            @Override
            public boolean canEncode(Object value) {
                return value.getClass().isAnnotationPresent(Anno1.class);
            }

            @Override
            public ONode encode(EncodeContext ctx, Object value, ONode target) {
                //对 value 做处理
                return null;
            }
        });

        ONode.serialize("", options);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public static @interface Anno1 {

    }
}
