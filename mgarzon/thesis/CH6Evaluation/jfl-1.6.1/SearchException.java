/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: SearchException.java,v 1.4 2003/03/31 05:58:14 luke Exp $
	$Log: SearchException.java,v $
	Revision 1.4  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import java.util.*;

/**
 * This exception is thrown when a symbol search has errors.
 */
public class SearchException extends Exception
{
	public SearchException() { super(); }
	public SearchException(String m) { super(m); }
}
