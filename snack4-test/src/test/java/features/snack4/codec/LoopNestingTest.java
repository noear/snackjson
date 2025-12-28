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
        A tmp = new A();
        tmp.a = "a";
        tmp.b = tmp;

        String json = ONode.serialize(tmp);
        System.out.println(json);
        Assertions.assertEquals("{\"a\":\"a\"}", json);
    }

    @Test
    public void case2_ref() {
        A tmp = new A();
        tmp.a = "a";
        tmp.b = tmp;

        A a = ONode.deserialize("{\"a\":\"a\"}", A.class);
        Assertions.assertEquals("a", "a");
        assert a.b == a;
    }

    public static class A {
        public String a;
        public A b;
    }
}