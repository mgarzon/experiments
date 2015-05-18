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
             if(t == HTML.Tag.SELECT)