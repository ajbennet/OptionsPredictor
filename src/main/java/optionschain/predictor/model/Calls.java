package optionschain.predictor.model;

public class Calls
{
    private String strike;

    private String vol;

    private String e;

    private String b;

    private String s;

    private String c;

    private String a;

    private String p;

    private String cs;

    public String getCs() {
		return cs;
	}

	public void setCs(String cs) {
		this.cs = cs;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	private String cp;
    private String name;

    private String oi;

    private String cid;

    private String expiry;

    public String getStrike ()
    {
        return strike;
    }

    public void setStrike (String strike)
    {
        this.strike = strike;
    }

    public String getVol ()
    {
        return vol;
    }

    public void setVol (String vol)
    {
        this.vol = vol;
    }

    public String getE ()
    {
        return e;
    }

    public void setE (String e)
    {
        this.e = e;
    }

    public String getB ()
    {
        return b;
    }

    public void setB (String b)
    {
        this.b = b;
    }

    public String getS ()
    {
        return s;
    }

    public void setS (String s)
    {
        this.s = s;
    }

    public String getC ()
    {
        return c;
    }

    public void setC (String c)
    {
        this.c = c;
    }

    public String getA ()
    {
        return a;
    }

    public void setA (String a)
    {
        this.a = a;
    }

    public String getP ()
    {
        return p;
    }

    public void setP (String p)
    {
        this.p = p;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getOi ()
    {
        return oi;
    }

    public void setOi (String oi)
    {
        this.oi = oi;
    }

    public String getCid ()
    {
        return cid;
    }

    public void setCid (String cid)
    {
        this.cid = cid;
    }

    public String getExpiry ()
    {
        return expiry;
    }

    public void setExpiry (String expiry)
    {
        this.expiry = expiry;
    }

	@Override
	public String toString() {
		return "Calls [strike=" + strike + ", vol=" + vol + ", e=" + e + ", b=" + b + ", s=" + s + ", c=" + c + ", a="
				+ a + ", p=" + p + ", cs=" + cs + ", cp=" + cp + ", name=" + name + ", oi=" + oi + ", cid=" + cid
				+ ", expiry=" + expiry + "]";
	}

   
}
