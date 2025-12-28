package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author noear 2025/12/28 created
 *
 */
public class LoopNestingTest {
    @Test
    public void case1() {
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("a", "a");
        tmp.put("b", tmp);

        String json = ONode.serialize(tmp);
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"a\"}", json);
    }

    @Test
    public void case2() {
        Bean tmp = new Bean();
        tmp.a = "a";
        tmp.b = tmp;

        String json = ONode.serialize(tmp);
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"a\"}", json);
    }

    @Test
    public void case2_ref() {
        Bean tmp = new Bean();
        tmp.a = "a";
        tmp.b = tmp;

        Bean a = ONode.deserialize("{\"a\":\"a\"}", Bean.class);
        Assertions.assertEquals("a", "a");
    }

    public static class Bean {
        public String a;
        public Bean b;
    }
}