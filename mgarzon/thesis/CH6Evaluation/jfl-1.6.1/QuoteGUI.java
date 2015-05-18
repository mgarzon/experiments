/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: QuoteGUI.java,v 1.11 2003/03/31 05:58:14 luke Exp $
	$Log: QuoteGUI.java,v $
	Revision 1.11  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import net.neurotech.finance.GUI;

/**
 * A GUI interface to allow fetching of quotes.
 */
public class QuoteGUI
{
	private GUI parent;
	private QuoteFactory qf;
	private Component myComponents;
	private final JTextField tfSymbol = new JTextField("symbol");
	private final JButton btnMain = new JButton("Get Quote");

	public QuoteGUI(GUI parent)
	{
		this.parent = parent;
		qf = new QuoteFactory();
	}

	public QuoteGUI()
	{
		this.parent = null;
		qf = new QuoteFactory();
	}

	public Component getComponents()
	{
		if(myComponents == null) myComponents = createComponents();
		return myComponents;
	}

	public void grabQuote(String symb)
	{
		tfSymbol.setText(symb);
		btnMain.doClick();
	}

	private Component createComponents()
	{

		final JPanel pane = new JPanel();

		final JLabel lbCompany = new JLabel("Company:");
		final JLabel stCompany = new JLabel("");
		final JLabel lbPrice = new JLabel("Price:");
		final JLabel stPrice = new JLabel("");
		final JLabel lbVolume = new JLabel("Volume:");
		final JLabel stVolume = new JLabel("");

		btnMain.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Quote current = null;

				// Set the quoteFactory to use the current set
				if(parent != null) qf.setSource(parent.getQuoteClass());

				try {
					current = qf.getQuote(tfSymbol.getText());
				} catch (QuoteException qe) {
					qe.printStackTrace();

					// Notify the user that there was a problem.
					net.neurotech.MessageBox mb = new net.neurotech.MessageBox("Error", qe.getMessage());
					return;
				}

				if(current != null) {
					stPrice.setText(new Float(current.getValue()).toString());
					stVolume.setText(new Long(current.getVolume()).toString());
					stCompany.setText(current.getCompany());
				}
			}
		});

		pane.setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
		pane.setLayout(new GridLayout(4, 2));

		pane.add(tfSymbol);
		pane.add(btnMain);
		pane.add(lbCompany);
		pane.add(stCompany);
		pane.add(lbPrice);
		pane.add(stPrice);
		pane.add(lbVolume);
		pane.add(stVolume);

		return pane;
	}

	public static void main(String args[])
	{
		try{
			UIManager.setLookAndFeel(
				UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {}
		JFrame frame = new JFrame("JFL - Stock Quotes");
		frame.setSize(550, 220);
		frame.setLocation(100, 100);
		QuoteGUI app = new QuoteGUI();
		frame.getContentPane().add(app.getComponents(), BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}});
		frame.setVisible(true);
	}

}
