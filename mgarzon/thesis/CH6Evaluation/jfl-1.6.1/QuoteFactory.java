/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: QuoteFactory.java,v 1.10 2003/03/31 06:12:27 luke Exp $
	$Log: QuoteFactory.java,v $
	Revision 1.10  2003/03/31 06:12:27  luke
	Change default converter to Oanda.
	
	Revision 1.9  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

/**
 * A factory for getting Quote objects.  This is the main interface into the
 * quote engine of the JFL.
 */
public class QuoteFactory
{
	private String quotesource;
	private boolean useCache = true;

	public QuoteFactory()
	{
		this("net.neurotech.quotes.YahooCSVSource");
	}

	public QuoteFactory(String quotesource)
	{
		this.quotesource = quotesource;
	}

	/**
	 * Returns the class that this factory will use to retrieve quotes.
	 */
	public String getSource()
	{
		return quotesource;
	}

	/**
	 * Manually sets the class that this factory will use to retrieve quotes.
	 */
	public void setSource(String quotesource)
	{
		this.quotesource = quotesource;
	}


	/**
	 * Retrieves a quote with the default market (or whatever market is indicated in the symbol).
	 */
	public Quote getQuote(String symbol) throws QuoteException
	{
		Quote q = new QuoteImpl(quotesource);
		q.setSymbol(symbol);
		q.update();
		return q;
	}

	/**
	 * Retrieves a quote from the specified market.  Market values are:
	 *   Quote.MARKET_DEFAULT
	 *   Quote.MARKET_TORONTO
	 *   Quote.MARKET_CDNX
	 *   Quote.MARKET_MONTREAL
	 *   Quote.MARKET_US
	 */
	public Quote getQuote(String symbol, int market) throws QuoteException
	{
		Quote q = new QuoteImpl(quotesource);
		q.setSymbol(symbol);
		q.setMarket(market);
		q.update();
		return q;
	}

	public boolean getCaching()
	{
		return useCache;
	}

	/**
	 * Tells this factory whether or not to cache quotes.
	 */
	public void setCaching(boolean p)
	{
		useCache = p;
	}

	private class QuoteImpl extends Quote
	{
		private QuoteImpl(String m) { super(m); }
	}

	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Usage:");
			System.out.println("  QuoteFactory <symbol>");
			System.out.println();
		}
		else
		{
			Quote q;

			try {
				q = new QuoteFactory().getQuote(args[0]);
			} catch (QuoteException qe) {
				System.out.println("Error - " + qe.getMessage());
				qe.printStackTrace();
				return;
			}

			System.out.println("Symbol:   " + q.getSymbol());
			System.out.println("Company:  " + q.getCompany());
			System.out.println("Value:    " + q.getValue());
			System.out.println("Volume:   " + q.getVolume());
		}
	}

}
