/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: SearchPopup.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: SearchPopup.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import net.neurotech.finance.*;

public class SearchPopup
{

	public SearchPopup(LinkedList results)
	{
		final JFrame frame = new JFrame("Search Results");
		frame.setSize(350, 420);
		frame.setLocation(120, 120);
		frame.getContentPane().add(createComponents(), BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {}});

		// Add the search results to the list
		String res[] = new String[results.size()];
		SearchHit temp;
		for(int i = 0; i < results.size(); i++)
		{
			temp = (SearchHit)results.get(i);
			res[i] = temp.getSymbol() + " - " +temp.getResult();
		}

		JScrollPane p = new JScrollPane();
		JButton btnGet = new JButton("Get Quote");
		final JList resultList = new JList(res);
		p.getViewport().setView(resultList);
		final LinkedList ll = (LinkedList)results.clone();

		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(p, BorderLayout.CENTER);
		frame.getContentPane().add(btnGet, BorderLayout.SOUTH);

		resultList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e)
			{
				// Make focus good on the button
			}
		});

		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				// Grab the symbol
				if ( resultList.getSelectedIndex() == -1 ) return;
				String symb = (String)resultList.getSelectedValue();
				symb = symb.substring(0, symb.indexOf('-'));
				symb = symb.trim();
				GUI.getInstance().grabQuote(symb);
				// Close window
				frame.hide();
			}
		});

		frame.setVisible(true);
	}

	public Component createComponents()
	{
		final JPanel pane = new JPanel();
		final JLabel lbName = new JLabel("Company Name:");
		return pane;
	}

}
