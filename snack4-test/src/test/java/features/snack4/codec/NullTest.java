package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2026/1/20 created
 *
 */
public class NullTest {
    @Test
    public void case1() {
        List<Number> list = new ArrayList<>();

        list.add(null);
        list.add(1);
        list.add(2);

        String json = ONode.serialize(list);
        System.out.println(json);
        Assertions.assertEquals("[null,1,2]", json);

        List<Number> list2 = ONode.deserialize(json, List.class);

        String json2 = ONode.serialize(list2);
        System.out.println(json2);
        Assertions.assertEquals("[null,1,2]", json2);
    }
}
