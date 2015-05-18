/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: SearchHit.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: SearchHit.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import java.util.*;
import java.io.*;

/**
 * A small class representing a search result.
 */
public class SearchHit
{
	String symbol;
    String result;

	public SearchHit(String psymbol, String presult)
	{
		symbol = psymbol;

		// Massage the result data
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < presult.length(); i++)
		{
			if ( (Character.isJavaIdentifierStart(presult.charAt(i))) ||
			     (Character.isWhitespace(         presult.charAt(i))) )
			{
				sb.append(presult.charAt(i));
			}
		}

		result = sb.toString();
	}

	public String getSymbol()   { return symbol; }
	public String getResult()   { return result; }
}
