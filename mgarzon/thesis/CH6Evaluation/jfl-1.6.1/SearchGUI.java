/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: SearchGUI.java,v 1.5 2003/03/31 05:58:14 luke Exp $
	$Log: SearchGUI.java,v $
	Revision 1.5  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech.quotes;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import net.neurotech.finance.GUI;

public class SearchGUI
{
	private GUI parent;

	public SearchGUI() { this.parent = null; }
	public SearchGUI(GUI parent) { this.parent = parent; }

	public Component createComponents()
	{
		final JPanel pane = new JPanel();

		final JLabel lbName = new JLabel("Company Name:");
		final JTextField tfName = new JTextField("");
		final JButton btnMain = new JButton("Search");

		btnMain.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					Searcher searcher;

					if(parent != null) searcher = new Searcher(parent.getSearchClass());
					else searcher = new Searcher();

					LinkedList ll = searcher.search(tfName.getText());
					new SearchPopup(ll);
				} catch(SearchException se) {
					net.neurotech.MessageBox mb = new net.neurotech.MessageBox("Error", se.getMessage());
				}
			}
		});

		pane.setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
		pane.setLayout(new GridLayout(3, 1));

		pane.add(lbName);
		pane.add(tfName);
		pane.add(btnMain);

		return pane;
	}

	public static void main(String args[])
	{
		try{
			UIManager.setLookAndFeel(
				UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {}
		JFrame frame = new JFrame("JFL - Symbol Search");
		frame.setSize(550, 220);
		frame.setLocation(100, 100);
		SearchGUI app = new SearchGUI();
		frame.getContentPane().add(app.createComponents(), BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}});
		frame.setVisible(true);
	}

}
