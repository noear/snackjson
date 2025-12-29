package demo.snack4;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

/**
 *
 * @author noear 2025/12/29 created
 *
 */
public class OptionsDemo {
    public void case1() {
        Options options = Options.of().addCreator(ONode.class, (opts, node, clazz) -> new ONode(opts));

        ONode node = ONode.deserialize("{}", options);
    }
}
