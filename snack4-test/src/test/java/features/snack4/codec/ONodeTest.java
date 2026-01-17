package features.snack4.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2026/1/17 created
 *
 */
public class ONodeTest {
    @Test
    public void case1() {
        DemoBean demo = new DemoBean();
        demo.data.set("user", "solon");

        String json = ONode.serialize(demo);
        DemoBean demo2 = ONode.deserialize(json, DemoBean.class);

        assert demo2.data != null;
        assert demo2.data.size() == 1;
    }

    public static class DemoBean {
        public ONode data = new ONode().asObject();
    }
}
