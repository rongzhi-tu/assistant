/*
 *    Copyright 2009-2014 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.util.*;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.loader.cglib.CglibProxyFactory;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j.Log4jImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.InterceptorChain;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Clinton Begin
 * @description 重写put，实现刷新的功能
 */
public class Configuration{

	protected Environment environment;
	protected boolean safeRowBoundsEnabled;
	protected boolean safeResultHandlerEnabled;
	protected boolean mapUnderscoreToCamelCase;
	protected boolean aggressiveLazyLoading;
	protected boolean multipleResultSetsEnabled;
	protected boolean useGeneratedKeys;
	protected boolean useColumnLabel;
	protected boolean cacheEnabled;
	protected boolean callSettersOnNulls;
	protected boolean useActualParamName;
	protected boolean returnInstanceForEmptyRow;
	protected String logPrefix;
	protected Class<? extends Log> logImpl;
	protected Class<? extends VFS> vfsImpl;
	protected LocalCacheScope localCacheScope;
	protected JdbcType jdbcTypeForNull;
	protected Set<String> lazyLoadTriggerMethods;
	protected Integer defaultStatementTimeout;
	protected Integer defaultFetchSize;
	protected ResultSetType defaultResultSetType;
	protected ExecutorType defaultExecutorType;
	protected AutoMappingBehavior autoMappingBehavior;
	protected AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior;
	protected Properties variables;
	protected ReflectorFactory reflectorFactory;
	protected ObjectFactory objectFactory;
	protected ObjectWrapperFactory objectWrapperFactory;
	protected boolean lazyLoadingEnabled;
	protected ProxyFactory proxyFactory;
	protected String databaseId;
	protected Class<?> configurationFactory;
	protected final MapperRegistry mapperRegistry;
	protected final InterceptorChain interceptorChain;
	protected final TypeHandlerRegistry typeHandlerRegistry;
	protected final TypeAliasRegistry typeAliasRegistry;
	protected final LanguageDriverRegistry languageRegistry;
	protected final Map<String, MappedStatement> mappedStatements;
	protected final Map<String, Cache> caches;
	protected final Map<String, ResultMap> resultMaps;
	protected final Map<String, ParameterMap> parameterMaps;
	protected final Map<String, KeyGenerator> keyGenerators;
	protected final Set<String> loadedResources;
	protected final Map<String, XNode> sqlFragments;
	protected final Collection<XMLStatementBuilder> incompleteStatements;
	protected final Collection<CacheRefResolver> incompleteCacheRefs;
	protected final Collection<ResultMapResolver> incompleteResultMaps;
	protected final Collection<MethodResolver> incompleteMethods;
	protected final Map<String, String> cacheRefMap;

	public Configuration(Environment environment) {
		this();
		this.environment = environment;
	}

