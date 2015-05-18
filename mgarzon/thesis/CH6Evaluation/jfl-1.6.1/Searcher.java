/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: Searcher.java,v 1.6 2003/03/31 05:58:14 luke Exp $
	$Log: Searcher.java,v $
	Revision 1.6  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import java.util.*;

/**
 * Allows the user to search for company symbols based on a given company name.
 */
public class Searcher
{
	String classname;

	/**
	 * Constructs a Searcher object with the default (Yahoo) data source.
	 */
	public Searcher()
	{
		this("net.neurotech.quotes.YahooSearchSource");
	}

	/**
	 * Constructs a Searcher object with a specific data source class.
	 */
	public Searcher(String classname)
	{
		this.classname = classname;
	}

	/**
	 * Gets the name of the source data class.
	 */
	public String getSource()
	{
		return classname;
	}

	/**
	 * Sets the class that this Searcher will use to retrieve quotes.
	 */
	public void setSource(String classname)
	{
		this.classname = classname;
	}

	/**
	 * Searches for a stock ticker.
	 */
	public LinkedList search(String expression) throws SearchException
	{
		SearchSource ss;

		try {
			Class cls = Class.forName(classname);
			ss = (SearchSource)cls.newInstance();
		} catch (Exception e) {
			throw new SearchException("Couldn't instantiate searcher.");
		}

		return ss.search(expression);
	}

	public static void main(String[] args) throws SearchException
	{
		if(args.length == 0)
		{
			System.out.println("Usage:");
			System.out.println("  Searcher <expression>");
		}
		else
		{
			Searcher s = new Searcher();
			LinkedList results = s.search(args[0]);
			System.out.println("Search Results (" + results.size() + ")");
			System.out.println("----------------------------------------");
			SearchHit sh;
			for(int i = 0; i < results.size(); i++)
			{
				sh = (SearchHit)results.get(i);
				System.out.println(sh.getSymbol() + " - " + sh.getResult());
			}
		}
	}

}
