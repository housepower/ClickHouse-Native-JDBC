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

package com.github.housepower.protocol.listener;

import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.protocol.ProgressResponse;

public class DefaultProgressListener implements ProgressListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultProgressListener.class);

    private DefaultProgressListener() {
    }

    public static DefaultProgressListener create() {
        return new DefaultProgressListener();
    }

    @Override
    public void onProgress(ProgressResponse progressResponse) {
        LOG.info("DefaultProgressListener: ".concat(progressResponse.toString()));
    }
}
