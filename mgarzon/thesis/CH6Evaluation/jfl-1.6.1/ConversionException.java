/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: ConversionException.java,v 1.4 2003/03/31 05:58:14 luke Exp $
	$Log: ConversionException.java,v $
	Revision 1.4  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.currency;

/**
 * This is thrown when there are problems converting currencies.
 */
public class ConversionException extends Exception
{
	public ConversionException() { super(); }
	public ConversionException(String msg) { super(msg); }
}
