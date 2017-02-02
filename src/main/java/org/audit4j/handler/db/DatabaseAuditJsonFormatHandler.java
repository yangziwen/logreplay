package org.audit4j.handler.db;

import static org.audit4j.handler.db.Utils.checkNotEmpty;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.audit4j.core.exception.HandlerException;
import org.audit4j.handler.db.DatabaseAuditHandler;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DatabaseAuditJsonFormatHandler extends DatabaseAuditHandler {

	private static final long serialVersionUID = 3997291026779486857L;

	private static final String DEFAULT_TABLE_NAME = "audit";

	private String default_table_name = DEFAULT_TABLE_NAME;

	private boolean separate = false;

	private String table_prefix;

	private String table_suffix = "audit";

	private final LoadingCache<String, AuditLogDao> daos = CacheBuilder
			.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(15, TimeUnit.MINUTES)
			.build(new CacheLoader<String, AuditLogDao>() {
				@Override
				public AuditLogDao load(String tableName) throws HandlerException {
					return new AuditLogJsonFormatDaoImpl(tableName);
				}
			});

	@Override
	public void handle() throws HandlerException {
		String repository = getAuditEvent().getRepository();
		boolean writeInDefaultTable = !separate || repository == null;
		String tableName = writeInDefaultTable ? default_table_name : generateTableName(repository);

		getDaoForTable(tableName).writeEvent(getAuditEvent());
	}

	private AuditLogDao getDaoForTable(String tableName) throws HandlerException {
		try {
			return daos.get(tableName);
		} catch (ExecutionException e) {
			Throwables.propagateIfInstanceOf(e.getCause(), HandlerException.class);
			throw new HandlerException("Execution Exception", DatabaseAuditHandler.class, e);
		}
	}

	private String generateTableName(String repository) {
		if (table_prefix == null) {
			return repository + "_" + table_suffix;
		}
		return table_prefix + "_" + repository + "_" + table_suffix;
	}

	@Override
	public void setDefault_table_name(String default_table_name) {
		this.default_table_name = checkNotEmpty(default_table_name, "Table name must not be empty");
	}

	@Override
	public void setSeparate(boolean separate) {
		this.separate = separate;
	}

	@Override
	public void setTable_prefix(String table_prefix) {
		this.table_prefix = table_prefix;
	}

	@Override
	public void setTable_suffix(String table_suffix) {
		this.table_suffix = table_suffix;
	}

}
