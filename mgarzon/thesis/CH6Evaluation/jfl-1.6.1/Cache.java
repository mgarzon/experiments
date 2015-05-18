/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: Cache.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: Cache.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.finance;

import java.util.*;

/**
 * A general abstract class for caching data.
 */
public abstract class Cache
{
	private Hashtable _cachedata = new Hashtable();

	public Object get(String key)
	{
		return _cachedata.get(key);
	}

	public void put(String key, Object data)
	{
		_cachedata.put(key, data);
	}
}
