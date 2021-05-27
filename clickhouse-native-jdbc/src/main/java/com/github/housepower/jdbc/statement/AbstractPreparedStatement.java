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

package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.client.NativeContext;
import com.github.housepower.misc.BytesCharSeq;
import com.github.housepower.misc.DateTimeUtil;
import com.github.housepower.misc.Validate;
import com.github.housepower.jdbc.wrapper.SQLPreparedStatement;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;

public abstract class AbstractPreparedStatement extends ClickHouseStatement implements SQLPreparedStatement {

    private final String[] queryParts;
    private final DateTimeFormatter dateFmt;
    private final DateTimeFormatter timestampFmt;
    protected final ZoneId tz;

    protected Object[] parameters;

    public AbstractPreparedStatement(ClickHouseConnection connection, NativeContext nativeContext, String[] queryParts) {
        super(connection, nativeContext);
        this.queryParts = queryParts;
        if (queryParts != null && queryParts.length > 0)
            this.parameters = new Object[queryParts.length];

        this.tz = DateTimeUtil.chooseTimeZone(nativeContext.serverCtx());
        this.dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT).withZone(tz);
        this.timestampFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT).withZone(tz);
    }

    @Override
    public void setBoolean(int index, boolean x) throws SQLException {
        setObject(index, x ? (byte) 1 : (byte) 0);
    }

    @Override
    public void setByte(int index, byte x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setShort(int index, short x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setInt(int index, int x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setLong(int index, long x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setFloat(int index, float x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setDouble(int index, double x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setNull(int index, int type) throws SQLException {
        setObject(index, null);
    }

    @Override
    public void setTimestamp(int index, Timestamp x) throws SQLException {
        setObject(index, DateTimeUtil.toZonedDateTime(x, tz));
    }

    @Override
    public void setTimestamp(int index, Timestamp x, Calendar cal) throws SQLException {
        setObject(index, DateTimeUtil.toZonedDateTime(x, cal.getTimeZone().toZoneId()));
    }

    @Override
    public void setDate(int index, Date x) throws SQLException {
        setObject(index, x.toLocalDate());
    }

    @Override
    public void setDate(int index, Date x, Calendar cal) throws SQLException {
        // just ignore cal, date has no concepts of timezone
        setObject(index, x.toLocalDate());
    }

    @Override
    public void setBigDecimal(int index, BigDecimal x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setString(int index, String x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setBytes(int index, byte[] x) throws SQLException {
        setObject(index, new BytesCharSeq(x));
    }

    @Override
    public void setURL(int index, URL x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setArray(int index, Array x) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setObject(int index, Object x, int targetSqlType) throws SQLException {
        setObject(index, x);
    }

    @Override
    public void setObject(int index, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        setObject(index, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return getResultSet().getMetaData();
    }

    @Override
    public void clearParameters() throws SQLException {
        Arrays.fill(parameters, null);
    }

    protected String assembleQueryPartsAndParameters() throws SQLException {
        // TODO: move to DataType
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < queryParts.length; i++) {
            if (i - 1 >= 0 && i - 1 < parameters.length) {
                Validate.isTrue(assembleParameter(parameters[i - 1], queryBuilder),
                        "UNKNOWN DataType :" + (parameters[i - 1] == null ? null : parameters[i - 1].getClass()));
            }
            queryBuilder.append(queryParts[i]);
        }
        return queryBuilder.toString();
    }

    private boolean assembleParameter(Object parameter, StringBuilder queryBuilder) throws SQLException {
        return assembleSimpleParameter(queryBuilder, parameter)
                || assembleComplexQuotedParameter(queryBuilder, parameter);
    }

    private boolean assembleSimpleParameter(StringBuilder queryBuilder, Object parameter) {
        if (parameter instanceof String)
            return assembleQuotedParameter(queryBuilder, String.valueOf(parameter));
        if (parameter == null)
            return assembleWithoutQuotedParameter(queryBuilder, "Null");
        if (parameter instanceof Number)
            return assembleWithoutQuotedParameter(queryBuilder, parameter);
        if (parameter instanceof LocalDate)
            return assembleQuotedParameter(queryBuilder, dateFmt.format((LocalDate) parameter));
        if (parameter instanceof ZonedDateTime)
            return assembleQuotedParameter(queryBuilder, timestampFmt.format((ZonedDateTime) parameter));
        return false;
    }

    private boolean assembleQuotedParameter(StringBuilder queryBuilder, String parameter) {
        queryBuilder.append("'");
        queryBuilder.append(parameter.replaceAll("'", Matcher.quoteReplacement("\\'")));
        queryBuilder.append("'");
        return true;
    }

    private boolean assembleWithoutQuotedParameter(StringBuilder queryBuilder, Object parameter) {
        queryBuilder.append(parameter);
        return true;
    }

    private boolean assembleComplexQuotedParameter(StringBuilder queryBuilder, Object parameter) throws SQLException {
        if (parameter instanceof Array) {
            queryBuilder.append("[");
            Object[] arrayData = (Object[]) ((Array) parameter).getArray();
            for (int arrayIndex = 0; arrayIndex < arrayData.length; arrayIndex++) {
                assembleParameter(arrayData[arrayIndex], queryBuilder);
                queryBuilder.append(arrayIndex == arrayData.length - 1 ? "]" : ",");
            }
            return true;
        } else if (parameter instanceof Struct) {
            queryBuilder.append("(");
            Object[] structData = ((Struct) parameter).getAttributes();
            for (int structIndex = 0; structIndex < structData.length; structIndex++) {
                assembleParameter(structData[structIndex], queryBuilder);
                queryBuilder.append(structIndex == structData.length - 1 ? ")" : ",");
            }
            return true;
        }
        return false;
    }
}
