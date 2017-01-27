package org.audit4j.handler.db;

import static org.audit4j.handler.db.Utils.checkNotEmpty;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.audit4j.core.ErrorGuide;
import org.audit4j.core.exception.HandlerException;
import org.audit4j.core.exception.InitializationException;
import org.audit4j.core.handler.Handler;
import org.audit4j.core.util.Log;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DatabaseAuditJsonFormatHandler extends Handler {

	private static final long serialVersionUID = -3716486069474582073L;
	
    private static final String DEFAULT_TABLE_NAME = "audit";

    /**
     * Creating cache for Data access objects for different tables.
     */
    private final LoadingCache<String, AuditLogDao> daos = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, AuditLogDao>() {
                @Override
                public AuditLogDao load(String tableName) throws HandlerException {
                    return new AuditLogJsonFormatDaoImpl(tableName);
                }
            });
    /**
     * The embeded.
     */
    private String embedded;

    /**
     * The db_driver.
     */
    private String db_driver;

    /**
     * The db_url.
     */
    private String db_url;

    /**
     * The db_user.
     */
    private String db_user;

    /**
     * The db_password.
     */
    private String db_password;

    /**
     * The db_connection_type.
     */
    private String db_connection_type;

    /**
     * The db_datasource class.
     */
    private String db_datasourceClass;

    /**
     * The db_jndi_datasource.
     */
    private String db_jndi_datasource;

    /**
     * The auto commit.
     */
    private boolean db_pool_autoCommit = true;

    /**
     * The connection timeout.
     */
    private Long db_pool_connectionTimeout;

    /**
     * The idle timeout.
     */
    private Integer db_pool_idleTimeout;

    /**
     * The max lifetime.
     */
    private Integer db_pool_maxLifetime;

    /**
     * The minimum idle.
     */
    private Integer db_pool_minimumIdle;

    /**
     * The maximum pool size.
     */
    private Integer db_pool_maximumPoolSize;

    /**
     * The Constant POOLED_CONNECTION.
     */
    private static final String POOLED_CONNECTION = "pooled";

    /**
     * The Constant JNDI_CONNECTION.
     */
    private static final String JNDI_CONNECTION = "jndi";

    /**
     * The server.
     */
    private EmbededDBServer server;

    /**
     * The factory.
     */
    private ConnectionFactory factory;

    /**
     * The separate.
     */
    private boolean separate = false;

    /**
     * The data source.
     */
    private DataSource dataSource;

    /**
     * The table_prefix.
     */
    private String table_prefix;

    /**
     * The table_suffix.
     */
    private String table_suffix = "audit";

    /**
     * The default_table_suffix.
     */
    private String default_table_name = DEFAULT_TABLE_NAME;

    /**
     * Instantiates a new database audit handler.
     */
    public DatabaseAuditJsonFormatHandler() {
    }

    /**
     * Initialize database handler.
     *
     * @throws InitializationException the initialization exception
     */
    @Override
    public void init() throws InitializationException {
        if (null == embedded || "true".equalsIgnoreCase(embedded)) {
            Log.warn("Audit4j Database Handler runs on embedded mode. See " + ErrorGuide.ERROR_URL
                    + "embeddeddb for further details.");
            server = HSQLEmbededDBServer.getInstance();
            db_driver = server.getDriver();
            db_url = server.getNetworkProtocol() + ":file:audit4jdb";
            if (db_user == null) {
                db_user = Utils.EMBEDED_DB_USER;
            }
            if (db_password == null) {
                db_password = Utils.EMBEDED_DB_PASSWORD;
            }
            server.setUname(db_user);
            server.setPassword(db_password);
            server.start();
        }

        factory = ConnectionFactory.getInstance();
        factory.setDataSource(dataSource);
        factory.setDriver(getDb_driver());
        factory.setUrl(getDb_url());
        factory.setUser(getDb_user());
        factory.setPassword(getDb_password());

        factory.setDataSourceClass(db_datasourceClass);
        factory.setAutoCommit(db_pool_autoCommit);
        if (db_pool_connectionTimeout != null) {
            factory.setConnectionTimeout(db_pool_connectionTimeout);
        }
        if (db_pool_idleTimeout != null) {
            factory.setIdleTimeout(db_pool_idleTimeout);
        }
        if (db_pool_maximumPoolSize != null) {
            factory.setMaximumPoolSize(db_pool_maximumPoolSize);
        }
        if (db_pool_maxLifetime != null) {
            factory.setMaxLifetime(db_pool_maxLifetime);
        }
        if (db_pool_minimumIdle != null) {
            factory.setMinimumIdle(db_pool_minimumIdle);
        }

        if (getDb_connection_type() != null && getDb_connection_type().equals(POOLED_CONNECTION)) {
            factory.setConnectionType(ConnectionType.POOLED);
        } else if (getDb_connection_type() != null && getDb_connection_type().equals(JNDI_CONNECTION)) {
            factory.setConnectionType(ConnectionType.JNDI);
            factory.setJndiDataSource(getDb_jndi_datasource());
        } else {
            factory.setConnectionType(ConnectionType.SINGLE);
        }

        factory.init();

        try {
            getDaoForTable(default_table_name);
        } catch (HandlerException e) {
            throw new InitializationException("Unable to create tables", e);
        }

    }

    /**
     * Handle event.
     * <p>
     * {@inheritDoc}
     *
     * @see org.audit4j.core.handler.Handler#handle()
     */
    @Override
    public void handle() throws HandlerException {
        String repository = getAuditEvent().getRepository();
        boolean writeInDefaultTable = !separate || repository == null;
        String tableName = writeInDefaultTable ? default_table_name : generateTableName(repository);

        getDaoForTable(tableName).writeEvent(getAuditEvent());
    }

    /**
     * Shutdown database handler.
     */
    @Override
    public void stop() {
        factory.stop();
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Generate table name.
     *
     * @param repository
     * @return the string
     */
    private String generateTableName(String repository) {
        if (table_prefix == null) {
            return repository + "_" + table_suffix;
        }
        return table_prefix + "_" + repository + "_" + table_suffix;
    }

    private AuditLogDao getDaoForTable(String tableName) throws HandlerException {
        try {
            return daos.get(tableName);
        } catch (ExecutionException e) {
            Throwables.propagateIfInstanceOf(e.getCause(), HandlerException.class);
            throw new HandlerException("Execution Exception", DatabaseAuditJsonFormatHandler.class, e);
        }
    }


    /**
     * Gets the db_connection_type.
     *
     * @return the db_connection_type
     */
    public String getDb_connection_type() {
        return db_connection_type;
    }

    /**
     * Sets the db_connection_type.
     *
     * @param db_connection_type the new db_connection_type
     */
    public void setDb_connection_type(String db_connection_type) {
        this.db_connection_type = db_connection_type;
    }

    /**
     * Gets the embedded.
     *
     * @return the embedded
     */
    public String getEmbedded() {
        return embedded;
    }

    /**
     * Sets the embedded.
     *
     * @param embedded the new embedded
     */
    public void setEmbedded(String embedded) {
        this.embedded = embedded;
    }

    /**
     * Gets the db_driver.
     *
     * @return the db_driver
     */
    public String getDb_driver() {
        return db_driver;
    }

    /**
     * Sets the db_driver.
     *
     * @param db_driver the new db_driver
     */
    public void setDb_driver(String db_driver) {
        this.db_driver = db_driver;
    }

    /**
     * Gets the db_url.
     *
     * @return the db_url
     */
    public String getDb_url() {
        return db_url;
    }

    /**
     * Sets the db_url.
     *
     * @param db_url the new db_url
     */
    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    /**
     * Gets the db_user.
     *
     * @return the db_user
     */
    public String getDb_user() {
        return db_user;
    }

    /**
     * Sets the db_user.
     *
     * @param db_user the new db_user
     */
    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    /**
     * Gets the db_password.
     *
     * @return the db_password
     */
    public String getDb_password() {
        return db_password;
    }

    /**
     * Sets the db_password.
     *
     * @param db_password the new db_password
     */
    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    /**
     * Gets the db_jndi_datasource.
     *
     * @return the db_jndi_datasource
     */
    public String getDb_jndi_datasource() {
        return db_jndi_datasource;
    }

    /**
     * Sets the db_jndi_datasource.
     *
     * @param db_jndi_datasource the new db_jndi_datasource
     */
    public void setDb_jndi_datasource(String db_jndi_datasource) {
        this.db_jndi_datasource = db_jndi_datasource;
    }

    /**
     * Sets the separate.
     *
     * @param separate the new separate
     */
    public void setSeparate(boolean separate) {
        this.separate = separate;
    }

    /**
     * Sets the data source.
     *
     * @param dataSource the new data source
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Sets the table_prefix.
     *
     * @param table_prefix the new table_prefix
     */
    public void setTable_prefix(String table_prefix) {
        this.table_prefix = table_prefix;
    }

    /**
     * Sets the table_suffix.
     *
     * @param table_suffix the new table_suffix
     */
    public void setTable_suffix(String table_suffix) {
        this.table_suffix = table_suffix;
    }

    /**
     * Sets the default_table_name.
     *
     * @param default_table_name the new default_table_name
     */
    public void setDefault_table_name(String default_table_name) {
        this.default_table_name = checkNotEmpty(default_table_name, "Table name must not be empty");
    }

    /**
     * Sets the db_pool_auto commit.
     *
     * @param db_pool_autoCommit the new db_pool_auto commit
     */
    public void setDb_pool_autoCommit(boolean db_pool_autoCommit) {
        this.db_pool_autoCommit = db_pool_autoCommit;
    }

    /**
     * Sets the db_pool_connection timeout.
     *
     * @param db_pool_connectionTimeout the new db_pool_connection timeout
     */
    public void setDb_pool_connectionTimeout(Long db_pool_connectionTimeout) {
        this.db_pool_connectionTimeout = db_pool_connectionTimeout;
    }

    /**
     * Sets the db_pool_idle timeout.
     *
     * @param db_pool_idleTimeout the new db_pool_idle timeout
     */
    public void setDb_pool_idleTimeout(Integer db_pool_idleTimeout) {
        this.db_pool_idleTimeout = db_pool_idleTimeout;
    }

    /**
     * Sets the db_pool_max lifetime.
     *
     * @param db_pool_maxLifetime the new db_pool_max lifetime
     */
    public void setDb_pool_maxLifetime(Integer db_pool_maxLifetime) {
        this.db_pool_maxLifetime = db_pool_maxLifetime;
    }

    /**
     * Sets the db_pool_minimum idle.
     *
     * @param db_pool_minimumIdle the new db_pool_minimum idle
     */
    public void setDb_pool_minimumIdle(Integer db_pool_minimumIdle) {
        this.db_pool_minimumIdle = db_pool_minimumIdle;
    }

    /**
     * Sets the db_pool_maximum pool size.
     *
     * @param db_pool_maximumPoolSize the new db_pool_maximum pool size
     */
    public void setDb_pool_maximumPoolSize(Integer db_pool_maximumPoolSize) {
        this.db_pool_maximumPoolSize = db_pool_maximumPoolSize;
    }

    /**
     * Sets the db_datasource class.
     *
     * @param db_datasourceClass the new db_datasource class
     */
    public void setDb_datasourceClass(String db_datasourceClass) {
        this.db_datasourceClass = db_datasourceClass;
    }

}
