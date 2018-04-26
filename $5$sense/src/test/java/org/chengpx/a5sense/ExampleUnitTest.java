package org.chengpx.a5sense;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private String mTag = "org.chengpx.a5sense.ExampleUnitTest";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1() {
        List<Integer> intList1 = new ArrayList<>();
        intList1.add(1);
        intList1.add(1);
        intList1.add(1);
        intList1.add(1);
        System.out.println("intList no clear: " + intList1.toString());
        ArrayList<Integer> intList2 = new ArrayList<>();
        boolean isAddAllSuccess = intList2.addAll(intList1);
        System.out.println("isAddAllSuccess = " + isAddAllSuccess);
        System.out.println("intList clear before intList2: " + intList2.toString());
        intList1.clear();
        System.out.println("intList2 clear after intList2: " + intList2.toString());

        System.out.println("第二次");
        intList1.add(2);
        intList1.add(2);
        intList1.add(2);
        intList1.add(2);
        System.out.println("intList no clear: " + intList1.toString());
        isAddAllSuccess = intList2.addAll(intList1);
        System.out.println("isAddAllSuccess = " + isAddAllSuccess);
        System.out.println("intList clear before intList2: " + intList2.toString());
        intList1.clear();
        System.out.println("intList2 clear after intList2: " + intList2.toString());
    }

}