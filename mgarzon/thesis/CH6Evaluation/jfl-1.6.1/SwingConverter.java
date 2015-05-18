/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: SwingConverter.java,v 1.8 2003/03/31 05:58:14 luke Exp $
	$Log: SwingConverter.java,v $
	Revision 1.8  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.currency;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import net.neurotech.finance.GUI;

/**
 * GUI class for the currency conversion process.
 */
public final class SwingConverter
{
	private Vector vec;
	private Lister lister;
	private Hashtable ht;
	private GUI parent;
	private Converter myConverter = new Converter();

	public SwingConverter(GUI parent) throws ConversionException
	{
		this.parent = parent;
		Lister lister = new Lister();
		vec = lister.getVector();
		ht = lister.getTable();
	}

	public SwingConverter() throws ConversionException
	{
		this(null);
	}

	public Component createComponents() throws ConversionException
	{

		final JPanel pane = new JPanel();
		final JComboBox from = new JComboBox(vec);
		final JComboBox to = new JComboBox(vec);
		final JTextField amount = new JTextField("");
		final JLabel result = new JLabel("Ready.");
		final JButton button = new JButton("Convert");
		final JLabel dummy = new JLabel("");
		//final JButton close = new JButton("Close");

		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				// Set the class
				if(parent != null) myConverter.setSourceClass(parent.getConvertClass());

				float i;

				try {
					i = Float.parseFloat(amount.getText());
				} catch (NumberFormatException nfe) {
					new net.neurotech.MessageBox("Error", "I can only convert numbers.");
					return;
				}

				String xl = (String)from.getSelectedItem();
				String yl = (String)to.getSelectedItem();
				String x = ""; String y = "";

				for(Enumeration en =ht.keys(); en.hasMoreElements();)
				{
					String key = (String)en.nextElement();
					String value = (String)ht.get(key);
					if(value.equals(xl)) x = key;
					if(value.equals(yl)) y = key;
				}

				float f;
				try {
					f = myConverter.convert(i, x, y);
				} catch (ConversionException ce) { f = -1; }
				result.setText(Float.toString(f) + " " + y);
			}
		});

		pane.setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
		pane.setLayout(new GridLayout(2, 3));

		pane.add(amount);
		pane.add(from);
		pane.add(to);

		pane.add(button);
		//pane.add(close);
		result.setHorizontalAlignment(SwingConstants.CENTER);
		pane.add(result);
		pane.add(dummy);
/*
		pane.add(dummy);
		pane.add(dummy);
		pane.add(dummy);*/

		return pane;
	}

	public static void main(String args[]) throws ConversionException
	{
		try{
			UIManager.setLookAndFeel(
				UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {}
		JFrame frame = new JFrame("JFL - Currency Converter");
		frame.setSize(550, 120);
		frame.setLocation(100, 100);
		SwingConverter app = new SwingConverter();
		frame.getContentPane().add(app.createComponents(), BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}});
		frame.setVisible(true);
	}

}
