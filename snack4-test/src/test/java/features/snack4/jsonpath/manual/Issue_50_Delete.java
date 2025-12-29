package features.snack4.jsonpath.manual;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author noear 2025/12/29 created
 *
 */
public class Issue_50_Delete {
    @Test
    public void deleteTest() {

        List<User> list = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin1");
        user1.setAge(8);
        list.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("admin2");
        user2.setAge(10);
        list.add(user2);

        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("admin3");
        user3.setAge(18);
        list.add(user3);

        final ONode node = ONode.ofBean(list);
        // 获取年龄的jsonPath
        String jsonPath = "$[?@.age != 10]";
        node.delete(jsonPath);
        System.out.println("node = " + node);
    }

    public static class User {
        private Long id;
        private String username;
        private Integer age;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}