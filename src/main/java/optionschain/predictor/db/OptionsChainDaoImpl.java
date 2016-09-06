package optionschain.predictor.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;

import optionschain.predictor.model.Puts;
import optionschain.predictor.utils.Utils;

public class OptionsChainDaoImpl implements OptionsChainDao {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OptionsChainDaoImpl.class);
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insert(Puts puts, double roc, double aroc, double marketprice, String stock, double dte) {
		String sql = "INSERT INTO puts "
				+ "(ASK, BID, C, CID, CP, CS, E, EXPIRATION, NAME, OPENINTEREST, P, S, STRIKE, VOLUME, ROC, AROC, MARKETPRICE,STOCK, DTE) "
				+ "VALUES (?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?)";
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
			ps.setString(18, stock);
			ps.setDouble(19, dte);
//			ps.setTimestamp(20, new Timestamp(new Date().getTime()));
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage(),e);
			throw new RuntimeException(e);

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			logger.error(e.getLocalizedMessage(),e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					logger.error(e.getLocalizedMessage(),e);
				}
			}
		}

	}

	public Puts getPuts(int toplist) {

		String sql = "INSERT INTO puts "
				+ "(ASK, BID, C, CID, CP, CS, E, EXPIRATION, NAME, OPENINTEREST, P, S, STRIKE, VOLUME, ROC, AROC, MARKETPRICE) "
				+ "VALUES (?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?)";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					logger.error(e.getLocalizedMessage(),e);
				}
			}
		}
		return null;
	}

}
