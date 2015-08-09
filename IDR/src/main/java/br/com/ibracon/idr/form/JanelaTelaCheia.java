package br.com.ibracon.idr.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class JanelaTelaCheia
{
    private static GraphicsDevice defaultScreen;

    private GraphicsDevice screen;

    private JFrame jf;

    private boolean dead= false;

    public JanelaTelaCheia(JComponent part, boolean forcechoice) {
	//	super();
	init(part, forcechoice);
    }

    public JanelaTelaCheia(JComponent part) {
	//	super();
	init(part, false);
    }

    public void fechar() {
	dead= true;
	flag.set();
	screen.setFullScreenWindow(null);
	if (jf!=null) {
	    jf.dispose();
	}
    }

    private void init(JComponent part, boolean forcechoice) {
	if (forcechoice) {
	    defaultScreen= null;
	}
	screen= null;

	GraphicsEnvironment ge=
	    GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice screens[]= ge.getScreenDevices();
	if (defaultScreen!=null) {
	    for (int i=0; i<screens.length; i++) {
		if (screens[i]== defaultScreen) {
		    screen= defaultScreen;
		}
	    }
	}

	if (screens.length==1) {
	    screen= screens[0];
	}
	if (screen==null) {
	    screen= pickScreen(screens);
	}
	if (dead) {
	    return;
	}
	defaultScreen= screen;
	DisplayMode dm= screen.getDisplayMode();
	GraphicsConfiguration gc= screen.getDefaultConfiguration();
	jf= new JFrame(gc);
	jf.setUndecorated(true);
	jf.setBounds(gc.getBounds());
	jf.getContentPane().add(part);
	jf.setVisible(true);
	screen.setFullScreenWindow(jf);
    }

    class PickMe extends JFrame {
	GraphicsDevice mygd;

	public PickMe(GraphicsDevice gd) {
	    super(gd.getDefaultConfiguration());
	    setUndecorated(true);
	    mygd= gd;
	    JButton jb= new JButton("Click here to use this screen");
	    jb.setBackground(Color.yellow);
	    jb.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
			pickDevice(mygd);
		    }
		});
	    Dimension sz= jb.getPreferredSize();
	    sz.width+= 30;
	    sz.height= 200;
	    jb.setPreferredSize(sz);
	    getContentPane().add(jb);
	    pack();
	    Rectangle bounds= gd.getDefaultConfiguration().getBounds();
	    int x= bounds.width/2-sz.width/2+bounds.x;
	    int y= bounds.height/2-sz.height/2+bounds.y;
	    setLocation(x,y);	    
	    setVisible(true);
	}
    }

    private Flag flag= new Flag();
    private GraphicsDevice pickedDevice;

    private void pickDevice(GraphicsDevice gd) {
	pickedDevice= gd;
	flag.set();
    }

    private GraphicsDevice pickScreen(GraphicsDevice scrns[]) {
	flag.clear();
	int count=0;
	PickMe pickers[]= new PickMe[scrns.length];
	for (int i=0; i<scrns.length; i++) {
	    if (scrns[i].isFullScreenSupported()) {
		count++;
	    }
	    pickers[i]= new PickMe(scrns[i]);
	}
	flag.waitForFlag();
	for (int i=0; i<pickers.length; i++) {
	    if (pickers[i]!=null) {
		pickers[i].dispose();
	    }
	}
	return pickedDevice;
    }
}
