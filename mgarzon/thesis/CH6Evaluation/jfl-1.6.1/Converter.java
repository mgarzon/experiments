/*
	Java Financial Library
	Luke Reeves <lreeves@member.fsf.org>
	Released under the LGPL (see included COPYING file)
	http://www.neuro-tech.net/
	$Id: Converter.java,v 1.8 2003/03/31 06:12:27 luke Exp $
	$Log: Converter.java,v $
	Revision 1.8  2003/03/31 06:12:27  luke
	Change default converter to Oanda.
	
	Revision 1.7  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.currency;
import java.util.*;
import java.net.*;
import java.io.*;

/** 
 * This is the primary currency converter class. 
 */
public final class Converter {	
	
	private Hashtable currencies;
	private ConvertSource myConverter = null;
	private boolean useCache = false;
	private String convertClass;

	public String getSourceClass() { 
		return convertClass; 
	}
	
	public void setSourceClass(String convertClass) { 
		this.convertClass = convertClass; 
	}

	/**
	 * Enables or disables caching of currency exchange values.
	 */
	public void setCaching(boolean p) {
		useCache = p;
	}

	/**
	 * This member function is used to set various options of the cache.
	 */
	public void setCacheOptions() {
	}

	public boolean getCaching()
	{
		return useCache;
	}
	
	/**	 
	 * The main constructer for the Converter class.  Uses the default Oanda backend
	 * for grabbing conversion data.
	 */
	public Converter() {		
		this("net.neurotech.currency.OandaSource");	
	}	
	
	/**	 
	 * The secondary constructer for the Converter class.  This allows the user
	 * to specify a class name that implements ConvertSource.
	 */
	public Converter(String convname) {
		this.convertClass = convname;
	}	
	
	/**	 
	 * Converts a specified amount of currency from the source symbol to the target 
	 * symbol. If an error occurs, a ConversionException is thrown.     
	 */	
	public float convert(int amount, String source, String target) throws ConversionException {		
		Float f = new Float(amount);		
		return convert(f.floatValue(), source, target);	
	}
	
	/**
	 * Converts a specified amount of currency from the source symbol to the target 
	 * symbol. If an error occurs, a ConversionException is thrown.	 
	 */	
	public float convert(float amount, String source, String target) throws ConversionException {
		ConvertSource myConverter;
		Class converterClass;

		try {
			converterClass = Class.forName(convertClass);
		} catch (ClassNotFoundException cnf) {
			throw new ConversionException("Error finding specified class \"" + convertClass + "\".");
		}

		/* Attempt to instantiate the converter into myConverter */
		try {
			myConverter = (ConvertSource)converterClass.newInstance();
		} catch (ClassCastException cce) {
			throw new ConversionException("Class \"" + convertClass + "\" is of the wrong type.");
		} catch (InstantiationException ie) {
			throw new ConversionException("Unable to instantiate class \"" + convertClass + "\".");
		} catch (IllegalAccessException iae) {
			throw new ConversionException("Unable to access protected/private class \"" + convertClass + "\".");
		}

		// TODO: Rounding?

		return myConverter.getConverted(amount, source, target);	
	}
	
	/**
	 * The command-line version of the main Converter.
	 * Usage: Converter &lt;amount&gt; &lt;from symbol&gt; &lt;to symbol&gt;
	 */	
	public static void main(String args[]) throws ConversionException {
		if(args.length < 2) {
			System.out.println("Usage: Converter <amount> <from symbol> <to symbol>");
			return;
		}

		Converter myConverter = new Converter();	
		float result = myConverter.convert(Integer.parseInt(args[0]), args[1], args[2]);
		System.out.println(args[0] + " " + args[1] + " = " + result + " " + args[2]);
	}
}
