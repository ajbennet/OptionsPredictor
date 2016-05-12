package optionschain.predictor.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import javax.sql.DataSource;

import optionschain.predictor.Utils;
import optionschain.predictor.model.Puts;

public class OptionsChainDaoImpl implements OptionsChainDao {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	public void insert(Puts puts, double roc, double aroc, double marketprice) {
		String sql = "INSERT INTO puts " +
				"(ASK, BID, C, CID, CP, CS, E, EXPIRATION, NAME, OPENINTEREST, P, S, STRIKE, VOLUME, ROC, AROC, MARKETPRICE) "
				+ "VALUES (?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?)";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setDouble(1, Utils.convertToDouble(puts.getA()));
			ps.setDouble(2, Utils.convertToDouble(puts.getB()));
			ps.setDouble(3, Utils.convertToDouble(puts.getC()));
			ps.setDouble(4, Utils.convertToDouble(puts.getCid()));
			ps.setDouble(5, Utils.convertToDouble(puts.getCp()));
			ps.setString(6, puts.getCs());
			ps.setString(7, puts.getE());
			ps.setDate(8, Utils.convertToDate(puts.getExpiry()));
			ps.setString(9, puts.getName());
			ps.setDouble(10, Utils.convertToDouble(puts.getOi()));
			ps.setDouble(11, Utils.convertToDouble(puts.getP()));
			ps.setString(12, puts.getS());
			ps.setDouble(13, Utils.convertToDouble(puts.getStrike()));
			ps.setDouble(14, Utils.convertToDouble(puts.getVol()));
			ps.setDouble(15, roc);
			ps.setDouble(16, aroc);
			ps.setDouble(17, marketprice);
			
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}

	}

	public Puts getPuts(int toplist) {
		// TODO Auto-generated method stub
		return null;
	}

}
