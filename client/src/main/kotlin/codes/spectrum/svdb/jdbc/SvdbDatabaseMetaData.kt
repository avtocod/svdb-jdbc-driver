package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.ProjectInfo
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.sql.RowIdLifetime

class SvdbDatabaseMetaData(
    private val svdbJdbcSysData: SvdbJdbcSysData,
    private val dbUser: String = "default"
) : DatabaseMetaData {
    override fun <T : Any?> unwrap(iface: Class<T>?): T  = TODO("method name ${retriveFunName()} called")

    override fun isWrapperFor(iface: Class<*>?): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun allProceduresAreCallable(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun allTablesAreSelectable(): Boolean = true

    override fun getURL(): String  = TODO("method name ${retriveFunName()} called")

    override fun getUserName(): String = dbUser

    override fun isReadOnly(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun nullsAreSortedHigh(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun nullsAreSortedLow(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun nullsAreSortedAtStart(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun nullsAreSortedAtEnd(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun getDatabaseProductName(): String = "SPECTRUM VIRTUAL DATABASE"

    override fun getDatabaseProductVersion(): String = svdbJdbcSysData.version

    override fun getDriverName(): String {
        return "SVDB_DRIVER"
    }

    override fun getDriverVersion(): String {
        return ProjectInfo.version
    }

    override fun getDriverMajorVersion(): Int  = TODO("method name ${retriveFunName()} called")

    override fun getDriverMinorVersion(): Int  = TODO("method name ${retriveFunName()} called")

    override fun usesLocalFiles(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun usesLocalFilePerTable(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun supportsMixedCaseIdentifiers(): Boolean = true

    override fun storesUpperCaseIdentifiers(): Boolean {
        return false
    }

    override fun storesLowerCaseIdentifiers(): Boolean {
        return true
    }

    override fun storesMixedCaseIdentifiers(): Boolean {
        return false
    }

    override fun supportsMixedCaseQuotedIdentifiers(): Boolean {
        return false
    }

    override fun storesUpperCaseQuotedIdentifiers(): Boolean {
        return false
    }

    override fun storesLowerCaseQuotedIdentifiers(): Boolean {
        return false
    }

    override fun storesMixedCaseQuotedIdentifiers(): Boolean {
        return false
    }

    override fun getIdentifierQuoteString(): String? {
        return null
    }

    override fun getSQLKeywords(): String {
        return "EXPLAIN,EXPLICIT_FIELDS,DEMO_MODE,LOCAL_NODE,SEQ_FULL_SCAN," +
                "RND_FULL_SCAN,REWRITE,APPEND,DEBUG_LOG"
    }

    override fun getNumericFunctions(): String? {
        return null
    }

    override fun getStringFunctions(): String? {
        return null
    }

    override fun getSystemFunctions(): String? {
        return null
    }

    override fun getTimeDateFunctions(): String? {
        return null
    }

    override fun getSearchStringEscape(): String = "\\"

    override fun getExtraNameCharacters(): String? {
        return null
    }

    override fun supportsAlterTableWithAddColumn(): Boolean = false

    override fun supportsAlterTableWithDropColumn(): Boolean = false

    override fun supportsColumnAliasing(): Boolean {
        return true
    }

    override fun nullPlusNonNullIsNull(): Boolean {
       return true
    }

    override fun supportsConvert(): Boolean {
        return false
    }

    override fun supportsConvert(fromType: Int, toType: Int): Boolean {
        return false
    }

    override fun supportsTableCorrelationNames(): Boolean {
       return true
    }

    override fun supportsDifferentTableCorrelationNames(): Boolean {
       return true
    }

    override fun supportsExpressionsInOrderBy(): Boolean {
        return false
    }

    override fun supportsOrderByUnrelated(): Boolean {
        return false
    }

    override fun supportsGroupBy(): Boolean {
        return false
    }

    override fun supportsGroupByUnrelated(): Boolean {
        return false
    }

    override fun supportsGroupByBeyondSelect(): Boolean {
        return false
    }

    override fun supportsLikeEscapeClause(): Boolean {
        return false
    }

    override fun supportsMultipleResultSets(): Boolean {
        return false
    }

    override fun supportsMultipleTransactions(): Boolean {
        return false
    }

    override fun supportsNonNullableColumns(): Boolean {
        return true
    }

    override fun supportsMinimumSQLGrammar(): Boolean {
        return false
    }

    override fun supportsCoreSQLGrammar(): Boolean {
        return false
    }

    override fun supportsExtendedSQLGrammar(): Boolean {
        return false
    }

    override fun supportsANSI92EntryLevelSQL(): Boolean {
        return false
    }

    override fun supportsANSI92IntermediateSQL(): Boolean {
        return false
    }

    override fun supportsANSI92FullSQL(): Boolean {
       return  false
    }

    override fun supportsIntegrityEnhancementFacility(): Boolean {
        return false
    }

    override fun supportsOuterJoins(): Boolean = false

    override fun supportsFullOuterJoins(): Boolean = false

    override fun supportsLimitedOuterJoins(): Boolean = false

    override fun getSchemaTerm(): String = "schema"

    override fun getProcedureTerm(): String? {
        return null
    }

    override fun getCatalogTerm(): String = "database"

    override fun isCatalogAtStart(): Boolean = false

    override fun getCatalogSeparator(): String = "."

    override fun supportsSchemasInDataManipulation(): Boolean = true

    override fun supportsSchemasInProcedureCalls(): Boolean = false

    override fun supportsSchemasInTableDefinitions(): Boolean = true

    override fun supportsSchemasInIndexDefinitions(): Boolean = false

    override fun supportsSchemasInPrivilegeDefinitions(): Boolean = false

    override fun supportsCatalogsInDataManipulation(): Boolean = false

    override fun supportsCatalogsInProcedureCalls(): Boolean = false

    override fun supportsCatalogsInTableDefinitions(): Boolean = false

    override fun supportsCatalogsInIndexDefinitions(): Boolean = false

    override fun supportsCatalogsInPrivilegeDefinitions(): Boolean = false

    override fun supportsPositionedDelete(): Boolean = false

    override fun supportsPositionedUpdate(): Boolean = false

    override fun supportsSelectForUpdate(): Boolean = false

    override fun supportsStoredProcedures(): Boolean = false

    override fun supportsSubqueriesInComparisons(): Boolean = false

    override fun supportsSubqueriesInExists(): Boolean = false

    override fun supportsSubqueriesInIns(): Boolean = false

    override fun supportsSubqueriesInQuantifieds(): Boolean = false

    override fun supportsCorrelatedSubqueries(): Boolean = false

    override fun supportsUnion(): Boolean = false

    override fun supportsUnionAll(): Boolean = false

    override fun supportsOpenCursorsAcrossCommit(): Boolean = false

    override fun supportsOpenCursorsAcrossRollback(): Boolean = false

    override fun supportsOpenStatementsAcrossCommit(): Boolean = false

    override fun supportsOpenStatementsAcrossRollback(): Boolean = false

    override fun getMaxBinaryLiteralLength(): Int {
        return 1024 * 1024
    }

    override fun getMaxCharLiteralLength(): Int {
        return 1024 * 1024
    }

    override fun getMaxColumnNameLength(): Int {
        return 32
    }

    override fun getMaxColumnsInGroupBy(): Int {
        return 8
    }

    override fun getMaxColumnsInIndex(): Int {
        return 8
    }

    override fun getMaxColumnsInOrderBy(): Int {
        return 8
    }

    override fun getMaxColumnsInSelect(): Int {
        return 128
    }

    override fun getMaxColumnsInTable(): Int {
        return 128
    }

    override fun getMaxConnections(): Int {
        return 1024
    }

    override fun getMaxCursorNameLength(): Int {
        return 128
    }

    override fun getMaxIndexLength(): Int {
        return 128
    }

    override fun getMaxSchemaNameLength(): Int {
        return 32
    }

    override fun getMaxProcedureNameLength(): Int {
        return 64
    }

    override fun getMaxCatalogNameLength(): Int {
        return 32
    }

    override fun getMaxRowSize(): Int {
        return  1024 * 1024 * 2
    }

    override fun doesMaxRowSizeIncludeBlobs(): Boolean {
        return true
    }

    override fun getMaxStatementLength(): Int {
        return 8192
    }

    override fun getMaxStatements(): Int {
        return 1024
    }

    override fun getMaxTableNameLength(): Int {
        return 32
    }

    override fun getMaxTablesInSelect(): Int {
        return 1
    }

    override fun getMaxUserNameLength(): Int {
        return 32
    }

    override fun getDefaultTransactionIsolation(): Int {
        return 0
    }

    override fun supportsTransactions(): Boolean = false

    override fun supportsTransactionIsolationLevel(level: Int): Boolean {
        return false
    }

    override fun supportsDataDefinitionAndDataManipulationTransactions(): Boolean = false

    override fun supportsDataManipulationTransactionsOnly(): Boolean = false

    override fun dataDefinitionCausesTransactionCommit(): Boolean = false

    override fun dataDefinitionIgnoredInTransactions(): Boolean = false

    override fun getProcedures(catalog: String?, schemaPattern: String?, procedureNamePattern: String?): ResultSet {
       return SvdbJdbcStaticStringResultSet(sequenceOf())
    }

    override fun getProcedureColumns(
        catalog: String?,
        schemaPattern: String?,
        procedureNamePattern: String?,
        columnNamePattern: String?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getTables(
        catalog: String?,
        schemaPattern: String?,
        tableNamePattern: String?,
        types: Array<out String>?
    ): ResultSet {
        return SvdbJdbcStaticStringResultSet(
            svdbJdbcSysData.toSchemasTablesScv(
                catalog = catalog,
                schema = schemaPattern
            ),
        )
    }

    override fun getSchemas(): ResultSet {
        return SvdbJdbcStaticStringResultSet(svdbJdbcSysData.toSchemasScv())
    }

    override fun getSchemas(catalog: String?, schemaPattern: String?): ResultSet {
        return SvdbJdbcStaticStringResultSet(svdbJdbcSysData.toSchemasScv(catalog = catalog))
    }

    override fun getCatalogs(): ResultSet {
        return SvdbJdbcStaticStringResultSet(svdbJdbcSysData.toCatalogsScv())
    }

    override fun getTableTypes(): ResultSet {
        return SvdbJdbcStaticStringResultSet(sequenceOf("TABLE_TYPE", "TABLE"))
    }

    override fun getColumns(
        catalog: String,
        schemaPattern: String,
        tableNamePattern: String,
        columnNamePattern: String?
    ): ResultSet {
        return SvdbJdbcStaticStringResultSet(
            svdbJdbcSysData.toFieldsScv(catalog = catalog, schema = schemaPattern, table = tableNamePattern),
        )
    }

    override fun getColumnPrivileges(
        catalog: String?,
        schema: String?,
        table: String?,
        columnNamePattern: String?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getTablePrivileges(catalog: String?, schemaPattern: String?, tableNamePattern: String?): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getBestRowIdentifier(
        catalog: String?,
        schema: String?,
        table: String?,
        scope: Int,
        nullable: Boolean
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getVersionColumns(catalog: String?, schema: String?, table: String?): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getPrimaryKeys(catalog: String?, schema: String?, table: String?): ResultSet {
       return SvdbJdbcStaticStringResultSet(sequenceOf())
    }

    override fun getImportedKeys(catalog: String?, schema: String?, table: String?): ResultSet {
        return SvdbJdbcStaticStringResultSet(sequenceOf())
    }

    override fun getExportedKeys(catalog: String?, schema: String?, table: String?): ResultSet {
        return SvdbJdbcStaticStringResultSet(sequenceOf())
    }

    override fun getCrossReference(
        parentCatalog: String?,
        parentSchema: String?,
        parentTable: String?,
        foreignCatalog: String?,
        foreignSchema: String?,
        foreignTable: String?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getTypeInfo(): ResultSet {
        return SvdbJdbcStaticStringResultSet(sequenceOf("TABLE_CAT", "text"))
    }

    override fun getIndexInfo(
        catalog: String?,
        schema: String?,
        table: String?,
        unique: Boolean,
        approximate: Boolean
    ): ResultSet {
        return SvdbJdbcStaticStringResultSet(sequenceOf())
    }

    override fun supportsResultSetType(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun supportsResultSetConcurrency(type: Int, concurrency: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun ownUpdatesAreVisible(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun ownDeletesAreVisible(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun ownInsertsAreVisible(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun othersUpdatesAreVisible(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun othersDeletesAreVisible(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun othersInsertsAreVisible(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun updatesAreDetected(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun deletesAreDetected(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun insertsAreDetected(type: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun supportsBatchUpdates(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun getUDTs(
        catalog: String?,
        schemaPattern: String?,
        typeNamePattern: String?,
        types: IntArray?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getConnection(): Connection  = TODO("method name ${retriveFunName()} called")

    override fun supportsSavepoints(): Boolean = false

    override fun supportsNamedParameters(): Boolean = true

    override fun supportsMultipleOpenResults(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun supportsGetGeneratedKeys(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun getSuperTypes(catalog: String?, schemaPattern: String?, typeNamePattern: String?): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getSuperTables(catalog: String?, schemaPattern: String?, tableNamePattern: String?): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getAttributes(
        catalog: String?,
        schemaPattern: String?,
        typeNamePattern: String?,
        attributeNamePattern: String?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun supportsResultSetHoldability(holdability: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun getResultSetHoldability(): Int  = TODO("method name ${retriveFunName()} called")

    override fun getDatabaseMajorVersion(): Int {
        return this.svdbJdbcSysData.version.split(".")[0].toInt()
    }

    override fun getDatabaseMinorVersion(): Int {
        return this.svdbJdbcSysData.version.split(".")[1].toInt()
    }

    override fun getJDBCMajorVersion(): Int  = TODO("method name ${retriveFunName()} called")

    override fun getJDBCMinorVersion(): Int  = TODO("method name ${retriveFunName()} called")

    override fun getSQLStateType(): Int  = TODO("method name ${retriveFunName()} called")

    override fun locatorsUpdateCopy(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun supportsStatementPooling(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun getRowIdLifetime(): RowIdLifetime  = TODO("method name ${retriveFunName()} called")

    override fun supportsStoredFunctionsUsingCallSyntax(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun autoCommitFailureClosesAllResultSets(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun getClientInfoProperties(): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getFunctions(catalog: String?, schemaPattern: String?, functionNamePattern: String?): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getFunctionColumns(
        catalog: String?,
        schemaPattern: String?,
        functionNamePattern: String?,
        columnNamePattern: String?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getPseudoColumns(
        catalog: String?,
        schemaPattern: String?,
        tableNamePattern: String?,
        columnNamePattern: String?
    ): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun generatedKeyAlwaysReturned(): Boolean  = TODO("method name ${retriveFunName()} called")
}
