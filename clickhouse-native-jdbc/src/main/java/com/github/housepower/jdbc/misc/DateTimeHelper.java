package com.github.housepower.jdbc.misc;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.settings.SettingKey;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeHelper {

    public static ZoneId chooseTimeZone(PhysicalInfo.ServerInfo serverInfo) {
        return (boolean) serverInfo.getConfigure().settings().getOrDefault(SettingKey.use_client_time_zone, false)
                ? ZoneId.systemDefault() : serverInfo.timeZone();
    }

    public static LocalDateTime convertTimeZone(LocalDateTime localDateTime, ZoneId from, ZoneId to) {
        return localDateTime.atZone(from).withZoneSameInstant(to).toLocalDateTime();
    }

    public static long toEpochMilli(ZonedDateTime zdt) {
        return zdt.toInstant().toEpochMilli();
    }
}
