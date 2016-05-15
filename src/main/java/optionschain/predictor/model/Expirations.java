package optionschain.predictor.model;

public class Expirations
{
    private String d;

    private String m;

    private String y;

    public String getD ()
    {
        return d;
    }

    public void setD (String d)
    {
        this.d = d;
    }

    public String getM ()
    {
        return m;
    }

    public void setM (String m)
    {
        this.m = m;
    }

    public String getY ()
    {
        return y;
    }

    public void setY (String y)
    {
        this.y = y;
    }

	@Override
	public String toString() {
		return "Expirations [d=" + d + ", m=" + m + ", y=" + y + "]";
	}

    
}

