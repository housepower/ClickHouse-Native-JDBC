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

package com.github.housepower.jdbc.wrapper;

import com.github.housepower.exception.NotImplementedException;
import com.github.housepower.log.Logging;

import java.sql.*;

public interface SQLDatabaseMetadata extends DatabaseMetaData, SQLWrapper, Logging {

    @Override
    default int getDriverMajorVersion() {
        logger().debug("invoke unimplemented method #getDriverMajorVersion()");
        throw new NotImplementedException("unimplemented method #getDriverMajorVersion()");
    }

    @Override
    default int getDriverMinorVersion() {
        logger().debug("invoke unimplemented method #getDriverMinorVersion()");
        throw new NotImplementedException("unimplemented method #getDriverMinorVersion()");
    }

    @Override
    default boolean allProceduresAreCallable() throws SQLException {
        logger().debug("invoke unimplemented method #allProceduresAreCallable()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean allTablesAreSelectable() throws SQLException {
        logger().debug("invoke unimplemented method #allTablesAreSelectable()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getURL() throws SQLException {
        logger().debug("invoke unimplemented method #getURL()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getUserName() throws SQLException {
        logger().debug("invoke unimplemented method #getUserName()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isReadOnly() throws SQLException {
        logger().debug("invoke unimplemented method #isReadOnly()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean nullsAreSortedHigh() throws SQLException {
        logger().debug("invoke unimplemented method #nullsAreSortedHigh()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean nullsAreSortedLow() throws SQLException {
        logger().debug("invoke unimplemented method #nullsAreSortedLow()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean nullsAreSortedAtStart() throws SQLException {
        logger().debug("invoke unimplemented method #nullsAreSortedAtStart()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean nullsAreSortedAtEnd() throws SQLException {
        logger().debug("invoke unimplemented method #nullsAreSortedAtEnd()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getDatabaseProductName() throws SQLException {
        logger().debug("invoke unimplemented method #getDatabaseProductName()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getDatabaseProductVersion() throws SQLException {
        logger().debug("invoke unimplemented method #getDatabaseProductVersion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getDriverName() throws SQLException {
        logger().debug("invoke unimplemented method #getDriverName()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getDriverVersion() throws SQLException {
        logger().debug("invoke unimplemented method #getDriverVersion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean usesLocalFiles() throws SQLException {
        logger().debug("invoke unimplemented method #usesLocalFiles()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean usesLocalFilePerTable() throws SQLException {
        logger().debug("invoke unimplemented method #usesLocalFilePerTable()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsMixedCaseIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #supportsMixedCaseIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean storesUpperCaseIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #storesUpperCaseIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean storesLowerCaseIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #storesLowerCaseIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean storesMixedCaseIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #storesMixedCaseIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #supportsMixedCaseQuotedIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #storesUpperCaseQuotedIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #storesLowerCaseQuotedIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        logger().debug("invoke unimplemented method #storesMixedCaseQuotedIdentifiers()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getIdentifierQuoteString() throws SQLException {
        logger().debug("invoke unimplemented method #getIdentifierQuoteString()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getSQLKeywords() throws SQLException {
        logger().debug("invoke unimplemented method #getSQLKeywords()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getNumericFunctions() throws SQLException {
        logger().debug("invoke unimplemented method #getNumericFunctions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getStringFunctions() throws SQLException {
        logger().debug("invoke unimplemented method #getStringFunctions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getSystemFunctions() throws SQLException {
        logger().debug("invoke unimplemented method #getSystemFunctions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getTimeDateFunctions() throws SQLException {
        logger().debug("invoke unimplemented method #getTimeDateFunctions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getSearchStringEscape() throws SQLException {
        logger().debug("invoke unimplemented method #getSearchStringEscape()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getExtraNameCharacters() throws SQLException {
        logger().debug("invoke unimplemented method #getExtraNameCharacters()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsAlterTableWithAddColumn() throws SQLException {
        logger().debug("invoke unimplemented method #supportsAlterTableWithAddColumn()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsAlterTableWithDropColumn() throws SQLException {
        logger().debug("invoke unimplemented method #supportsAlterTableWithDropColumn()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsColumnAliasing() throws SQLException {
        logger().debug("invoke unimplemented method #supportsColumnAliasing()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean nullPlusNonNullIsNull() throws SQLException {
        logger().debug("invoke unimplemented method #nullPlusNonNullIsNull()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsConvert() throws SQLException {
        logger().debug("invoke unimplemented method #supportsConvert()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsConvert(int fromType, int toType) throws SQLException {
        logger().debug("invoke unimplemented method #supportsConvert(int fromType, int toType)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsTableCorrelationNames() throws SQLException {
        logger().debug("invoke unimplemented method #supportsTableCorrelationNames()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsDifferentTableCorrelationNames() throws SQLException {
        logger().debug("invoke unimplemented method #supportsDifferentTableCorrelationNames()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsExpressionsInOrderBy() throws SQLException {
        logger().debug("invoke unimplemented method #supportsExpressionsInOrderBy()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsOrderByUnrelated() throws SQLException {
        logger().debug("invoke unimplemented method #supportsOrderByUnrelated()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsGroupBy() throws SQLException {
        logger().debug("invoke unimplemented method #supportsGroupBy()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsGroupByUnrelated() throws SQLException {
        logger().debug("invoke unimplemented method #supportsGroupByUnrelated()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsGroupByBeyondSelect() throws SQLException {
        logger().debug("invoke unimplemented method #supportsGroupByBeyondSelect()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsLikeEscapeClause() throws SQLException {
        logger().debug("invoke unimplemented method #supportsLikeEscapeClause()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsMultipleResultSets() throws SQLException {
        logger().debug("invoke unimplemented method #supportsMultipleResultSets()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsMultipleTransactions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsMultipleTransactions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsNonNullableColumns() throws SQLException {
        logger().debug("invoke unimplemented method #supportsNonNullableColumns()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsMinimumSQLGrammar() throws SQLException {
        logger().debug("invoke unimplemented method #supportsMinimumSQLGrammar()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCoreSQLGrammar() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCoreSQLGrammar()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsExtendedSQLGrammar() throws SQLException {
        logger().debug("invoke unimplemented method #supportsExtendedSQLGrammar()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsANSI92EntryLevelSQL() throws SQLException {
        logger().debug("invoke unimplemented method #supportsANSI92EntryLevelSQL()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsANSI92IntermediateSQL() throws SQLException {
        logger().debug("invoke unimplemented method #supportsANSI92IntermediateSQL()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsANSI92FullSQL() throws SQLException {
        logger().debug("invoke unimplemented method #supportsANSI92FullSQL()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsIntegrityEnhancementFacility() throws SQLException {
        logger().debug("invoke unimplemented method #supportsIntegrityEnhancementFacility()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsOuterJoins() throws SQLException {
        logger().debug("invoke unimplemented method #supportsOuterJoins()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsFullOuterJoins() throws SQLException {
        logger().debug("invoke unimplemented method #supportsFullOuterJoins()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsLimitedOuterJoins() throws SQLException {
        logger().debug("invoke unimplemented method #supportsLimitedOuterJoins()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getSchemaTerm() throws SQLException {
        logger().debug("invoke unimplemented method #getSchemaTerm()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getProcedureTerm() throws SQLException {
        logger().debug("invoke unimplemented method #getProcedureTerm()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getCatalogTerm() throws SQLException {
        logger().debug("invoke unimplemented method #getCatalogTerm()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean isCatalogAtStart() throws SQLException {
        logger().debug("invoke unimplemented method #isCatalogAtStart()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default String getCatalogSeparator() throws SQLException {
        logger().debug("invoke unimplemented method #getCatalogSeparator()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSchemasInDataManipulation() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSchemasInDataManipulation()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSchemasInProcedureCalls() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSchemasInProcedureCalls()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSchemasInTableDefinitions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSchemasInTableDefinitions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSchemasInIndexDefinitions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSchemasInIndexDefinitions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSchemasInPrivilegeDefinitions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCatalogsInDataManipulation() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCatalogsInDataManipulation()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCatalogsInProcedureCalls() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCatalogsInProcedureCalls()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCatalogsInTableDefinitions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCatalogsInTableDefinitions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCatalogsInIndexDefinitions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCatalogsInPrivilegeDefinitions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsPositionedDelete() throws SQLException {
        logger().debug("invoke unimplemented method #supportsPositionedDelete()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsPositionedUpdate() throws SQLException {
        logger().debug("invoke unimplemented method #supportsPositionedUpdate()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSelectForUpdate() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSelectForUpdate()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsStoredProcedures() throws SQLException {
        logger().debug("invoke unimplemented method #supportsStoredProcedures()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSubqueriesInComparisons() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSubqueriesInComparisons()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSubqueriesInExists() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSubqueriesInExists()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSubqueriesInIns() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSubqueriesInIns()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSubqueriesInQuantifieds() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSubqueriesInQuantifieds()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsCorrelatedSubqueries() throws SQLException {
        logger().debug("invoke unimplemented method #supportsCorrelatedSubqueries()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsUnion() throws SQLException {
        logger().debug("invoke unimplemented method #supportsUnion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsUnionAll() throws SQLException {
        logger().debug("invoke unimplemented method #supportsUnionAll()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        logger().debug("invoke unimplemented method #supportsOpenCursorsAcrossCommit()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        logger().debug("invoke unimplemented method #supportsOpenCursorsAcrossRollback()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        logger().debug("invoke unimplemented method #supportsOpenStatementsAcrossCommit()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        logger().debug("invoke unimplemented method #supportsOpenStatementsAcrossRollback()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxBinaryLiteralLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxBinaryLiteralLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxCharLiteralLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxCharLiteralLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxColumnNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxColumnNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxColumnsInGroupBy() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxColumnsInGroupBy()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxColumnsInIndex() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxColumnsInIndex()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxColumnsInOrderBy() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxColumnsInOrderBy()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxColumnsInSelect() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxColumnsInSelect()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxColumnsInTable() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxColumnsInTable()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxConnections() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxConnections()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxCursorNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxCursorNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxIndexLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxIndexLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxSchemaNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxSchemaNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxProcedureNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxProcedureNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxCatalogNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxCatalogNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxRowSize() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxRowSize()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        logger().debug("invoke unimplemented method #doesMaxRowSizeIncludeBlobs()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxStatementLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxStatementLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxStatements() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxStatements()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxTableNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxTableNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxTablesInSelect() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxTablesInSelect()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getMaxUserNameLength() throws SQLException {
        logger().debug("invoke unimplemented method #getMaxUserNameLength()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getDefaultTransactionIsolation() throws SQLException {
        logger().debug("invoke unimplemented method #getDefaultTransactionIsolation()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsTransactions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsTransactions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        logger().debug("invoke unimplemented method #supportsTransactionIsolationLevel(int level)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        logger().debug("invoke unimplemented method #supportsDataDefinitionAndDataManipulationTransactions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        logger().debug("invoke unimplemented method #supportsDataManipulationTransactionsOnly()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        logger().debug("invoke unimplemented method #dataDefinitionCausesTransactionCommit()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        logger().debug("invoke unimplemented method #dataDefinitionIgnoredInTransactions()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getProcedures(String catalog, String schemaPattern, String procedureNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        logger().debug("invoke unimplemented method #getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getSchemas() throws SQLException {
        logger().debug("invoke unimplemented method #getSchemas()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getCatalogs() throws SQLException {
        logger().debug("invoke unimplemented method #getCatalogs()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getTableTypes() throws SQLException {
        logger().debug("invoke unimplemented method #getTableTypes()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        logger().debug("invoke unimplemented method #getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        logger().debug("invoke unimplemented method #getVersionColumns(String catalog, String schema, String table)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        logger().debug("invoke unimplemented method #getPrimaryKeys(String catalog, String schema, String table)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        logger().debug("invoke unimplemented method #getImportedKeys(String catalog, String schema, String table)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        logger().debug("invoke unimplemented method #getExportedKeys(String catalog, String schema, String table)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        logger().debug("invoke unimplemented method #getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getTypeInfo() throws SQLException {
        logger().debug("invoke unimplemented method #getTypeInfo()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        logger().debug("invoke unimplemented method #getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsResultSetType(int type) throws SQLException {
        logger().debug("invoke unimplemented method #supportsResultSetType(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        logger().debug("invoke unimplemented method #supportsResultSetConcurrency(int type, int concurrency)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean ownUpdatesAreVisible(int type) throws SQLException {
        logger().debug("invoke unimplemented method #ownUpdatesAreVisible(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean ownDeletesAreVisible(int type) throws SQLException {
        logger().debug("invoke unimplemented method #ownDeletesAreVisible(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean ownInsertsAreVisible(int type) throws SQLException {
        logger().debug("invoke unimplemented method #ownInsertsAreVisible(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean othersUpdatesAreVisible(int type) throws SQLException {
        logger().debug("invoke unimplemented method #othersUpdatesAreVisible(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean othersDeletesAreVisible(int type) throws SQLException {
        logger().debug("invoke unimplemented method #othersDeletesAreVisible(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean othersInsertsAreVisible(int type) throws SQLException {
        logger().debug("invoke unimplemented method #othersInsertsAreVisible(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean updatesAreDetected(int type) throws SQLException {
        logger().debug("invoke unimplemented method #updatesAreDetected(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean deletesAreDetected(int type) throws SQLException {
        logger().debug("invoke unimplemented method #deletesAreDetected(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean insertsAreDetected(int type) throws SQLException {
        logger().debug("invoke unimplemented method #insertsAreDetected(int type)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsBatchUpdates() throws SQLException {
        logger().debug("invoke unimplemented method #supportsBatchUpdates()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        logger().debug("invoke unimplemented method #getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default Connection getConnection() throws SQLException {
        logger().debug("invoke unimplemented method #getConnection()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsSavepoints() throws SQLException {
        logger().debug("invoke unimplemented method #supportsSavepoints()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsNamedParameters() throws SQLException {
        logger().debug("invoke unimplemented method #supportsNamedParameters()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsMultipleOpenResults() throws SQLException {
        logger().debug("invoke unimplemented method #supportsMultipleOpenResults()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsGetGeneratedKeys() throws SQLException {
        logger().debug("invoke unimplemented method #supportsGetGeneratedKeys()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getSuperTables(String catalog, String schemaPattern, String tableNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsResultSetHoldability(int holdability) throws SQLException {
        logger().debug("invoke unimplemented method #supportsResultSetHoldability(int holdability)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getResultSetHoldability() throws SQLException {
        logger().debug("invoke unimplemented method #getResultSetHoldability()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getDatabaseMajorVersion() throws SQLException {
        logger().debug("invoke unimplemented method #getDatabaseMajorVersion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getDatabaseMinorVersion() throws SQLException {
        logger().debug("invoke unimplemented method #getDatabaseMinorVersion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getJDBCMajorVersion() throws SQLException {
        logger().debug("invoke unimplemented method #getJDBCMajorVersion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getJDBCMinorVersion() throws SQLException {
        logger().debug("invoke unimplemented method #getJDBCMinorVersion()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default int getSQLStateType() throws SQLException {
        logger().debug("invoke unimplemented method #getSQLStateType()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean locatorsUpdateCopy() throws SQLException {
        logger().debug("invoke unimplemented method #locatorsUpdateCopy()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsStatementPooling() throws SQLException {
        logger().debug("invoke unimplemented method #supportsStatementPooling()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default RowIdLifetime getRowIdLifetime() throws SQLException {
        logger().debug("invoke unimplemented method #getRowIdLifetime()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        logger().debug("invoke unimplemented method #getSchemas(String catalog, String schemaPattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        logger().debug("invoke unimplemented method #supportsStoredFunctionsUsingCallSyntax()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        logger().debug("invoke unimplemented method #autoCommitFailureClosesAllResultSets()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getClientInfoProperties() throws SQLException {
        logger().debug("invoke unimplemented method #getClientInfoProperties()");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getFunctions(String catalog, String schemaPattern, String functionNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        logger().debug("invoke unimplemented method #getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)");
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    default boolean generatedKeyAlwaysReturned() throws SQLException {
        logger().debug("invoke unimplemented method #generatedKeyAlwaysReturned()");
        throw new SQLFeatureNotSupportedException();
    }
}
