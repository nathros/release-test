package lto.manager.common.database.tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.pdfbox.util.Hex;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordRole;

public class TableRoles {
	public static final DbTable table = getSelf();
	public static final String TABLE_NAME = "table_roles";
	public static final String COLUMN_NAME_ID = "id_role";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_DESCRIPTION = "description";
	public static final String COLUMN_NAME_PERMISSION_MASK = "permission";

	public static final int COLUMN_INDEX_ID = 0;
	public static final int COLUMN_INDEX_NAME = 1;
	public static final int COLUMN_INDEX_DESCRIPTION = 2;
	public static final int COLUMN_INDEX_PERMISSION_MASK  = 3;

	public static final int ROLE_ID_ADMIN = 1;
	public static final int ROLE_ID_VIEWER = 2;

	static private DbTable getSelf() {
		DbSchema schema = Database.schema;
		DbTable table = schema.addTable(TABLE_NAME);

		DbColumn id = table.addColumn(COLUMN_NAME_ID, Types.INTEGER, null);
		//id.primaryKey();
		id.unique();
		id.notNull();

		String key[] = new String[] { COLUMN_NAME_ID};
		table.primaryKey(COLUMN_NAME_ID, key);

		table.addColumn(COLUMN_NAME_NAME, Types.VARCHAR, 128).unique();
		table.addColumn(COLUMN_NAME_DESCRIPTION, Types.VARCHAR, 256);
		table.addColumn(COLUMN_NAME_PERMISSION_MASK, Types.VARCHAR, 256); // 1024 bit

		return table;
	}

	public static boolean createTable(Connection con) throws SQLException {
		String q = new CreateTableQuery(TableRoles.table, true).validate().toString();
		q = q.replace(COLUMN_NAME_ID + ")", COLUMN_NAME_ID + " AUTOINCREMENT)");

		var statment = con.createStatement();
		if (!statment.execute(q)) {
			List<RecordRole> roles = RecordRole.getDefaultRoles();
			for (RecordRole role : roles) {
				if (!addNewRole(con, role)) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public static boolean addNewRole(Connection con, RecordRole newRole) throws SQLException {
		var statment = con.createStatement();

		InsertQuery iq = new InsertQuery(table);
		if (newRole.getID() != Database.NEW_RECORD_ID)
		{
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_ID), newRole.getID());
		}
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_NAME), newRole.getName());
		iq.addColumn(table.getColumns().get(COLUMN_INDEX_DESCRIPTION), newRole.getDescription());

		final String hexData = Hex.getString(newRole.getPermission().toByteArray());
		if (hexData.length() < 256) {
			final String hexDataPadded = "0".repeat(256 - hexData.length()) + hexData;
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_PERMISSION_MASK), hexDataPadded);
		} else {
			iq.addColumn(table.getColumns().get(COLUMN_INDEX_PERMISSION_MASK), hexData);
		}

		String sql = iq.validate().toString();
		if (!statment.execute(sql)) {
			return true;
		}
		return false;
	}

	public static List<RecordRole> getAll(Connection con) throws SQLException, IOException {
		var statment = con.createStatement();

		SelectQuery uq = new SelectQuery();
		uq.addAllTableColumns(table);
		String sql = uq.validate().toString();
		ResultSet result = statment.executeQuery(sql);

		List<RecordRole> list = new ArrayList<RecordRole>();
		while (result.next()) {
			list.add(fromResultSet(result));
		}

		return list;
	}

	public static boolean deleteRole(Connection con, int id) throws SQLException, IOException {
		var statment = con.createStatement();

		DeleteQuery dq = new DeleteQuery(table);
		dq.addCondition(BinaryCondition.equalTo(table.getColumns().get(COLUMN_INDEX_ID), id));

		String sql = dq.validate().toString();

		if (!statment.execute(sql)) {
			return true;
		}

		return true;
	}

	public static RecordRole fromResultSet(ResultSet result) throws SQLException, IOException {
		final int id = result.getInt(COLUMN_NAME_ID);
		final String name = result.getString(COLUMN_NAME_NAME);
		final String description = result.getString(COLUMN_NAME_DESCRIPTION);
		final String permissionStr = result.getString(COLUMN_NAME_PERMISSION_MASK);
		final BitSet permission = BitSet.valueOf(Hex.decodeHex(permissionStr));
		return RecordRole.of(id, name, description, permission);
	}

}
