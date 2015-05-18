/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: YahooCSVSource.java,v 1.2 2003/03/31 05:58:14 luke Exp $
	$Log: YahooCSVSource.java,v $
	Revision 1.2  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.

*/

package net.neurotech.quotes;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A QuoteSource for the Yahoo! Finance site in CSV format.
 */
public class YahooCSVSource implements QuoteSource
{
	private Quote quote;
	private String symbol;
	private static final boolean DEBUG = false;

	public boolean test() { return true; }

	public boolean fetch(Quote quote) throws QuoteException
	{
		this.quote = quote;
		symbol = quote.getSymbol();
		String content;

		String u = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol
			+ "&f=snl1d1t1c1ohgv&e=.c";

		try {
			URL url = new URL(u);

			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			content = in.readLine();
			in.close();

			if(DEBUG) {
				System.out.println(content);
			}
		} catch (Exception e) {
			throw new QuoteException("Couldn't retrieve quote - " + e);
		}

		StringTokenizer tk = new StringTokenizer(content, ",");
		tk.nextToken(); // symbol
		quote.setCompany(stripQuotes(tk.nextToken())); 		// name
		quote.setValue(Float.parseFloat(tk.nextToken())); 	// value
		tk.nextToken(); // date
		tk.nextToken(); // time
		tk.nextToken(); // net

		try {
			quote.setOpenPrice(Float.parseFloat(tk.nextToken())); // open price
		} catch(NumberFormatException nfe) {
			quote.setOpenPrice(0);
		}

		tk.nextToken(); // Daily High
		tk.nextToken(); // Daily Low

		try {
			quote.setVolume(Long.parseLong(tk.nextToken()));
		} catch(NumberFormatException nfe) {
			quote.setVolume(0);
		}

		return true;
	}

	private static final String stripQuotes(String q)
	{
		String s;

		s = q.substring(1, q.length() - 1);

		return s;
	}

}
