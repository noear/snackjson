package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.encode.StringEncoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 *
 * @author noear 2026/1/3 created
 *
 */
public class MarkingTest {
    @Test
    public void case1() {
        Options options = Options.of().addEncoder(String.class, new StringEncoder() {
            @Override
            public ONode encode(EncodeContext ctx, String value, ONode target) {
                if (ctx.getElement() != null) {
                    Marking anno = ctx.getAnnotation(Marking.class);
                    if (anno != null) {
                        return target.setValue(value.replace("xxx", "***"));
                    }
                }

                return super.encode(ctx, value, target);
            }
        });

        User user = new User();

        String json = ONode.ofBean(user, options).toJson();
        System.out.println(json);

        Assertions.assertEquals("{\"name\":\"aaa\",\"password\":\"***\"}", json);
    }


    public static class User {
        String name = "aaa";
        @Marking(rule = "password")
        String password = "xxx";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Marking {
        String rule() default "";
    }
}