package features.snack4.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import java.util.List;


/**
 *
 * @author noear 2026/3/20 created
 *
 */
public class ListTest {
    @Test
    public void case1() {
        String json = "{\"list\": \"a, b,c\"}";

        Demo demo = ONode.deserialize(json, Demo.class);

        System.out.println(demo.list);

        assert "a".equals(demo.list.get(0));
        assert "b".equals(demo.list.get(1));
        assert "c".equals(demo.list.get(2));
    }

    public static class Demo {
        public List<String> list;
    }
}