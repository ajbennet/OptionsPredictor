package optionschain.predictor.model;

public class OptionsChain {
	private Puts[] puts;

    private String underlying_id;

    private String underlying_price;

    private Expirations[] expirations;

    private Calls[] calls;

    private Expiry expiry;

    public Puts[] getPuts ()
    {
        return puts;
    }

    public void setPuts (Puts[] puts)
    {
        this.puts = puts;
    }

    public String getUnderlying_id ()
    {
        return underlying_id;
    }

    public void setUnderlying_id (String underlying_id)
    {
        this.underlying_id = underlying_id;
    }

    public String getUnderlying_price ()
    {
        return underlying_price;
    }

    public void setUnderlying_price (String underlying_price)
    {
        this.underlying_price = underlying_price;
    }

    public Expirations[] getExpirations ()
    {
        return expirations;
    }

    public void setExpirations (Expirations[] expirations)
    {
        this.expirations = expirations;
    }

    public Calls[] getCalls ()
    {
        return calls;
    }

    public void setCalls (Calls[] calls)
    {
        this.calls = calls;
    }

    public Expiry getExpiry ()
    {
        return expiry;
    }

    public void setExpiry (Expiry expiry)
    {
        this.expiry = expiry;
    }

   
}
