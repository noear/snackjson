package features.snack4.codec;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.Optional;

/**
 *
 * @author noear 2025/12/29 created
 *
 */
public class OptionalTest {
    @Test
    public void case1() {
        OptionalBean tmp = new OptionalBean();
        tmp.name = Optional.of("xxx");

        String json = ONode.serialize(tmp);
        System.out.println(json);

        Assertions.assertEquals("{\"name\":\"xxx\"}", json);

        OptionalBean tmp2 = ONode.deserialize(json, OptionalBean.class);
        Assertions.assertNotNull(tmp2.name);
        Assertions.assertEquals("xxx", tmp2.name.get());
    }

    public static class OptionalBean {
        public Optional<String> name;
    }
}
