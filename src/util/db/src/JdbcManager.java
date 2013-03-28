import java.sql.*;

public class JdbcManager {
	private Connection connection;
	private String baseAddr, baseName, baseUser, basePass;

	public JdbcManager(String baseAddr, String baseName,
			String baseUser, String basePass) throws ClassNotFoundException,
			SQLException {
		this.baseAddr = baseAddr;
		this.baseName = baseName;
		this.baseUser = baseUser;
		this.basePass = basePass;
		connection = establish_jdbc_connection();
	}

	private Connection establish_jdbc_connection()
			throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.jdbc.Driver";
		Class.forName(driverName);
		String url = "jdbc:mysql://" + baseAddr + "/" + baseName;
		Connection connection = DriverManager.getConnection(url
				+ "?useUnicode=true&characterEncoding=utf-8", baseUser,
				basePass);
		System.out.println("Connected to DB" + connection);
		return connection;
	}

	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}

	public PreparedStatement createPreparedStatement(String pquery)
			throws SQLException {
		return connection.prepareStatement(pquery);
	}
}
