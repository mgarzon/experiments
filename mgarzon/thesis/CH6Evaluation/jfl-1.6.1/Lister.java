/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: Lister.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: Lister.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.currency;
import java.util.*;
import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.net.*;
import java.io.*;
import java.text.*;

/**
 * Connects to Yahoo and lists available currencies
 */
public final class Lister
{
    private Hashtable theList = null;
    private static TreeMap tm = null;
    private static Vector vec = null;

    /* Inner class for parsing */
    class parseYahoo extends ParserCallback
    {
        Hashtable v;
        int select = 0;
        int match = 1;
        String curSymbol;
        String curDesc;
        boolean listMode = false;
        public parseYahoo(Hashtable v) { this.v = v; }
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
        {
            if((listMode == true) && (t == HTML.Tag.OPTION))
            {
                Enumeration e = a.getAttributeNames();
                while(e.hasMoreElements())
                {
                    Object name = e.nextElement();
                    HTML.Attribute att = (HTML.Attribute)name;
                    String textname = att.toString().toLowerCase();
                    String value = (String)a.getAttribute(name);
                    if(textname.equals("value"))
                    {
                        curSymbol = value;
                    }
                 }
             }
             if(t == HTML.Tag.SELECT)			{				select++;				if(select == match) listMode = true;				else listMode = false;			}		}		public void handleEndTag(HTML.Tag t, int pos)		{			if((t == HTML.Tag.SELECT) && (listMode == true)) listMode = false;		}		public void handleText(char[] data, int pos)		{			if(listMode == true)			{				curDesc = new String(data);				curDesc = curDesc.trim();				if(!curDesc.equals(""))				{					v.put(curSymbol, curDesc);				}			}		}	}	public Lister() throws ConversionException	{		/* Do connection, using HTML editor kit's parser */		theList = new Hashtable();		BufferedReader in;		try		{			URL Finance = new URL("http://finance.yahoo.com/m3");			URLConnection Conn = Finance.openConnection();			in = new BufferedReader(new InputStreamReader(Conn.getInputStream()));			parseYahoo parseCalls = new parseYahoo(theList);			ParserDelegator p = new ParserDelegator();			p.parse(in, parseCalls, true);		} catch(Exception e) {			throw new ConversionException("Couldn't get list of currencies.");		}	}	public Hashtable getTable()	{		return theList;	}	/**	 * Returns a sorted TreeMap of all the symbols in the list of currencies.	 */	public TreeMap getMap()	{		if(tm == null)		{			Collator c = Collator.getInstance(Locale.US);			c.setStrength(Collator.PRIMARY);			tm = new TreeMap(c);			for(Enumeration e = theList.keys(); e.hasMoreElements();)			{				String key = (String)e.nextElement();				String value = (String)theList.get(key);				tm.put(key.trim(), value.trim());			}		}		return tm;	}	public Vector getVector()	{		if(tm == null) getMap();		if(vec == null)		{			vec = new Vector();			Set ks = tm.keySet();			for(Iterator i = ks.iterator(); i.hasNext();)			{				String key = (String)i.next();				vec.add(tm.get(key));			}		}		return vec;	}	public static void main(String[] args) throws ConversionException	{		Lister temp = new Lister();		Vector vec = temp.getVector();		for(int i = 0; i < vec.size(); i++)		{			String s = (String)vec.get(i);			System.out.println(vec.get(i));		}	}}
