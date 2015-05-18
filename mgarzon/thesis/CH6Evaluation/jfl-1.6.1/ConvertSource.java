/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: ConvertSource.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: ConvertSource.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.currency;

/**
 * A source of data for the conversion process.  The source must be able to be called
 * multiple times in a row, although synchronizing the ConvertSource method is fine.
 */
public interface ConvertSource
{
	public float getConverted(float amount, String symbolFrom, String symbolTo) throws ConversionException;

	/**
	 * Returns whether or not this implementation can currently return conversions.
	 */
	public boolean test();
}
