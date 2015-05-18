/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: GUI.java,v 1.17 2003/03/31 05:58:14 luke Exp $
	$Log: GUI.java,v $
	Revision 1.17  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.finance;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import net.neurotech.currency.*;
import net.neurotech.quotes.*;

/**
 * The main GUI exposing access to most of the functionality of the JFL.
 */
public class GUI
{
	private static GUI instance;

	// Classes for data
	private String quoteClass = "", convertClass = "", searchClass = "";

	private QuoteGUI quoteg;
	private Component convertComp, quoteComp, searchComp, optionsComp;
	private JTabbedPane jtp;
	private Options options;

	public static GUI getInstance() { return instance; }
	public static void setInstance(GUI i) { instance = i; }

	public void setClasses(String quote, String convert, String search)
	{
		quoteClass = quote;
		convertClass = convert;
		searchClass = search;
	}

	public String getQuoteClass() { return quoteClass; }
	public String getConvertClass() { return convertClass; }
	public String getSearchClass() { return searchClass; }

	public void grabQuote(String symbol)
	{
		jtp.setSelectedComponent(quoteComp);
		quoteg.grabQuote(symbol);
	}

	public Component createComponents() throws Exception
	{
		JPanel pane = new JPanel();

		jtp = new JTabbedPane();
		options = new Options(this);
		quoteg = new QuoteGUI(this);

		convertComp = new SwingConverter(this).createComponents();
		quoteComp   = quoteg.getComponents();
		searchComp  = new SearchGUI(this).createComponents();
		optionsComp = options.getComponents();

		jtp.addTab("Currencies", convertComp);
		jtp.addTab("Stock Quotes", quoteComp);
 		jtp.addTab("Symbol Lookup", searchComp);
 		jtp.addTab("Options", optionsComp);

		pane.add(jtp);

		return pane;
	}

	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(
			UIManager.getCrossPlatformLookAndFeelClassName());

		JFrame frame = new JFrame("Java Financial Library");
		frame.setSize(620, 190);
		frame.setLocation(100, 100);
		GUI app = new GUI();
		GUI.setInstance(app);
		frame.getContentPane().add(app.createComponents(), BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}});
		frame.setVisible(true);
	}
}
