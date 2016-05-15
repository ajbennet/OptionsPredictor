package optionschain.predictor.db;

import optionschain.predictor.model.Puts;

public interface OptionsChainDao 
{
	public void insert(Puts puts, double roc, double aroc, double marketprice, String stock , double dte);
	public Puts getPuts(int toplist);
}