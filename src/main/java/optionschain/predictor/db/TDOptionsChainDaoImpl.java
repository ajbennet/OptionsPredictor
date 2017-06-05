package optionschain.predictor.db;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;

import optionschain.predictor.model.Amtd.OptionChainResults.OptionDate.OptionStrike.Put;
import optionschain.predictor.utils.Utils;

public class TDOptionsChainDaoImpl implements TDOptionsChainDao {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TDOptionsChainDaoImpl.class);
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insert(Put puts, int date, double strike, String time, double last,double open, double close, double high, double low, double roc, double aroc, int dte,
			double percentbelow, double rom, double arom, int regtmargin, String earningsDate, String good) {
		String sql = "INSERT INTO tdputs "
				+ "(OPTIONSYMBOL,BID,ASK,BIDASKSIZE,LAST,TIME,VOLUME,"
				+ "OPENINTEREST,REALTIME,UNDERLYINGSYMBOL,DELTA,GAMMA,THETA,VEGA,"
				+ "RHO,IMPLIEDVOLATILITY,TIMEVALUEINDEX,MULTIPLIER,CHANGEVALUE,"
				+ "CHANGEPERCENT,INTHEMONEY,NEARTHEMONEY,THEORETICALVALUE,EXPIRATION,STRIKE,"
				+ "OPEN,CLOSE, HIGH, LOW, ROC, AROC, DTE, PERCENTBELOW, ROM, AROM, REGTMARGIN, EARNINGSDATE, GOOD)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?)";
		Connection conn = null;
		//logger.info("Processing Put : " + puts);
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,puts.getOptionSymbol());
			ps.setDouble(2,puts.getBid());
			ps.setDouble(3,puts.getAsk());
			ps.setDouble(4,puts.getBidAskSize());
			ps.setDouble(5,last);
			ps.setString(6,time);
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
			ps.setDouble(26,open);
			ps.setDouble(27,close);
			ps.setDouble(28,high);
			ps.setDouble(29,low);
			ps.setDouble(30,roc*100);
			ps.setDouble(31,aroc*100);
			ps.setInt(32,dte);
			ps.setDouble(33, percentbelow);
			ps.setDouble(34, rom*100);
			ps.setDouble(35, arom*100);
			ps.setInt(36, regtmargin);
			ps.setString(37, earningsDate);
			ps.setString(38, good);
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
	
	public void filterData() {
		String query = "SELECT UNDERLYINGSYMBOL, LAST, EXPIRATION, DTE,EARNINGSDATE, GOOD, STRIKE,BID,ASK, THEORETICALVALUE,  DELTA,PERCENTBELOW, ROM, AROM, REGTMARGIN,IMPLIEDVOLATILITY,ROC, AROC"
				+ " FROM tdputs where bid >.09 and theta < 0 and STRIKE < LAST and "
				+ "delta > -0.06 and AROM >29 AND  DTE <60 AND GOOD=\"Good\" LIMIT 0,1000000;";
		Connection conn = null;
		//logger.info("Processing Put : " + puts);
		try {
			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement() ;
			ResultSet rs = stmt.executeQuery(query) ;
			Utils.convertToCsv(rs);
		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage(),e);
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public void clearData() {
		String query = "Delete from tdputs;";
		Connection conn = null;
		//logger.info("Processing Put : " + puts);
		try {
			conn = dataSource.getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
		    preparedStmt.execute();
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
