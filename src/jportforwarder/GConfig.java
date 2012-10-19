/**
 * <p>Title: Port Forwarder</p>
 * Copyright: Copyright 2005 Michael Montuori
 * <p>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * @author Michael Montuori
 * @version 0.1
 */

package jportforwarder;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;


public class GConfig extends JFrame {
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  JButton jButton1 = new JButton();
  JButton jButton3 = new JButton();
  ImageIcon image1;
  ImageIcon image2;
  ImageIcon image3;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea configText = new JTextArea();
  JButton exitButton = new JButton();
  JButton addButton = new JButton();
  JButton removeButton = new JButton();
  JPortInfo[] jPortInfo;
  public static String configFile=null;

  //Construct the frame
  public GConfig() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      //loadConfigFile();
      jbInit();
      //showConfig();
    }
    catch(Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Error initializing GConfig\n"+e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  //Component initialization
  private void jbInit() throws Exception  {
    image1 = new ImageIcon(jportforwarder.GConfig.class.getResource("openFile.gif"));
    image2 = new ImageIcon(jportforwarder.GConfig.class.getResource("closeFile.gif"));
    image3 = new ImageIcon(jportforwarder.GConfig.class.getResource("help.gif"));
    //setIconImage(Toolkit.getDefaultToolkit().createImage(GConfig.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(400, 300));
    this.setTitle("JPortForwarder Config");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jButton1.setIcon(image1);
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jButton1.setToolTipText("Open File");
    jButton3.setIcon(image3);
    jButton3.setToolTipText("Help");
    exitButton.setText("Exit");
    exitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitButton_actionPerformed(e);
      }
    });
    addButton.setText("Add");
    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButton_actionPerformed(e);
      }
    });
    removeButton.setText("Remove");
    removeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeButton_actionPerformed(e);
      }
    });
    jToolBar.add(jButton1);
    jToolBar.add(jButton3);
    contentPane.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(addButton, null);
    jPanel1.add(removeButton, null);
    contentPane.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(configText, null);
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);
    contentPane.add(jToolBar, BorderLayout.NORTH);
    jPanel1.add(exitButton, null);
    this.getRootPane().setDefaultButton(addButton);
  }
  //File | Exit action performed
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }
  //Help | About action performed
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    GConfig_AboutBox dlg = new GConfig_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  private void exitButton_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  private void loadConfigFile() throws Exception {
    Config config = new Config(configFile);
    jPortInfo = config.getConfig();
  }

  private void showConfig() {
    configText.setText("");
    if (jPortInfo != null) {
      for (int i=0; i<jPortInfo.length; i++) {
        configText.append(jPortInfo[i].protocol+"\t");
        configText.append(jPortInfo[i].src+"\t");
        configText.append(jPortInfo[i].dest+"\t");
        configText.append(jPortInfo[i].application+"\n");
      }
    }
  }

  private void addConfig() {
    GAddConfig add = new GAddConfig(addButton);
    add.show();
  }

  private void removeConfig() {
    GRemoveConfig remove = new GRemoveConfig(removeButton);
    remove.show();
  }

  void addButton_actionPerformed(ActionEvent e) {
    if (configFile!=null) {
      addButton.setEnabled(false);
      addConfig();
    } else {
      JOptionPane.showMessageDialog(this, "Please open a configuration file",
          "JPortForwarder", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  void removeButton_actionPerformed(ActionEvent e) {
    if (configFile!=null) {
      removeButton.setEnabled(false);
      removeConfig();
    } else {
      JOptionPane.showMessageDialog(this, "Please open a configuration file",
          "JPortForwarder", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {
    try {
      JFileChooser jf = new JFileChooser(".");
      int response = jf.showOpenDialog(this);
      if (response == JFileChooser.APPROVE_OPTION) {
        configFile = jf.getSelectedFile().getAbsolutePath();
        loadConfigFile();
        showConfig();
      }
    } catch (Exception ex) {
      JOptionPane.showConfirmDialog(this, "Error opening config file", "Error",
            JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void main(String[] args) {
    GConfig c = new GConfig();
    c.show();
  }

  public void show() {
    if (configFile==null) {
      jButton1_actionPerformed(null);
    }
    super.show();
  }
}