	public Configuration() {
		this.safeResultHandlerEnabled = true;
		this.multipleResultSetsEnabled = true;
		this.useColumnLabel = true;
		this.cacheEnabled = true;
		this.useActualParamName = true;
		this.localCacheScope = LocalCacheScope.SESSION;
		this.jdbcTypeForNull = JdbcType.OTHER;
		this.lazyLoadTriggerMethods = new HashSet(Arrays.asList("equals", "clone", "hashCode", "toString"));
		this.defaultExecutorType = ExecutorType.SIMPLE;
		this.autoMappingBehavior = AutoMappingBehavior.PARTIAL;
		this.autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;
		this.variables = new Properties();
		this.reflectorFactory = new DefaultReflectorFactory();
		this.objectFactory = new DefaultObjectFactory();
		this.objectWrapperFactory = new DefaultObjectWrapperFactory();
		this.lazyLoadingEnabled = false;
		this.proxyFactory = new JavassistProxyFactory();
		this.mapperRegistry = new MapperRegistry(this);
		this.interceptorChain = new InterceptorChain();
		this.typeHandlerRegistry = new TypeHandlerRegistry();
		this.typeAliasRegistry = new TypeAliasRegistry();
		this.languageRegistry = new LanguageDriverRegistry();
		this.mappedStatements = new HashMap<>();
		this.caches = new StrictMap("Caches collection");
		this.resultMaps = new StrictMap("Result Maps collection");
		this.parameterMaps = new StrictMap("Parameter Maps collection");
		this.keyGenerators = new StrictMap("Key Generators collection");
		this.loadedResources = new HashSet();
		this.sqlFragments = new StrictMap("XML fragments parsed from previous mappers");
		this.incompleteStatements = new LinkedList();
		this.incompleteCacheRefs = new LinkedList();
		this.incompleteResultMaps = new LinkedList();
		this.incompleteMethods = new LinkedList();
		this.cacheRefMap = new HashMap();
		this.typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
		this.typeAliasRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);
		this.typeAliasRegistry.registerAlias("JNDI", JndiDataSourceFactory.class);
		this.typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
		this.typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
		this.typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
		this.typeAliasRegistry.registerAlias("FIFO", FifoCache.class);
		this.typeAliasRegistry.registerAlias("LRU", LruCache.class);
		this.typeAliasRegistry.registerAlias("SOFT", SoftCache.class);
		this.typeAliasRegistry.registerAlias("WEAK", WeakCache.class);
		this.typeAliasRegistry.registerAlias("DB_VENDOR", VendorDatabaseIdProvider.class);
		this.typeAliasRegistry.registerAlias("XML", XMLLanguageDriver.class);
		this.typeAliasRegistry.registerAlias("RAW", RawLanguageDriver.class);
		this.typeAliasRegistry.registerAlias("SLF4J", Slf4jImpl.class);
		this.typeAliasRegistry.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
		this.typeAliasRegistry.registerAlias("LOG4J", Log4jImpl.class);
		this.typeAliasRegistry.registerAlias("LOG4J2", Log4j2Impl.class);
		this.typeAliasRegistry.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
		this.typeAliasRegistry.registerAlias("STDOUT_LOGGING", StdOutImpl.class);
		this.typeAliasRegistry.registerAlias("NO_LOGGING", NoLoggingImpl.class);
		this.typeAliasRegistry.registerAlias("CGLIB", CglibProxyFactory.class);
		this.typeAliasRegistry.registerAlias("JAVASSIST", JavassistProxyFactory.class);
		this.languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
		this.languageRegistry.register(RawLanguageDriver.class);
	}

	public String getLogPrefix() {
		return this.logPrefix;
	}

	public void setLogPrefix(String logPrefix) {
		this.logPrefix = logPrefix;
	}

	public Class<? extends Log> getLogImpl() {
		return this.logImpl;
	}

	public void setLogImpl(Class<? extends Log> logImpl) {
		if (logImpl != null) {
			this.logImpl = logImpl;
			LogFactory.useCustomLogging(this.logImpl);
		}

	}

	public Class<? extends VFS> getVfsImpl() {
		return this.vfsImpl;
	}

	public void setVfsImpl(Class<? extends VFS> vfsImpl) {
		if (vfsImpl != null) {
			this.vfsImpl = vfsImpl;
			VFS.addImplClass(this.vfsImpl);
		}

	}

	public boolean isCallSettersOnNulls() {
		return this.callSettersOnNulls;
	}

	public void setCallSettersOnNulls(boolean callSettersOnNulls) {
		this.callSettersOnNulls = callSettersOnNulls;
	}

	public boolean isUseActualParamName() {
		return this.useActualParamName;
	}

	public void setUseActualParamName(boolean useActualParamName) {
		this.useActualParamName = useActualParamName;
	}

	public boolean isReturnInstanceForEmptyRow() {
		return this.returnInstanceForEmptyRow;
	}

	public void setReturnInstanceForEmptyRow(boolean returnEmptyInstance) {
		this.returnInstanceForEmptyRow = returnEmptyInstance;
	}

	public String getDatabaseId() {
		return this.databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public Class<?> getConfigurationFactory() {
		return this.configurationFactory;
	}

	public void setConfigurationFactory(Class<?> configurationFactory) {
		this.configurationFactory = configurationFactory;
	}

	public boolean isSafeResultHandlerEnabled() {
		return this.safeResultHandlerEnabled;
	}

	public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
		this.safeResultHandlerEnabled = safeResultHandlerEnabled;
	}

	public boolean isSafeRowBoundsEnabled() {
		return this.safeRowBoundsEnabled;
	}

	public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
		this.safeRowBoundsEnabled = safeRowBoundsEnabled;
	}

	public boolean isMapUnderscoreToCamelCase() {
		return this.mapUnderscoreToCamelCase;
	}

	public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
		this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
	}

	public void addLoadedResource(String resource) {
		this.loadedResources.add(resource);
	}

	public boolean isResourceLoaded(String resource) {
		return this.loadedResources.contains(resource);
	}

	public Environment getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public AutoMappingBehavior getAutoMappingBehavior() {
		return this.autoMappingBehavior;
	}

	public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
		this.autoMappingBehavior = autoMappingBehavior;
	}

	public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
		return this.autoMappingUnknownColumnBehavior;
	}

	public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
		this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
	}

	public boolean isLazyLoadingEnabled() {
		return this.lazyLoadingEnabled;
	}

	public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
		this.lazyLoadingEnabled = lazyLoadingEnabled;
	}

	public ProxyFactory getProxyFactory() {
		return this.proxyFactory;
	}

	public void setProxyFactory(ProxyFactory proxyFactory) {
		if (proxyFactory == null) {
			proxyFactory = new JavassistProxyFactory();
		}

		this.proxyFactory = (ProxyFactory)proxyFactory;
	}

	public boolean isAggressiveLazyLoading() {
		return this.aggressiveLazyLoading;
	}

	public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
		this.aggressiveLazyLoading = aggressiveLazyLoading;
	}

	public boolean isMultipleResultSetsEnabled() {
		return this.multipleResultSetsEnabled;
	}

	public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
		this.multipleResultSetsEnabled = multipleResultSetsEnabled;
	}

	public Set<String> getLazyLoadTriggerMethods() {
		return this.lazyLoadTriggerMethods;
	}

	public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
		this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
	}

	public boolean isUseGeneratedKeys() {
		return this.useGeneratedKeys;
	}

	public void setUseGeneratedKeys(boolean useGeneratedKeys) {
		this.useGeneratedKeys = useGeneratedKeys;
	}

	public ExecutorType getDefaultExecutorType() {
		return this.defaultExecutorType;
	}

	public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
		this.defaultExecutorType = defaultExecutorType;
	}

	public boolean isCacheEnabled() {
		return this.cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public Integer getDefaultStatementTimeout() {
		return this.defaultStatementTimeout;
	}

	public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
		this.defaultStatementTimeout = defaultStatementTimeout;
	}

	public Integer getDefaultFetchSize() {
		return this.defaultFetchSize;
	}

	public void setDefaultFetchSize(Integer defaultFetchSize) {
		this.defaultFetchSize = defaultFetchSize;
	}

	public ResultSetType getDefaultResultSetType() {
		return this.defaultResultSetType;
	}

	public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
		this.defaultResultSetType = defaultResultSetType;
	}

	public boolean isUseColumnLabel() {
		return this.useColumnLabel;
	}

	public void setUseColumnLabel(boolean useColumnLabel) {
		this.useColumnLabel = useColumnLabel;
	}

	public LocalCacheScope getLocalCacheScope() {
		return this.localCacheScope;
	}

	public void setLocalCacheScope(LocalCacheScope localCacheScope) {
		this.localCacheScope = localCacheScope;
	}

	public JdbcType getJdbcTypeForNull() {
		return this.jdbcTypeForNull;
	}

	public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
		this.jdbcTypeForNull = jdbcTypeForNull;
	}

	public Properties getVariables() {
		return this.variables;
	}

	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return this.typeHandlerRegistry;
	}

	public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandler) {
		if (typeHandler != null) {
			this.getTypeHandlerRegistry().setDefaultEnumTypeHandler(typeHandler);
		}

	}

	public TypeAliasRegistry getTypeAliasRegistry() {
		return this.typeAliasRegistry;
	}

	public MapperRegistry getMapperRegistry() {
		return this.mapperRegistry;
	}

	public ReflectorFactory getReflectorFactory() {
		return this.reflectorFactory;
	}

	public void setReflectorFactory(ReflectorFactory reflectorFactory) {
		this.reflectorFactory = reflectorFactory;
	}

	public ObjectFactory getObjectFactory() {
		return this.objectFactory;
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public ObjectWrapperFactory getObjectWrapperFactory() {
		return this.objectWrapperFactory;
	}

	public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
		this.objectWrapperFactory = objectWrapperFactory;
	}

	public List<Interceptor> getInterceptors() {
		return this.interceptorChain.getInterceptors();
	}

	public LanguageDriverRegistry getLanguageRegistry() {
		return this.languageRegistry;
	}

	public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
		if (driver == null) {
			driver = XMLLanguageDriver.class;
		}

		this.getLanguageRegistry().setDefaultDriverClass(driver);
	}

	public LanguageDriver getDefaultScriptingLanguageInstance() {
		return this.languageRegistry.getDefaultDriver();
	}

	public LanguageDriver getLanguageDriver(Class<? extends LanguageDriver> langClass) {
		if (langClass == null) {
			return this.languageRegistry.getDefaultDriver();
		} else {
			this.languageRegistry.register(langClass);
			return this.languageRegistry.getDriver(langClass);
		}
	}

	/** @deprecated */
	@Deprecated
	public LanguageDriver getDefaultScriptingLanuageInstance() {
		return this.getDefaultScriptingLanguageInstance();
	}

	public MetaObject newMetaObject(Object object) {
		return MetaObject.forObject(object, this.objectFactory, this.objectWrapperFactory, this.reflectorFactory);
	}

	public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
		parameterHandler = (ParameterHandler)this.interceptorChain.pluginAll(parameterHandler);
		return parameterHandler;
	}

	public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
		ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
		ResultSetHandler resultSetHandler1 = (ResultSetHandler)this.interceptorChain.pluginAll(resultSetHandler);
		return resultSetHandler1;
	}

	public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
		StatementHandler statementHandler1= (StatementHandler)this.interceptorChain.pluginAll(statementHandler);
		return statementHandler1;
	}

	public Executor newExecutor(Transaction transaction) {
		return this.newExecutor(transaction, this.defaultExecutorType);
	}

	public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
		executorType = executorType == null ? this.defaultExecutorType : executorType;
		executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
		Object executor;
		if (ExecutorType.BATCH == executorType) {
			executor = new BatchExecutor(this, transaction);
		} else if (ExecutorType.REUSE == executorType) {
			executor = new ReuseExecutor(this, transaction);
		} else {
			executor = new SimpleExecutor(this, transaction);
		}

		if (this.cacheEnabled) {
			executor = new CachingExecutor((Executor)executor);
		}

		Executor executor1 = (Executor)this.interceptorChain.pluginAll(executor);
		return executor1;
	}

	public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
		this.keyGenerators.put(id, keyGenerator);
	}

	public Collection<String> getKeyGeneratorNames() {
		return this.keyGenerators.keySet();
	}

	public Collection<KeyGenerator> getKeyGenerators() {
		return this.keyGenerators.values();
	}

	public KeyGenerator getKeyGenerator(String id) {
		return (KeyGenerator)this.keyGenerators.get(id);
	}

	public boolean hasKeyGenerator(String id) {
		return this.keyGenerators.containsKey(id);
	}

	public void addCache(Cache cache) {
		this.caches.put(cache.getId(), cache);
	}

	public Collection<String> getCacheNames() {
		return this.caches.keySet();
	}

	public Collection<Cache> getCaches() {
		return this.caches.values();
	}

	public Cache getCache(String id) {
		return (Cache)this.caches.get(id);
	}

	public boolean hasCache(String id) {
		return this.caches.containsKey(id);
	}

	public void addResultMap(ResultMap rm) {
		this.resultMaps.put(rm.getId(), rm);
		this.checkLocallyForDiscriminatedNestedResultMaps(rm);
		this.checkGloballyForDiscriminatedNestedResultMaps(rm);
	}

	public Collection<String> getResultMapNames() {
		return this.resultMaps.keySet();
	}

	public Collection<ResultMap> getResultMaps() {
		return this.resultMaps.values();
	}

	public ResultMap getResultMap(String id) {
		return (ResultMap)this.resultMaps.get(id);
	}

	public boolean hasResultMap(String id) {
		return this.resultMaps.containsKey(id);
	}

	public void addParameterMap(ParameterMap pm) {
		this.parameterMaps.put(pm.getId(), pm);
	}

	public Collection<String> getParameterMapNames() {
		return this.parameterMaps.keySet();
	}

	public Collection<ParameterMap> getParameterMaps() {
		return this.parameterMaps.values();
	}

	public ParameterMap getParameterMap(String id) {
		return (ParameterMap)this.parameterMaps.get(id);
	}

	public boolean hasParameterMap(String id) {
		return this.parameterMaps.containsKey(id);
	}

	public void addMappedStatement(MappedStatement ms) {
		this.mappedStatements.put(ms.getId(), ms);
	}

	public Collection<String> getMappedStatementNames() {
		this.buildAllStatements();
		return this.mappedStatements.keySet();
	}

	public Collection<MappedStatement> getMappedStatements() {
		this.buildAllStatements();
		return this.mappedStatements.values();
	}

	public Collection<XMLStatementBuilder> getIncompleteStatements() {
		return this.incompleteStatements;
	}

	public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
		this.incompleteStatements.add(incompleteStatement);
	}

	public Collection<CacheRefResolver> getIncompleteCacheRefs() {
		return this.incompleteCacheRefs;
	}

	public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
		this.incompleteCacheRefs.add(incompleteCacheRef);
	}

	public Collection<ResultMapResolver> getIncompleteResultMaps() {
		return this.incompleteResultMaps;
	}

	public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
		this.incompleteResultMaps.add(resultMapResolver);
	}

	public void addIncompleteMethod(MethodResolver builder) {
		this.incompleteMethods.add(builder);
	}

	public Collection<MethodResolver> getIncompleteMethods() {
		return this.incompleteMethods;
	}

	public MappedStatement getMappedStatement(String id) {
		return this.getMappedStatement(id, true);
	}

	public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
		if (validateIncompleteStatements) {
			this.buildAllStatements();
		}

		return (MappedStatement)this.mappedStatements.get(id);
	}

	public Map<String, XNode> getSqlFragments() {
		return this.sqlFragments;
	}

	public void addInterceptor(Interceptor interceptor) {
		this.interceptorChain.addInterceptor(interceptor);
	}

	public void addMappers(String packageName, Class<?> superType) {
		this.mapperRegistry.addMappers(packageName, superType);
	}

	public void addMappers(String packageName) {
		this.mapperRegistry.addMappers(packageName);
	}

	public <T> void addMapper(Class<T> type) {
		this.mapperRegistry.addMapper(type);
	}

	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return this.mapperRegistry.getMapper(type, sqlSession);
	}

	public boolean hasMapper(Class<?> type) {
		return this.mapperRegistry.hasMapper(type);
	}

	public boolean hasStatement(String statementName) {
		return this.hasStatement(statementName, true);
	}

	public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
		if (validateIncompleteStatements) {
			this.buildAllStatements();
		}

		return this.mappedStatements.containsKey(statementName);
	}

	public void addCacheRef(String namespace, String referencedNamespace) {
		this.cacheRefMap.put(namespace, referencedNamespace);
	}

	protected void buildAllStatements() {
		this.parsePendingResultMaps();
		if (!this.incompleteCacheRefs.isEmpty()) {
			synchronized(this.incompleteCacheRefs) {
				this.incompleteCacheRefs.removeIf((x) -> {
					return x.resolveCacheRef() != null;
				});
			}
		}

		if (!this.incompleteStatements.isEmpty()) {
			synchronized(this.incompleteStatements) {
				this.incompleteStatements.removeIf((x) -> {
					x.parseStatementNode();
					return true;
				});
			}
		}

		if (!this.incompleteMethods.isEmpty()) {
			synchronized(this.incompleteMethods) {
				this.incompleteMethods.removeIf((x) -> {
					x.resolve();
					return true;
				});
			}
		}

	}

	private void parsePendingResultMaps() {
		if (!this.incompleteResultMaps.isEmpty()) {
			synchronized(this.incompleteResultMaps) {
				IncompleteElementException ex = null;

				boolean resolved;
				do {
					resolved = false;
					Iterator<ResultMapResolver> iterator = this.incompleteResultMaps.iterator();

					while(iterator.hasNext()) {
						try {
							((ResultMapResolver)iterator.next()).resolve();
							iterator.remove();
							resolved = true;
						} catch (IncompleteElementException var7) {
							ex = var7;
						}
					}
				} while(resolved);

				if (!this.incompleteResultMaps.isEmpty() && ex != null) {
					throw ex;
				}
			}
		}
	}

	protected String extractNamespace(String statementId) {
		int lastPeriod = statementId.lastIndexOf(46);
		return lastPeriod > 0 ? statementId.substring(0, lastPeriod) : null;
	}

	protected void checkGloballyForDiscriminatedNestedResultMaps(ResultMap rm) {
		if (rm.hasNestedResultMaps()) {
			Iterator var2 = this.resultMaps.entrySet().iterator();

			while(var2.hasNext()) {
				Map.Entry<String, ResultMap> entry = (Map.Entry)var2.next();
				Object value = entry.getValue();
				if (value instanceof ResultMap) {
					ResultMap entryResultMap = (ResultMap)value;
					if (!entryResultMap.hasNestedResultMaps() && entryResultMap.getDiscriminator() != null) {
						Collection<String> discriminatedResultMapNames = entryResultMap.getDiscriminator().getDiscriminatorMap().values();
						if (discriminatedResultMapNames.contains(rm.getId())) {
							entryResultMap.forceNestedResultMaps();
						}
					}
				}
			}
		}

	}

	protected void checkLocallyForDiscriminatedNestedResultMaps(ResultMap rm) {
		if (!rm.hasNestedResultMaps() && rm.getDiscriminator() != null) {
			Iterator var2 = rm.getDiscriminator().getDiscriminatorMap().entrySet().iterator();

			while(var2.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry)var2.next();
				String discriminatedResultMapName = (String)entry.getValue();
				if (this.hasResultMap(discriminatedResultMapName)) {
					ResultMap discriminatedResultMap = (ResultMap)this.resultMaps.get(discriminatedResultMapName);
					if (discriminatedResultMap.hasNestedResultMaps()) {
						rm.forceNestedResultMaps();
						break;
					}
				}
			}
		}

	}
	protected static class StrictMap<V> extends HashMap<String, V> {

		private static final long serialVersionUID = -4950446264854982944L;
		private String name;

		public StrictMap(String name, int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
			this.name = name;
		}

		public StrictMap(String name, int initialCapacity) {
			super(initialCapacity);
			this.name = name;
		}

		public StrictMap(String name) {
			super();
			this.name = name;
		}

		public StrictMap(String name, Map<String, ? extends V> m) {
			super(m);
			this.name = name;
		}

		// TODO 如果现在状态为刷新，则刷新(先删除后添加)
		@SuppressWarnings("unchecked")
		public V put(String key, V value) {
			if (org.apache.ibatis.thread.Runnable.isRefresh()) {
				remove(key);
				org.apache.ibatis.thread.Runnable.log.debug("refresh key:"
						+ key.substring(key.lastIndexOf(".") + 1));
			}
			if (containsKey(key))
				throw new IllegalArgumentException(name
						+ " already contains value for " + key);
			if (key.contains(".")) {
				final String shortKey = getShortName(key);
				if (super.get(shortKey) == null) {
					super.put(shortKey, value);
				} else {
					super.put(shortKey, (V) new Ambiguity(shortKey));
				}
			}
			return super.put(key, value);
		}

		public V get(Object key) {
			V value = super.get(key);
			if (value == null) {
				throw new IllegalArgumentException(name
						+ " does not contain value for " + key);
			}
			if (value instanceof Ambiguity) {
				throw new IllegalArgumentException(
						((Ambiguity) value).getSubject()
								+ " is ambiguous in "
								+ name
								+ " (try using the full name including the namespace, or rename one of the entries)");
			}
			return value;
		}

		private String getShortName(String key) {
			final String[] keyparts = key.split("\\.");
			final String shortKey = keyparts[keyparts.length - 1];
			return shortKey;
		}

		protected static class Ambiguity {
			private String subject;

			public Ambiguity(String subject) {
				this.subject = subject;
			}

			public String getSubject() {
				return subject;
			}
		}
	}

}
