/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: SearchSource.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: SearchSource.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import java.util.*;

/**
 * Interface for classes that perform symbol searches.
 */
public interface SearchSource
{
	public LinkedList search(String expression) throws SearchException;

	/**
	 * Returns true if this source can currently provide search results.
	 */
	public boolean test();
}
