package com.github.housepower.jdbc;

import com.github.housepower.jdbc.tool.TestHarness;
import org.junit.Test;

/**
 * Implements to test all supported DataTypes
 */
public class GenericSimpleInsertITest extends AbstractITest {

    @Test
    public void runGeneric() throws Exception {
        TestHarness helper = new TestHarness();
        helper.clean();
        helper.create();
        helper.insert();
        helper.checkItem();
        helper.checkAggr();
        helper.clean();
    }
}
