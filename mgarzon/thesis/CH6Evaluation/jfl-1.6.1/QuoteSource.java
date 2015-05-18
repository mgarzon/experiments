/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: QuoteSource.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: QuoteSource.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

/**
 * Interface for classes that will retrieve quotes from data sources.
 */
public interface QuoteSource
{
	/**
	 * Fetches a quote from this source.
	 */
	public boolean fetch(Quote quote) throws QuoteException;

	/**
	 * Tests the QuoteSource implementation to see whether or not valid
	 * stock quotes can be retrieved. Returns true on sucess.  This will
	 * eventually be used by the QuoteFactory possibly to decide which
	 * backend to get data from.
	 */
	public boolean test();
}
