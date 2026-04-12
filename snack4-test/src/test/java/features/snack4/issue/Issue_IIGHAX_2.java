package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author noear 2026/4/11 created
 *
 */
public class Issue_IIGHAX_2 {
    @Test
    public void case1() {
        Task task = ONode.ofJson("{todos:[{idx:1, note:'a'},{idx:2, note:'b'}],title:'ab'}")
                .toBean(Task.class);

        assert "ab".equals(task.title);
        assert "b".equals(task.todos.get(1).note);
    }

    @Test
    public void case2() {
        Task task = ONode.ofJson("{todos:\"[{idx:1, note:'a'},{idx:2, note:'b'}]\",title:'ab'}")
                .toBean(Task.class);

        assert "ab".equals(task.title);
        assert "b".equals(task.todos.get(1).note);

        task = ONode.ofJson("{todos:\"[{idx:1, note:'a'},{idx:2, note:'b'}]\",title:'{a:1}'}")
                .toBean(Task.class);

        System.out.println(task.title);
        assert "{a:1}".equals(task.title);
        assert "b".equals(task.todos.get(1).note);

        task = ONode.ofJson("{todos:\"[{idx:1, note:'a'},{idx:2, note:'b'}]\",title:{a:1}}")
                .toBean(Task.class);

        System.out.println(task.title);
        assert "{\"a\":1}".equals(task.title);
        assert "b".equals(task.todos.get(1).note);
    }

    public static class Task {
        ArrayList<Todo> todos;
        String title;
    }

    public static class Todo {
        int idx;
        String note;
    }
}