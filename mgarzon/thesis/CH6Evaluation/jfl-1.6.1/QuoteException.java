/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: QuoteException.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: QuoteException.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

/**
 * This exception is thrown when there is an error retrieving
 * a stock quote from the Internet.
 */
public class QuoteException extends Exception
{
	public QuoteException() { super(); }
	public QuoteException(String m) { super(m); }
}
