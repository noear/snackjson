package features.snack4.codec;


import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author noear 2025/12/25 created
 *
 */
public class IterableTest {
    @Test
    public void case1() {
        IterableImpl tmp = new IterableImpl();
        String json = ONode.serialize(tmp);

        assert "[1,2,3]".equals(json);
    }

    public static class IterableImpl implements Iterable<Integer> {
        private List<Integer> list = Arrays.asList(1, 2, 3);

        @Override
        public Iterator<Integer> iterator() {
            return list.iterator();
        }
    }
}