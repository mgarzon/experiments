/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: YahooParser.java,v 1.8 2003/03/31 05:58:14 luke Exp $
	$Log: YahooParser.java,v $
	Revision 1.8  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.currency;

import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Implementation of ConvertSource for Yahoo Finance.
 */
public class YahooParser extends ParserCallback implements ConvertSource
{
	private float result = -1;
	private int tableMatch = 6;
	private int rowMatch = 2;
	private int cellMatch = 2;
	private int tableCurrent = 0;
	private int rowCurrent = 0;
	private int cellCurrent = 0;
	private boolean found = false;
	public float getResult() {return result;}

	public boolean test()
	{
		return true;
	}

	public synchronized float getConverted(float amount, String symbolFrom, String symbolTo) throws ConversionException
	{
		String u = "http://finance.yahoo.com/m5?a=" + amount + "&s=" + symbolFrom + "&t=" + symbolTo;
		tableCurrent = 0;
		rowCurrent = 0;
		cellCurrent = 0;

		try {
			URL url = new URL(u);
			URLConnection conn = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			ParserDelegator p = new ParserDelegator();
			p.parse(in, this, true);
		} catch (MalformedURLException mue) {
			throw new ConversionException("Couldn't build a URL to send.");
		} catch (IOException e) {
			throw new ConversionException("Error reading from Yahoo's site.");
		}

		return result;

	}

	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
	{
		if(t == HTML.Tag.TABLE)
		{
			tableCurrent++; cellCurrent = 0; rowCurrent = 0;
		}
		if(t == HTML.Tag.TR)
		{
			rowCurrent++; cellCurrent = 0;
		}
		if(t == HTML.Tag.TD)
		{
			cellCurrent++;
			if((tableMatch == tableCurrent) && (cellMatch == cellCurrent) && (rowCurrent == rowMatch))
			{
				found = true;
			}
		}
	}
	public void handleText(char[] data, int pos)
	{
		if(found == true)
		{
			// We have to remove anything that is not a number, or decimal point,
			// otherwise parseFloat gets confused...

			ByteArrayInputStream uncleanFloat = new ByteArrayInputStream(new String(data).getBytes());
			ByteArrayOutputStream cleanFloat = new ByteArrayOutputStream();

			int nextByte = uncleanFloat.read();
			while (nextByte != -1)
			{
				if ((nextByte >= 48 && nextByte <=57) || nextByte == 46) cleanFloat.write(nextByte);
				nextByte = uncleanFloat.read();
			}

			result = Float.parseFloat(new String(cleanFloat.toByteArray()));
			found = false;
		}
	}
}

