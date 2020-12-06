/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc;

import com.github.housepower.jdbc.tool.TestHarness;
import org.junit.jupiter.api.Test;

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
        helper.checkAgg();
        helper.clean();
    }
}
