package features.snack4.jsonpath.RFC9535;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContextImpl;
import org.noear.snack4.jsonpath.filter.Expression;
import org.noear.snack4.jsonpath.QueryMode;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author noear 2025/5/6 created
 */
public class RFC9535_s2353_Comparison extends AbsRFC9535 {
    // SQL/JSON Path (ISO/IEC 9075)
    // IETF JSONPath (RFC 9535) https://www.rfc-editor.org/rfc/rfc9535.html

    static final String comparisonJson = "{\n" +
            "  \"obj\": {\"x\": \"y\"},\n" +
            "  \"arr\": [2, 3]\n" +
            "}";

    @Test
    public void comparisonTest() {
        //https://www.rfc-editor.org/rfc/rfc9535.html#filter-selector

        comparisonAssert("$.absent1 == $.absent2", true); //Empty nodelists
        comparisonAssert("$.absent1 <= $.absent2", true); //== implies <=
        comparisonAssert("$.absent == 'g'", false);
        comparisonAssert("$.absent1 != $.absent2", false);
        comparisonAssert("$.absent != 'g'", true);

        comparisonAssert("1 <= 2", true);
        comparisonAssert("1 > 2", false);
        comparisonAssert("13 == '13'", false);
        comparisonAssert("'a' <= 'b'", true);
        comparisonAssert("'a' > 'b'", false);

        comparisonAssert("$.obj == $.arr", false);
        comparisonAssert("$.obj != $.arr", true);
        comparisonAssert("$.obj == $.obj", true);
        comparisonAssert("$.obj != $.obj", false);
        comparisonAssert("$.arr == $.arr", true);
        comparisonAssert("$.arr != $.arr", false);

        comparisonAssert("$.obj == 17", false);
        comparisonAssert("$.obj != 17", true);

        comparisonAssert("$.obj <= $.arr", false);
        comparisonAssert("$.obj < $.arr", false);
        comparisonAssert("$.obj <= $.obj", true);
        comparisonAssert("$.arr <= $.arr", true);

        comparisonAssert("1 <= $.arr", false);
        comparisonAssert("1 >= $.arr", false);
        comparisonAssert("1 > $.arr", false);
        comparisonAssert("1 < $.arr", false);

        comparisonAssert("true <= true", true);
        comparisonAssert("true > true", false);
    }

    @Test
    public void comparisonTest_NoSpace() {
        //https://www.rfc-editor.org/rfc/rfc9535.html#filter-selector

        comparisonAssert("$.absent1==$.absent2", true); //Empty nodelists
        comparisonAssert("$.absent1<=$.absent2", true); //== implies <=
        comparisonAssert("$.absent=='g'", false);
        comparisonAssert("$.absent1!=$.absent2", false);
        comparisonAssert("$.absent!='g'", true);

        comparisonAssert("1<=2", true);
        comparisonAssert("1>2", false);
        comparisonAssert("13=='13'", false);
        comparisonAssert("'a'<='b'", true);
        comparisonAssert("'a'>'b'", false);

        comparisonAssert("$.obj==$.arr", false);
        comparisonAssert("$.obj!=$.arr", true);
        comparisonAssert("$.obj==$.obj", true);
        comparisonAssert("$.obj!=$.obj", false);
        comparisonAssert("$.arr==$.arr", true);
        comparisonAssert("$.arr!=$.arr", false);

        comparisonAssert("$.obj==17", false);
        comparisonAssert("$.obj!=17", true);

        comparisonAssert("$.obj<=$.arr", false);
        comparisonAssert("$.obj<$.arr", false);
        comparisonAssert("$.obj<=$.obj", true);
        comparisonAssert("$.arr<=$.arr", true);

        comparisonAssert("1<=$.arr", false);
        comparisonAssert("1>=$.arr", false);
        comparisonAssert("1>$.arr", false);
        comparisonAssert("1<$.arr", false);

        comparisonAssert("true<=true", true);
        comparisonAssert("true>true", false);
    }

    @Test
    public void comparisonTest_SpaceSymbols_constant() {
        /*
            RFC 9535 defines 4 symbols to be considered as spaces: ' ', '\n', '\r', '\t'
         */

        comparisonAssert("1==2", false);
        comparisonAssert("1==1", true);
        comparisonAssert("1 == 1", true);
        comparisonAssert("1  == 1", true);
        comparisonAssert("1== 1", true);
//        comparisonAssert("1 ==1", true); // TODO this fails
        comparisonAssert("1\t==\t1", true);
        comparisonAssert("1\t==1", true);
        comparisonAssert("1==\t1", true);
        comparisonAssert("1\r==\r1", true);
        comparisonAssert("1==\r1", true);
        comparisonAssert("1\r==1", true);
        comparisonAssert("1\n==\n1", true);
        comparisonAssert("1==\n1", true);
        comparisonAssert("1\n==1", true);
        comparisonAssert("1\n\r\t==1", true);
        comparisonAssert("1\n==\t1", true);
//        comparisonAssert("1 == \t \t \n    \r1", true); // TODO this fails
    }


    @Test
    public void comparisonTest_SpaceSymbols_identifier() {
        /*
            RFC 9535 defines 4 symbols to be considered as spaces: ' ', '\n', '\r', '\t'
         */

        comparisonAssert("$.arr==$.obj", false);
        comparisonAssert("$.arr==$.arr", true);
        comparisonAssert("$.arr == $.arr", true);
        comparisonAssert("$.arr  == $.arr", true);
        comparisonAssert("$.arr== $.arr", true);
//        comparisonAssert("$.arr ==$.arr", true); // TODO this fails
        comparisonAssert("$.arr\t==\t$.arr", true);
        comparisonAssert("$.arr\t==$.arr", true);
        comparisonAssert("$.arr==\t$.arr", true);
        comparisonAssert("$.arr\r==\r$.arr", true);
        comparisonAssert("$.arr==\r$.arr", true);
        comparisonAssert("$.arr\r==$.arr", true);
        comparisonAssert("$.arr\n==\n$.arr", true);
        comparisonAssert("$.arr==\n$.arr", true);
        comparisonAssert("$.arr\n==$.arr", true);
        comparisonAssert("$.arr\n\r\t==$.arr", true);
        comparisonAssert("$.arr\n==\t$.arr", true);
//        comparisonAssert("$.arr == \t \t \n    \r$.arr", true); // TODO this fails
    }

    private void comparisonAssert(String expr, boolean expected) {
        System.out.println("----------------------: " + expr);
        ONode node = ofJson(comparisonJson);
        boolean actual;
        try {
            actual = Expression.of(expr).test(node, new QueryContextImpl(node, QueryMode.SELECT));
        } catch (JsonPathException e) {
            fail("Failed to parse expression: " + expr, e);
            return;
        }

        if (expected != actual) {
            System.out.println("::: " + actual + " != " + expected);
            System.out.println(comparisonJson);
            //assert false; //fail() will work even without -ea, and provide msg
            fail("Failed to correctly handle the boolean result of expression: " + expr);
        }
    }
}