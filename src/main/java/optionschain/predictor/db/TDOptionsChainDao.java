package optionschain.predictor.db;

import optionschain.predictor.model.Amtd.OptionChainResults.OptionDate.OptionStrike.Put;

public interface TDOptionsChainDao 
{
	public void insert(Put put, int date, double strike, String time, double last, double open, double close, double high, double low, double roc, double aroc);
}