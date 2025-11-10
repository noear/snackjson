package features.snack4.issue;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import java.util.List;

/**
 *
 * @author noear 2025/11/10 created
 *
 */
public class Issue_ID5SJ6 {
    @Test
    public void case1() {
        String json = "{'defaultPlatform':'iOs', 'localPlus':[{'code':1, 'name':'aaa'}]}";
        A123 a123 = ONode.deserialize(json, A123.class);

        assert a123.getLocalPlus().get(0).getCode() == 1;
    }

    @Data
    public static class A123 {
        private String defaultPlatform;
        private List<? extends C123> localPlus;
    }

    @Data
    public static class C123 {
        private int code;
        private String name;
    }
}