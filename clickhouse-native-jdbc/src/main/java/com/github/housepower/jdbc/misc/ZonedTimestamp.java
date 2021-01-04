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

package com.github.housepower.jdbc.misc;

import java.sql.Timestamp;
import java.time.ZoneId;

public class ZonedTimestamp extends Timestamp {

    public ZonedTimestamp(long ts) {
        super(ts);
    }
    
    public ZonedTimestamp(long ts, ZoneId tz) {
        super(ts);
    }

    public ZonedTimestamp(long ts, int nanos, ZoneId tz) {
        super(ts);
        this.setNanos(nanos);
    }

    public ZonedTimestamp(Timestamp x, ZoneId tz) {
        super(x.getTime());
        this.setNanos(x.getNanos());
    }
}
