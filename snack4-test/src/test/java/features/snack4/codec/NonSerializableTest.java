package features.snack4.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectPatternEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author noear 2026/1/22 created
 *
 */
public class NonSerializableTest {
    @Test
    public void case1() {
        Map<String, Object> map = new HashMap<>();

        map.put("a", new A());
        map.put("b", new B());
        map.put("test", true);

        String json1 = ONode.serialize(map);
        System.out.println(json1);

        Options options = Options.of();
        options.addEncoder(new ObjectPatternEncoder<NonSerializable>() {
            @Override
            public boolean canEncode(Object value) {
                return value instanceof NonSerializable;
            }

            @Override
            public ONode encode(EncodeContext ctx, NonSerializable value, ONode target) {
                return null;
            }
        });

        String json2 = ONode.serialize(map, options);
        System.out.println(json2);
    }

    public static class A implements NonSerializable {
        String userId = "a";
    }

    public static class B {
        Map<String, Object> map = new HashMap<>();

        public B() {
            map.put("x", "x");
            map.put("c", new C());
        }
    }

    public static class C implements NonSerializable {
        long orderId = 1;
    }

    interface NonSerializable {
    }
}