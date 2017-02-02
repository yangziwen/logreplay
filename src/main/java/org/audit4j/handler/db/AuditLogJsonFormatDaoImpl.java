package org.audit4j.handler.db;

import static org.audit4j.handler.db.Utils.checkNotEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.audit4j.core.dto.AuditEvent;
import org.audit4j.core.dto.Field;
import org.audit4j.core.exception.HandlerException;

public class AuditLogJsonFormatDaoImpl extends AuditBaseDao implements AuditLogDao {

	private final String tableName;

	/** The insert query. */
	private final String insertQuery;

	AuditLogJsonFormatDaoImpl(String tableName) throws HandlerException {
		this.tableName = checkNotEmpty(tableName, "Table name must not be empty");
		this.insertQuery = "insert into " + tableName
				+ "(identifier, timestamp, actor, origin, action, elements) "
				+ "values (?, ?, ?, ?, ?, ?)";

		createTableIfNotExists();
	}

	@Override
	public boolean writeEvent(AuditEvent event) throws HandlerException {
		try (Connection conn = getConnection()) {
			try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
				statement.setString(1, event.getUuid().toString());
				statement.setTimestamp(2, new Timestamp(event.getTimestamp().getTime()));
				statement.setString(3, event.getActor());
				statement.setString(4, event.getOrigin());
				statement.setString(5, event.getAction());
				statement.setString(6, generateFieldsJson(event.getFields()));
				return statement.execute();
			}
		} catch (SQLException e) {
			throw new HandlerException("SQL Exception", DatabaseAuditHandler.class, e);
		}
	}
	
	private String generateFieldsJson(List<Field> fields) {
		StringBuilder elements = new StringBuilder("{");
		int i = 0;
		for (Field element : fields) {
			if (i++ > 0) {
				elements.append(", ");
			}
			elements.append(element.getName() + ":" + element.getValue());
		}
		elements.append("}");
		return elements.toString();
	}

	/**
	 * Only support mysql
	 * @return
	 * @throws HandlerException
	 */
	private boolean createTableIfNotExists() throws HandlerException {
		boolean supportJson = false;
		try (Connection conn = getConnection()) {
			try (PreparedStatement statement = conn.prepareStatement("select version();");
					ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					supportJson = isJsonSupport(rs.getString(1));
				}
			}
			StringBuilder query = new StringBuilder();
			query.append("create table if not exists ").append(tableName)
					.append(" (")
					.append("identifier VARCHAR(200) NOT NULL,")
					.append("timestamp TIMESTAMP NOT NULL,")
					.append("actor VARCHAR(200) NOT NULL,")
					.append("origin VARCHAR(200),")
					.append("action VARCHAR(200) NOT NULL,")
					.append("elements ").append(supportJson ? "JSON" : "TEXT")
					.append(");");
			try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
				return statement.execute();
			}
		} catch (SQLException e) {
			throw new HandlerException("SQL Exception", DatabaseAuditHandler.class, e);
		}
	}
	
	private boolean isJsonSupport(String version) {
		if (version == null) {
			return false;
		}
		String[] array = version.split(".");
		if (array.length < 2) {
			return false;
		}
		int major = NumberUtils.toInt(array[0]);
		int minor = NumberUtils.toInt(array[1]);
		if (major < 5) {
			return false;
		}
		if (major == 5 && minor < 7) {
			return false;
		}
		return true;
	}

}
