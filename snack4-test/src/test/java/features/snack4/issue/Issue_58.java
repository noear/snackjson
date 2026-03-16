package features.snack4.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.SnackException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author noear 2025/12/29 created
 *
 */
public class Issue_58 {
    static String json ="{\n" +
            "  \"a\": [{\"x\":  42}, {\"x\": 777}],\n" +
            "  \"b\": [],\n" +
            "  \"c\": {\"y\":  1},\n" +
            "  \"d\": {\"y\":  2},\n" +
            "  \"e\": null\n" +
            "}";

    @Test
    public void testLibrarySupport() throws Exception {
        ONode schema = ONode.ofJson(json);

        //----------------------------------------------------
        //Case (0): this should return no results
        String q0 = "$.a[?@.y]";
        boolean e0 = schema.exists(q0);
        ONode r0 = schema.select(q0);

        assertFalse(e0); //this fails
        assertTrue(r0.isArray());
        assertEquals(0, r0.size());
        assertNull(r0.parent());

        //----------------------------------------------------
        //Case (1): this should return the empty array in b
        String q1 = "$.b";
        boolean e1 = schema.exists(q1);
        ONode r1 = schema.select(q1);

        assertTrue(e1);
        assertTrue(r1.isArray());
        assertEquals(0, r1.size());
        //is checking the parent the only way to see if the target is an array?
        assertNotNull(r1.parent());

        //----------------------------------------------------
        //Case (2): should get the elements in c and d
        String q2 = "$.*.y";
        boolean e2 = schema.exists(q2);
        ONode r2 = schema.select(q2);

        assertTrue(e2);
        assertTrue(r2.isArray());
        assertEquals(2, r2.size());
        assertNull(r2.parent());
        // the returned nodes in the result arrays are not copies, but references to original tree, sharing same root
        assertEquals(r2.get(0).parent().parent(), r2.get(1).parent().parent());

        //----------------------------------------------------
        //Case (3):  although e is null, it exists
        String q3 = "$.e";
        boolean e3 = schema.exists(q3);
        ONode r3 = schema.select(q3);

        assertTrue(e3);
        assertFalse(r3.isArray());
        assertTrue(r3.isNull());
        assertNotNull(r3.parent());

        //----------------------------------------------------
        //Case (4):  f is undefined in the document
        String q4 = "$.f";
        boolean e4 = schema.exists(q4);
        ONode r4 = schema.select(q4);

        //if undefined, then it is treated as a "null" node, but with no parent, and with "exists" returning false
        assertFalse(e4);
        assertFalse(r4.isArray());
        assertTrue(r4.isNull());
        assertNull(r4.parent());

        //----------------------------------------------------
        //Case (5):  f is undefined
        String q5 = "$.f[?@.y]";
        boolean e5 = schema.exists(q5);
        ONode r5 = schema.select(q5);

        assertFalse(e5); // this fails???
        assertTrue(r5.isArray()); //this is now treated as an empty array?
        assertEquals(0, r5.size());
        assertNull(r5.parent());
    }
}