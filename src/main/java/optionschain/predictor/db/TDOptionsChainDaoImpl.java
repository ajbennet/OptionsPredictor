package optionschain.predictor.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.sql.DataSource;
import org.slf4j.Logger;
import optionschain.predictor.model.Amtd.OptionChainResults.OptionDate.OptionStrike.Put;

public class TDOptionsChainDaoImpl implements TDOptionsChainDao {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TDOptionsChainDaoImpl.class);
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insert(Put puts, int date, double strike) {
		String sql = "INSERT INTO tdputs "
				+ "(OPTIONSYMBOL,BID,ASK,BIDASKSIZE,LAST,LASTTRADEDATE,VOLUME,"
				+ "OPENINTEREST,REALTIME,UNDERLYINGSYMBOL,DELTA,GAMMA,THETA,VEGA,"
				+ "RHO,IMPLIEDVOLATILITY,TIMEVALUEINDEX,MULTIPLIER,CHANGEVALUE,"
				+ "CHANGEPERCENT,INTHEMONEY,NEARTHEMONEY,THEORETICALVALUE,EXPIRATION,STRIKE)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		//logger.info("Processing Put : " + puts);
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,puts.getOptionSymbol());
			ps.setDouble(2,puts.getBid());
			ps.setDouble(3,puts.getAsk());
			ps.setDouble(4,puts.getBidAskSize());
			ps.setDouble(5,puts.getLast());
			ps.setTimestamp(6,new Timestamp(new Date().getTime()));//TODO: Need to change this.
			ps.setDouble(7,puts.getVolume());
			ps.setDouble(8,puts.getOpenInterest());
			ps.setString(9,puts.getRealTime());
			ps.setString(10,puts.getUnderlyingSymbol());
			ps.setDouble(11,puts.getDelta());
			ps.setDouble(12,puts.getGamma());
			ps.setDouble(13,puts.getTheta());
			ps.setDouble(14,puts.getVega());
			ps.setDouble(15,puts.getRho());
			ps.setDouble(16,puts.getImpliedVolatility());
			ps.setDouble(17,puts.getTimeValueIndex());
			ps.setDouble(18,puts.getMultiplier());
			ps.setDouble(19,puts.getChange());
			ps.setDouble(20,puts.getChangePercent());
			ps.setBoolean(21,Boolean.getBoolean(puts.getInTheMoney()));
			ps.setBoolean(22,Boolean.getBoolean(puts.getNearTheMoney()));
			ps.setDouble(23,puts.getTheoreticalValue());
			ps.setInt(24,date);
			ps.setDouble(25,strike);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage(),e);
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

	}

}
