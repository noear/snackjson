package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.TypeRef;

import java.util.List;

/**
 *
 * @author noear 2026/4/11 created
 *
 */
public class Issue_IIGHAX {
    @Test
    public void case11() {
        Task task = ONode.ofJson("{todos:[{idx:1, note:'a'},{idx:2, note:'b'}],title:'ab'}")
                .toBean(Task.class);

        assert "ab".equals(task.title);
        assert "b".equals(task.todos.get(1).note);
    }

    @Test
    public void case12() {
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

    @Test
    public void case13() {
        ONode oNode = ONode.ofJson("{todos:\"[{idx:1, note:'a'},{idx:2, note:'b'}]\",title:'ab'}");

        List<Todo> list = oNode.get("todos").toBean(TypeRef.listOf(Todo.class));
        assert "b".equals(list.get(1).note);
    }

    @Test
    public void case21() {
        TodoHold todoHold = ONode.ofJson("{todo:{idx:1, note:'a'},title:'ab'}")
                .toBean(TodoHold.class);

        assert "ab".equals(todoHold.title);
        assert "a".equals(todoHold.todo.note);
    }

    @Test
    public void case22() {
        TodoHold todoHold = ONode.ofJson("{todo:\"{idx:1, note:'a'}\",title:'ab'}")
                .toBean(TodoHold.class);

        assert "ab".equals(todoHold.title);
        assert "a".equals(todoHold.todo.note);
    }

    @Test
    public void case23() {
        ONode oNode = ONode.ofJson("{todo:\"{idx:1, note:'a'}\",title:'ab'}");

        Todo todo = oNode.get("todo").toBean(Todo.class);
        assert "a".equals(todo.note);
    }

    public static class Task {
        List<Todo> todos;
        String title;
    }

    public static class TodoHold {
        Todo todo;
        String title;
    }

    public static class Todo {
        int idx;
        String note;
    }
}