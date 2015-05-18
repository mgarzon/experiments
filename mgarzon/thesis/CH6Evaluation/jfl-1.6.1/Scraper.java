/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: Scraper.java,v 1.2 2003/03/31 05:58:14 luke Exp $
	$Log: Scraper.java,v $
	Revision 1.2  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech;

import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A utility class to ease the pain of finding information in a large web page.
 */
public class Scraper extends ParserCallback
{
	private int tableCurrent = 0;
	private int cellCurrent = 0;
	private int rowCurrent = 0;
	private int subCurrent = 0;

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
		System.out.println("Table: " + tableCurrent + " Row: " +
			rowCurrent + " Cell: " + cellCurrent + " Sub: + " +
			subCurrent + " = " + new String(data));
		subCurrent++;
	}

	public static void main(String args[]) throws Exception
	{
		System.out.println("Starting scraper on " + args[0]);

		URL url = new URL(args[0]);

		URLConnection conn = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		ParserDelegator p = new ParserDelegator();
		Scraper s = new Scraper();
		p.parse(in, s, true);
	}

}
