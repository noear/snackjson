package features.snack4.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.SnackException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2025/12/29 created
 *
 */
public class Issue_38 {
    @Test
    public void deleteTest() {
        Assertions.assertThrows(SnackException.class, () -> {
            ONode.ofJson("127.0.0.1");
        });
    }
}