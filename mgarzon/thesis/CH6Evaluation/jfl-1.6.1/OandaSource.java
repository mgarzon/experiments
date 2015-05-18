/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: OandaSource.java,v 1.8 2003/03/31 06:23:08 luke Exp $
	$Log: OandaSource.java,v $
	Revision 1.8  2003/03/31 06:23:08  luke
	E-Mail address
	
	Revision 1.7  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
 */


package net.neurotech.currency;

import java.io.*;
import java.net.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;

/**
 * Currency data source for Oanda.
 * @author Craig Cavanaugh &lt;rideatrail@fwi.com&gt;
 */
public class OandaSource implements ConvertSource {

    public static URLConnection connection = null;

	public boolean test()
	{
		return true;
	}

    public float getConverted(float amount, String symbolFrom, String symbolTo) throws ConversionException{
        String result = getOandaExchangeRate(Float.toString(amount), symbolFrom, symbolTo);
        return Float.parseFloat(result);
    }

    public static String getOandaExchangeRate(String amount, String cur1, String cur2) {
        // http://www.oanda.com/converter/classic?value=1.0&exch=USD&expr=CAD
        String req = "http://www.oanda.com/converter/classic?value=" + amount + "&exch=" + cur1 + "&expr=" + cur2;
        try {
            parseOandaExchangeRate parser = new parseOandaExchangeRate();
            URL url = new URL(req);
            connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            ParserDelegator pd = new ParserDelegator();
            pd.parse(in, parser, true);
            connection = null;
            return parser.getResult();
        } catch (Exception e) {
            connection = null;
            return "";
        }
    }

    private static class parseOandaExchangeRate extends HTMLEditorKit.ParserCallback {
        private String result = "";

        private boolean inrange = false;
        private boolean found = false;
        private int fontCount = 0;
        private int fontMatch = 3;

        public String getResult() {
            return result;
        }

        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t == HTML.Tag.FONT && inrange) {
                fontCount++;
                if (fontCount == fontMatch) {
                    found = true;
                }
            }
        }

        public void handleText(char[] data, int pos) {
            if (found == true) {
                try {
                    String tempStr = new String(data);
                    int index = tempStr.indexOf(',');
                    if (index != -1) {
                        StringBuffer buf = new StringBuffer(tempStr);
                        buf.deleteCharAt(index);
                        tempStr = buf.toString();
                    }
                    result = new Float(tempStr).toString();
                } catch (NumberFormatException e) {
                    result = "";
                }
                found = false;
                inrange = false;
            }
        }

        public void handleComment(char[] data, int pos) {
            if (new String(data).trim().startsWith("conversion result starts")) {
                inrange = true;
            }
        }
    }
}
