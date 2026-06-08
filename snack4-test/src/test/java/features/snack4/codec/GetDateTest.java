package features.snack4.codec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.Date;

/**
 * ONode.getDate(def) 单测
 *
 * <p>验证各类型分支下 getDate(Date def) 的行为，尤其关注：
 * <ul>
 *   <li>字符串解析失败时是否正确返回 def</li>
 *   <li>正常类型转换是否正确</li>
 * </ul>
 *
 * @author noear 2026/6/8 created
 * @since 4.0
 */
public class GetDateTest {

    @Test
    public void getDate_from_date() {
        Date now = new Date();
        ONode node = new ONode().asObject().set("d", now).get("d");
        Assertions.assertEquals(now, node.getDate());
    }

    @Test
    public void getDate_from_number() {
        long ts = 1700000000000L;
        ONode node = ONode.ofJson("{\"d\":" + ts + "}").get("d");
        Assertions.assertEquals(new Date(ts), node.getDate());
    }

    @Test
    public void getDate_from_valid_string() {
        String dateStr = "2025-06-08T12:00:00";
        ONode node = ONode.ofJson("{\"d\":\"" + dateStr + "\"}").get("d");
        Date result = node.getDate();
        Assertions.assertNotNull(result);
    }

    @Test
    public void getDate_from_invalid_string_returns_def() {
        Date def = new Date(0);
        ONode node = ONode.ofJson("{\"d\":\"not-a-date\"}").get("d");
        Date result = node.getDate(def);
        // 解析失败时必须返回 def，而不是 null
        Assertions.assertEquals(def, result);
    }

    @Test
    public void getDate_from_null_returns_def() {
        Date def = new Date(12345);
        ONode node = ONode.ofJson("{\"d\":null}").get("d");
        Date result = node.getDate(def);
        Assertions.assertEquals(def, result);
    }

    @Test
    public void getDate_from_boolean_returns_def() {
        Date def = new Date(99999);
        ONode node = ONode.ofJson("{\"d\":true}").get("d");
        Date result = node.getDate(def);
        Assertions.assertEquals(def, result);
    }

    @Test
    public void getDate_from_missing_returns_def() {
        Date def = new Date(100);
        ONode node = ONode.ofJson("{}").get("nonexistent");
        Date result = node.getDate(def);
        Assertions.assertEquals(def, result);
    }
}
