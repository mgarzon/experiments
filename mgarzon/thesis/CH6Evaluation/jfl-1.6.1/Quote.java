/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: Quote.java,v 1.9 2003/03/31 05:58:14 luke Exp $
	$Log: Quote.java,v $
	Revision 1.9  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

/**
 * The main Quote class.  Do not instantiate this directly - use the provided
 * QuoteFactory class.
 */
public abstract class Quote
{
	private static final int MARKET_DEFAULT = 0;
	private static final int MARKET_TORONTO = 1;
	private static final int MARKET_CDNX = 2;
	private static final int MARKET_MONTREAL = 3;
	private static final int MARKET_US = 4;

	// TODO: make this abstrace and the implementation an inner class of the factory
	private String 	symbol			= "NULL";
	private String 	company			= "Unknown";
	private long 	volume			= 0;
	private float 	value			= 0;
	private long 	lastTradeTime	= 0;
	private long 	quoteTime 		= 0;
	private int		market 			= MARKET_DEFAULT;
	private String 	clsname; 		// The class that got this quote (for updating)
	private boolean wasCached 		= false;
	private float	openPrice		= 0;

	/**
	 * Create a quote with the minimum amount of required information.
	 */
	public Quote(String clsname)
	{
		this.clsname = clsname;
	}

	public int getMarket() 		{ return market; }
	public long getVolume() 	{ return volume; }
	public String getCompany() 	{ return company; }
	public String getSymbol() 	{ return symbol; }
	public float getValue() 	{ return value; }
	public float getOpenPrice() { return openPrice; }
	public float getChange() 	{ return (this.value - openPrice); }
	public float getPctChange (){ return ((this.getChange() / openPrice) * 100); }
	
	public void setMarket(int market)		{ this.market = market; }
	public void setVolume(long volume) 		{ this.volume = volume; }
	public void setSymbol(String symbol) 	{ this.symbol = symbol; }
	public void setCompany(String company)	{ this.company = company; }
	public void setValue(float value)		{ this.value = value; }
	public void setOpenPrice(float value)	{ this.openPrice = value; }

	/**
	 * Updates the quote from the original source, and returns true if the
	 * value has changed.
	 */
	public boolean update() throws QuoteException
	{
		QuoteSource qs;

		try {
			Class c = Class.forName(clsname);
			qs = (QuoteSource)c.newInstance();
		} catch (ClassNotFoundException cnf) {
			throw new QuoteException("Can't find source class \"" + clsname + "\".");
		} catch (InstantiationException ie) {
			throw new QuoteException("Can't instantiate class \"" + clsname + "\".");
		} catch (IllegalAccessException iae) {
			throw new QuoteException("Illegal access to \"" + clsname + "\".");
		}

		qs.fetch(this);

		// Do sanity checking
		if(value == 0) throw new QuoteException("Symbol not found.");

		return true;
	}

	public String toString()
	{
		return "Symbol: " + symbol + ", Company: " + company + ", Value: " + value + ", OpenPrice: " + openPrice;
	}

}
