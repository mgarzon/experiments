/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: Options.java,v 1.8 2003/03/31 06:20:18 luke Exp $
	$Log: Options.java,v $
	Revision 1.8  2003/03/31 06:20:18  luke
	Fix GUI default sources
	
	Revision 1.7  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.finance;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
 * This is a GUI interface to the various options of the Java Financial Library.
 */
public class Options
{
	private JPanel pane;
	private GUI parent = null;

	private final JLabel jl_convert = new JLabel("Conversion Source Class:");
	private final JComboBox cb_convert = new JComboBox(); //("net.neurotech.currency.YahooParser");

	private final JLabel jl_quote = new JLabel("Quote Source Class:");
	private final JComboBox cb_quote = new JComboBox(); //("net.neurotech.quotes.YahooSource");

	private final JLabel jl_symbol = new JLabel("Symbol Lookup Class:");
	private final JComboBox cb_symbol = new JComboBox(); //("net.neurotech.quotes.YahooSearchSource");

	private final JButton btnApply = new JButton("Apply");

	public Options()
	{
		this(null);
	}

	public Options(GUI parent)
	{
		this.parent = parent;

		cb_convert.addItem("net.neurotech.currency.OandaSource");
		cb_convert.addItem("net.neurotech.currency.YahooParser");
		cb_quote.addItem("net.neurotech.quotes.YahooCSVSource");
		cb_symbol.addItem("net.neurotech.quotes.YahooSearchSource");

		pane = new JPanel();

		btnApply.setEnabled(false);

		pane.setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
		pane.setLayout(new GridLayout(4, 2));

		pane.add(jl_convert);
		pane.add(cb_convert);

		pane.add(jl_quote);
		pane.add(cb_quote);

		pane.add(jl_symbol);
		pane.add(cb_symbol);

		// Bind the textfields
		ActionListener al = new optionModified();
		cb_convert.addActionListener(al);
		cb_quote.addActionListener(al);
		cb_symbol.addActionListener(al);

		btnApply.addActionListener(new applyChanges());

		pane.add(new JLabel(""));

		set();

		pane.add(btnApply);
	}

	private void set()
	{
		if(parent != null)
			parent.setClasses(
				cb_quote.getSelectedItem().toString(),
				cb_convert.getSelectedItem().toString(),
				cb_symbol.getSelectedItem().toString()
			);
	}

	class applyChanges implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			//Disable the button if successfull
			btnApply.setEnabled(false);
			set();
		}
	}

	// This is to allow the Apply button to be clicked
	class optionModified implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)	{ btnApply.setEnabled(true); }
	}

	public Component getComponents()
	{
		return pane;
	}

}
