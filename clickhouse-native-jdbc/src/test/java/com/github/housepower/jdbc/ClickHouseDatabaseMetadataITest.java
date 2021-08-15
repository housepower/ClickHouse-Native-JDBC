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

import com.github.housepower.settings.ClickHouseDefines;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ClickHouseDatabaseMetadataITest extends AbstractITest {

    @Test
    void allProceduresAreCallable() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.allProceduresAreCallable());
        });
    }

    @Test
    void allTablesAreSelectable() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.allTablesAreSelectable());
        });
    }

    @Test
    void getURL() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(String.format(Locale.ROOT, "jdbc:clickhouse://%s:%s/default?query_timeout=0&connect_timeout=0&charset=UTF-8&client_name=ClickHouse client&tcp_keep_alive=false", CK_HOST, CK_PORT),
                    dm.getURL());
        });
    }

    @Test
    void getUserName() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("default", dm.getUserName());
        });
    }

    @Test
    void isReadOnly() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.isReadOnly());
        });
    }

    @Test
    void nullsAreSortedHigh() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.nullsAreSortedHigh());
        });
    }

    @Test
    void nullsAreSortedLow() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.nullsAreSortedLow());
        });
    }

    @Test
    void nullsAreSortedAtStart() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.nullsAreSortedAtStart());
        });
    }

    @Test
    void nullsAreSortedAtEnd() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.nullsAreSortedAtEnd());
        });
    }

    @Test
    void getDatabaseProductName() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("ClickHouse", dm.getDatabaseProductName());
        });
    }

    @Test
    void getDatabaseProductVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(((ClickHouseConnection) connection).serverContext().version(), dm.getDatabaseProductVersion());
        });
    }

    @Test
    void getDriverName() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("com.github.housepower.clickhouse.native.jdbc", dm.getDriverName());
        });
    }

    @Test
    void getDriverVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(String.valueOf(ClickHouseDefines.CLIENT_REVISION), dm.getDriverVersion());
        });
    }

    @Test
    void getDriverMajorVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(ClickHouseDefines.MAJOR_VERSION, dm.getDriverMajorVersion());
        });
    }

    @Test
    void getDriverMinorVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(ClickHouseDefines.MINOR_VERSION, dm.getDriverMinorVersion());
        });
    }

    @Test
    void usesLocalFiles() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.usesLocalFiles());
        });
    }

    @Test
    void usesLocalFilePerTable() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.usesLocalFilePerTable());
        });
    }

    @Test
    void supportsMixedCaseIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsMixedCaseIdentifiers());
        });
    }

    @Test
    void storesUpperCaseIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.storesUpperCaseIdentifiers());
        });
    }

    @Test
    void storesLowerCaseIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.storesLowerCaseIdentifiers());
        });
    }

    @Test
    void storesMixedCaseIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.storesMixedCaseIdentifiers());
        });
    }

    @Test
    void supportsMixedCaseQuotedIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsMixedCaseQuotedIdentifiers());
        });
    }

    @Test
    void storesUpperCaseQuotedIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.storesUpperCaseQuotedIdentifiers());
        });
    }

    @Test
    void storesLowerCaseQuotedIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.storesLowerCaseQuotedIdentifiers());
        });
    }

    @Test
    void storesMixedCaseQuotedIdentifiers() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.storesMixedCaseQuotedIdentifiers());
        });
    }

    @Test
    void getIdentifierQuoteString() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("`", dm.getIdentifierQuoteString());
        });
    }

    @Test
    void getSQLKeywords() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("GLOBAL,ARRAY", dm.getSQLKeywords());
        });
    }

    @Test
    void getNumericFunctions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("", dm.getNumericFunctions());
        });
    }

    @Test
    void getStringFunctions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("", dm.getStringFunctions());
        });
    }

    @Test
    void getSystemFunctions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("", dm.getSystemFunctions());
        });
    }

    @Test
    void getTimeDateFunctions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("", dm.getTimeDateFunctions());
        });
    }

    @Test
    void getSearchStringEscape() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("\\", dm.getSearchStringEscape());
        });
    }

    @Test
    void getExtraNameCharacters() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("", dm.getExtraNameCharacters());
        });
    }

    @Test
    void supportsAlterTableWithAddColumn() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsAlterTableWithAddColumn());
        });
    }

    @Test
    void supportsAlterTableWithDropColumn() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsAlterTableWithDropColumn());
        });
    }

    @Test
    void supportsColumnAliasing() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsColumnAliasing());
        });
    }

    @Test
    void nullPlusNonNullIsNull() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.nullPlusNonNullIsNull());
        });
    }

    @Test
    void supportsConvert() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsConvert());
        });
    }

    @Test
    void supportsTableCorrelationNames() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsTableCorrelationNames());
        });
    }

    @Test
    void supportsDifferentTableCorrelationNames() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsDifferentTableCorrelationNames());
        });
    }

    @Test
    void supportsExpressionsInOrderBy() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsExpressionsInOrderBy());
        });
    }

    @Test
    void supportsOrderByUnrelated() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsOrderByUnrelated());
        });
    }

    @Test
    void supportsGroupBy() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsGroupBy());
        });
    }

    @Test
    void supportsGroupByUnrelated() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsGroupByUnrelated());
        });
    }

    @Test
    void supportsGroupByBeyondSelect() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsGroupByBeyondSelect());
        });
    }

    @Test
    void supportsLikeEscapeClause() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsLikeEscapeClause());
        });
    }

    @Test
    void supportsMultipleResultSets() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsMultipleResultSets());
        });
    }

    @Test
    void supportsMultipleTransactions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsMultipleTransactions());
        });
    }

    @Test
    void supportsNonNullableColumns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsNonNullableColumns());
        });
    }

    @Test
    void supportsMinimumSQLGrammar() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsMinimumSQLGrammar());
        });
    }

    @Test
    void supportsCoreSQLGrammar() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCoreSQLGrammar());
        });
    }

    @Test
    void supportsExtendedSQLGrammar() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsExtendedSQLGrammar());
        });
    }

    @Test
    void supportsANSI92EntryLevelSQL() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsANSI92EntryLevelSQL());
        });
    }

    @Test
    void supportsANSI92IntermediateSQL() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsANSI92IntermediateSQL());
        });
    }

    @Test
    void supportsANSI92FullSQL() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsANSI92FullSQL());
        });
    }

    @Test
    void supportsIntegrityEnhancementFacility() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsIntegrityEnhancementFacility());
        });
    }

    @Test
    void supportsOuterJoins() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsOuterJoins());
        });
    }

    @Test
    void supportsFullOuterJoins() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsFullOuterJoins());
        });
    }

    @Test
    void supportsLimitedOuterJoins() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsLimitedOuterJoins());
        });
    }

    @Test
    void getSchemaTerm() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("database", dm.getSchemaTerm());
        });
    }

    @Test
    void getProcedureTerm() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("procedure", dm.getProcedureTerm());
        });
    }

    @Test
    void getCatalogTerm() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals("catalog", dm.getCatalogTerm());
        });
    }

    @Test
    void isCatalogAtStart() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.isCatalogAtStart());
        });
    }

    @Test
    void getCatalogSeparator() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(".", dm.getCatalogSeparator());
        });
    }

    @Test
    void supportsSchemasInDataManipulation() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSchemasInDataManipulation());
        });
    }

    @Test
    void supportsSchemasInProcedureCalls() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSchemasInProcedureCalls());
        });
    }

    @Test
    void supportsSchemasInTableDefinitions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSchemasInTableDefinitions());
        });
    }

    @Test
    void supportsSchemasInIndexDefinitions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSchemasInIndexDefinitions());
        });
    }

    @Test
    void supportsSchemasInPrivilegeDefinitions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSchemasInPrivilegeDefinitions());
        });
    }

    @Test
    void supportsCatalogsInDataManipulation() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCatalogsInDataManipulation());
        });
    }

    @Test
    void supportsCatalogsInProcedureCalls() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCatalogsInProcedureCalls());
        });
    }

    @Test
    void supportsCatalogsInTableDefinitions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCatalogsInTableDefinitions());
        });
    }

    @Test
    void supportsCatalogsInIndexDefinitions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCatalogsInIndexDefinitions());
        });
    }

    @Test
    void supportsCatalogsInPrivilegeDefinitions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCatalogsInPrivilegeDefinitions());
        });
    }

    @Test
    void supportsPositionedDelete() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsPositionedDelete());
        });
    }

    @Test
    void supportsPositionedUpdate() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsPositionedUpdate());
        });
    }

    @Test
    void supportsSelectForUpdate() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsSelectForUpdate());
        });
    }

    @Test
    void supportsStoredProcedures() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsStoredProcedures());
        });
    }

    @Test
    void supportsSubqueriesInComparisons() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSubqueriesInComparisons());
        });
    }

    @Test
    void supportsSubqueriesInExists() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsSubqueriesInExists());
        });
    }

    @Test
    void supportsSubqueriesInIns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsSubqueriesInIns());
        });
    }

    @Test
    void supportsSubqueriesInQuantifieds() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsSubqueriesInQuantifieds());
        });
    }

    @Test
    void supportsCorrelatedSubqueries() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsCorrelatedSubqueries());
        });
    }

    @Test
    void supportsUnion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsUnion());
        });
    }

    @Test
    void supportsUnionAll() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsUnionAll());
        });
    }

    @Test
    void supportsOpenCursorsAcrossCommit() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsOpenCursorsAcrossCommit());
        });
    }

    @Test
    void supportsOpenCursorsAcrossRollback() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsOpenCursorsAcrossRollback());
        });
    }

    @Test
    void supportsOpenStatementsAcrossCommit() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsOpenStatementsAcrossCommit());
        });
    }

    @Test
    void supportsOpenStatementsAcrossRollback() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsOpenStatementsAcrossRollback());
        });
    }

    @Test
    void getMaxBinaryLiteralLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxBinaryLiteralLength());
        });
    }

    @Test
    void getMaxCharLiteralLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxCharLiteralLength());
        });
    }

    @Test
    void getMaxColumnNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxColumnNameLength());
        });
    }

    @Test
    void getMaxColumnsInGroupBy() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxColumnsInGroupBy());
        });
    }

    @Test
    void getMaxColumnsInIndex() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxColumnsInIndex());
        });
    }

    @Test
    void getMaxColumnsInOrderBy() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxColumnsInOrderBy());
        });
    }

    @Test
    void getMaxColumnsInSelect() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxColumnsInSelect());
        });
    }

    @Test
    void getMaxColumnsInTable() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxColumnsInTable());
        });
    }

    @Test
    void getMaxConnections() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxConnections());
        });
    }

    @Test
    void getMaxCursorNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxCursorNameLength());
        });
    }

    @Test
    void getMaxIndexLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxIndexLength());
        });
    }

    @Test
    void getMaxSchemaNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxSchemaNameLength());
        });
    }

    @Test
    void getMaxProcedureNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxProcedureNameLength());
        });
    }

    @Test
    void getMaxCatalogNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxCatalogNameLength());
        });
    }

    @Test
    void getMaxRowSize() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxRowSize());
        });
    }

    @Test
    void doesMaxRowSizeIncludeBlobs() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.doesMaxRowSizeIncludeBlobs());
        });
    }

    @Test
    void getMaxStatementLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxStatementLength());
        });
    }

    @Test
    void getMaxStatements() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxStatements());
        });
    }

    @Test
    void getMaxTableNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxTableNameLength());
        });
    }

    @Test
    void getMaxTablesInSelect() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxTablesInSelect());
        });
    }

    @Test
    void getMaxUserNameLength() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(0, dm.getMaxUserNameLength());
        });
    }

    @Test
    void getDefaultTransactionIsolation() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(Connection.TRANSACTION_NONE, dm.getDefaultTransactionIsolation());
        });
    }

    @Test
    void supportsTransactions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsTransactions());
        });
    }

    @Test
    void supportsTransactionIsolationLevel() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE));
            assertFalse(dm.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
            assertFalse(dm.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
            assertFalse(dm.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ));
            assertFalse(dm.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
        });
    }

    @Test
    void supportsDataDefinitionAndDataManipulationTransactions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsDataDefinitionAndDataManipulationTransactions());
        });
    }

    @Test
    void supportsDataManipulationTransactionsOnly() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsDataManipulationTransactionsOnly());
        });
    }

    @Test
    void dataDefinitionCausesTransactionCommit() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.dataDefinitionCausesTransactionCommit());
        });
    }

    @Test
    void dataDefinitionIgnoredInTransactions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.dataDefinitionIgnoredInTransactions());
        });
    }

    @Test
    void getProcedures() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getProcedures(null, "system", null);
            assertEquals(9, rs.getMetaData().getColumnCount());
        });
    }

    @Test
    void getProcedureColumns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getProcedureColumns(null, "system", null, null);
            assertEquals(20, rs.getMetaData().getColumnCount());
        });
    }

    @Test
    void getTables() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getTables(null, "system", "columns", null);
            assertEquals(10, rs.getMetaData().getColumnCount());
        });
    }

    @Test
    void getSchemas() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getSchemas();
            assertEquals(2, rs.getMetaData().getColumnCount());
        });
    }

    @Test
    void getCatalogs() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getCatalogs();
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertTrue(rs.next());
            assertFalse(rs.next());
        });
    }

    @Test
    void getTableTypes() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getTableTypes();
            assertEquals(1, rs.getMetaData().getColumnCount());
        });
    }

    @Test
    void getColumns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getColumns(null, "system", "columns", null);
            assertEquals(24, rs.getMetaData().getColumnCount());
        });
    }

    @Test
    void getColumnPrivileges() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getColumnPrivileges(null, "system", "columns", null);
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getTablePrivileges() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getColumnPrivileges(null, "system", "columns", null);
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getBestRowIdentifier() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getBestRowIdentifier(null, "system", "columns", 0, false);
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getVersionColumns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getVersionColumns(null, "system", "columns");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getPrimaryKeys() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getPrimaryKeys(null, "system", "columns");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getImportedKeys() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getImportedKeys(null, "system", "columns");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getExportedKeys() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getExportedKeys(null, "system", "columns");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getCrossReference() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getCrossReference("", "", "", "", "", "");
            assertFalse(rs.next());
        });
    }

    @Test
    void getTypeInfo() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getTypeInfo();
            for (int i = 0; i < 13; i++) {
                assertTrue(rs.next());
            }
            assertFalse(rs.next());
        });
    }

    @Test
    void getIndexInfo() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getIndexInfo(null, "system", "columns", false, false);
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void supportsResultSetType() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
            assertFalse(dm.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
            assertFalse(dm.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));
        });
    }

    @Test
    void supportsResultSetConcurrency() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsResultSetConcurrency(0, 0));
        });
    }

    @Test
    void ownUpdatesAreVisible() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.ownUpdatesAreVisible(0));
        });
    }

    @Test
    void ownDeletesAreVisible() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.ownDeletesAreVisible(0));
        });
    }

    @Test
    void ownInsertsAreVisible() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.ownInsertsAreVisible(0));
        });
    }

    @Test
    void othersUpdatesAreVisible() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.othersUpdatesAreVisible(0));
        });
    }

    @Test
    void othersDeletesAreVisible() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.othersDeletesAreVisible(0));
        });
    }

    @Test
    void othersInsertsAreVisible() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.othersInsertsAreVisible(0));
        });
    }

    @Test
    void updatesAreDetected() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
            assertFalse(dm.updatesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
            assertFalse(dm.updatesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
        });
    }

    @Test
    void deletesAreDetected() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
            assertFalse(dm.deletesAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
            assertFalse(dm.deletesAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
        });
    }

    @Test
    void insertsAreDetected() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY));
            assertFalse(dm.insertsAreDetected(ResultSet.TYPE_SCROLL_INSENSITIVE));
            assertFalse(dm.insertsAreDetected(ResultSet.TYPE_SCROLL_SENSITIVE));
        });
    }

    @Test
    void supportsBatchUpdates() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertTrue(dm.supportsBatchUpdates());
        });
    }

    @Test
    void getUDTs() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getUDTs("", "", "", null);
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getConnection() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(connection, dm.getConnection());
        });
    }

    @Test
    void supportsSavepoints() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsSavepoints());
        });
    }

    @Test
    void supportsNamedParameters() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsNamedParameters());
        });
    }

    @Test
    void supportsMultipleOpenResults() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsMultipleOpenResults());
        });
    }

    @Test
    void supportsGetGeneratedKeys() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsGetGeneratedKeys());
        });
    }

    @Test
    void getSuperTypes() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getSuperTypes("", "", "");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getSuperTables() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getSuperTables("", "", "");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getAttributes() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getAttributes("", "", "", "");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void supportsResultSetHoldability() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT));
            assertTrue(dm.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        });
    }

    @Test
    void getResultSetHoldability() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, dm.getResultSetHoldability());
        });
    }

    @Test
    void getDatabaseMajorVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals((int) ((ClickHouseConnection) connection).serverContext().majorVersion(), dm.getDatabaseMajorVersion());
        });
    }

    @Test
    void getDatabaseMinorVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals((int) ((ClickHouseConnection) connection).serverContext().minorVersion(), dm.getDatabaseMinorVersion());
        });
    }

    @Test
    void getJDBCMajorVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(ClickHouseDefines.MAJOR_VERSION, dm.getJDBCMajorVersion());
        });
    }

    @Test
    void getJDBCMinorVersion() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(ClickHouseDefines.MINOR_VERSION, dm.getJDBCMinorVersion());
        });
    }

    @Test
    void getSQLStateType() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(DatabaseMetaData.sqlStateSQL, dm.getSQLStateType());
        });
    }

    @Test
    void locatorsUpdateCopy() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.locatorsUpdateCopy());
        });
    }

    @Test
    void supportsStatementPooling() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsStatementPooling());
        });
    }

    @Test
    void getRowIdLifetime() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertEquals(RowIdLifetime.ROWID_UNSUPPORTED, dm.getRowIdLifetime());
        });
    }

    @Test
    void supportsStoredFunctionsUsingCallSyntax() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.supportsStoredFunctionsUsingCallSyntax());
        });
    }

    @Test
    void autoCommitFailureClosesAllResultSets() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.autoCommitFailureClosesAllResultSets());
        });
    }

    @Test
    void getClientInfoProperties() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getClientInfoProperties();
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getFunctions() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getFunctions("", "", "");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void getFunctionColumns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getFunctionColumns("", "", "", "");
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertFalse(rs.next());
        });
    }

    @Test
    void unwrap() {
    }

    @Test
    void isWrapperFor() {
    }

    @Test
    void getPseudoColumns() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertNull(dm.getPseudoColumns("", "", "", ""));
        });
    }

    @Test
    void generatedKeyAlwaysReturned() throws Exception {
        withNewConnection(connection -> {
            DatabaseMetaData dm = connection.getMetaData();
            assertFalse(dm.generatedKeyAlwaysReturned());
        });
    }
}
