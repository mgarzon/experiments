/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: YahooSearchSource.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: YahooSearchSource.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A class to search for symbols on Yahoo Finance.
 */
public class YahooSearchSource extends ParserCallback implements SearchSource
{
	private int tableCurrent = 0;
	private int cellCurrent = 0;
	private int rowCurrent = 0;
	private int subCurrent = 0;
	private LinkedList results;
	private String symbol = "";

	public boolean test()
	{
		return true;
	}

	public LinkedList search(String expression) throws SearchException
	{
		String u = "http://finance.yahoo.com/l?m=US&s=" + expression + "&t=";

		try {
			results = new LinkedList();
			URL url = new URL(u);

			URLConnection conn = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			ParserDelegator p = new ParserDelegator();
			p.parse(in, this, true);
		} catch (Exception e) {
			throw new SearchException ("Couldn't search.");
		}

		return results;
	}

	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
	{
		if(t == HTML.Tag.TABLE)
		{
			tableCurrent++; cellCurrent = 0; rowCurrent = 0; subCurrent = 0;
		}
		if(t == HTML.Tag.TR)
		{
			rowCurrent++; cellCurrent = 0; subCurrent = 0;
		}
		if(t == HTML.Tag.TD)
		{
			cellCurrent++; subCurrent = 0;
		}
	}

	public void handleText(char[] data, int pos)
	{
		boolean debug = false;

		if(debug)
		{
			System.out.println("Table: " + tableCurrent + " Row: " +
				rowCurrent + " Cell: " + cellCurrent + " Sub: + " +
				subCurrent + " = " + new String(data));
		}

		// Everything is in table number 8
		if((tableCurrent == 8) && (rowCurrent > 1))
		{
			if((cellCurrent == 1) && (subCurrent == 0))
			{
				symbol = new String(data);
			}
			if((cellCurrent == 2) && (subCurrent == 0))
			{
				String hit = new String(data);
				String symhit = new String(symbol);

				SearchHit sh = new SearchHit(symhit, hit);
				results.add(sh);
			}
		}

		subCurrent++;
	}


}
