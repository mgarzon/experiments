/*
        Java Financial Library
        Luke Reeves <lreeves@member.fsf.org>
        Released under the LGPL (see included COPYING file)
        http://www.neuro-tech.net/
	$Id: MessageBox.java,v 1.6 2003/03/31 05:58:14 luke Exp $
	$Log: MessageBox.java,v $
	Revision 1.6  2003/03/31 05:58:14  luke
	Line feed fixes, e-mail address update.
	
*/

package net.neurotech;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
 * A quick class to display message boxes on the screen.
 */
public class MessageBox
{
	public MessageBox(String title, String msg)
	{
		final JFrame msgbox = new JFrame(title);
		msgbox.setSize(360, 90);
		msgbox.setLocation(170, 100);

		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(2, 1));

		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				msgbox.setVisible(false);
			}
		});

		JLabel message = new JLabel(msg);
		message.setHorizontalAlignment(SwingConstants.CENTER);

		pane.add(message);
		pane.add(btnOK);

		msgbox.getContentPane().add(pane);
		msgbox.setVisible(true);
	}

	public static void main(String args[])
	{
		/* Unit test */
		MessageBox mb = new MessageBox("Test", "This is a test.");
	}

}
