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

package com.github.housepower.data;

import com.github.housepower.misc.ByteBufHelper;
import com.github.housepower.protocol.Encodable;
import io.netty.buffer.ByteBuf;

public class BlockSettings implements ByteBufHelper, Encodable {
    private static final ByteBufHelper helper = new ByteBufHelper() {
    };

    private final Setting[] settings;

    public BlockSettings(Setting[] settings) {
        this.settings = settings;
    }

    @Override
    public void encode(ByteBuf buf) {
        for (Setting setting : settings) {
            writeVarInt(buf, setting.num);

            if (Boolean.class.isAssignableFrom(setting.clazz)) {
                buf.writeBoolean((Boolean) setting.defaultValue);
            } else if (Integer.class.isAssignableFrom(setting.clazz)) {
                buf.writeIntLE((Integer) setting.defaultValue);
            }
        }
        writeVarInt(buf, 0);
    }

    public static BlockSettings readFrom(ByteBuf buf) {
        return new BlockSettings(readSettingsFrom(1, buf));
    }

    private static Setting[] readSettingsFrom(int currentSize, ByteBuf buf) {
        long num = helper.readVarInt(buf);

        for (Setting setting : Setting.defaultValues()) {
            if (setting.num == num) {
                if (Boolean.class.isAssignableFrom(setting.clazz)) {
                    Setting receiveSetting = new Setting(setting.num, buf.readBoolean());
                    Setting[] settings = readSettingsFrom(currentSize + 1, buf);
                    settings[currentSize - 1] = receiveSetting;
                    return settings;
                } else if (Integer.class.isAssignableFrom(setting.clazz)) {
                    Setting receiveSetting = new Setting(setting.num, buf.readIntLE());
                    Setting[] settings = readSettingsFrom(currentSize + 1, buf);
                    settings[currentSize - 1] = receiveSetting;
                    return settings;
                }
            }
        }
        return new Setting[currentSize - 1];
    }

    public static class Setting {
        public static final Setting IS_OVERFLOWS = new Setting(1, false);
        public static final Setting BUCKET_NUM = new Setting(2, -1);

        public static Setting[] defaultValues() {
            return new Setting[]{IS_OVERFLOWS, BUCKET_NUM};
        }

        private final int num;
        private final Class<?> clazz;
        private final Object defaultValue;

        public Setting(int num, Object defaultValue) {
            this.num = num;
            this.defaultValue = defaultValue;
            this.clazz = defaultValue.getClass();
        }
    }
}
