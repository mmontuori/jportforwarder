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
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.util.StringTokenizer;


public class GRemoveConfig extends JFrame {
  JPanel jPanel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel1 = new JLabel();
  JButton OKButton = new JButton();
  JButton cancelButton = new JButton();
  JButton btn;
  JComboBox configCombo = new JComboBox();

  public GRemoveConfig(JButton btn) {
    try {
      this.btn=btn;
      populateConfig();
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    GRemoveConfig gRemoveConfig = new GRemoveConfig(new JButton(""));
    gRemoveConfig.show();
  }
  private void jbInit() throws Exception {
    jPanel1.setLayout(xYLayout1);
    jLabel1.setText("Config Line:");
    OKButton.setText("OK");
    OKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OKButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    this.setTitle("Add Config");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1, new XYConstraints(25, 16, -1, -1));
    jPanel1.add(configCombo,   new XYConstraints(100, 12, 284, -1));
    jPanel1.add(cancelButton, new XYConstraints(187, 46, -1, -1));
    jPanel1.add(OKButton, new XYConstraints(133, 46, -1, -1));
    Toolkit tk = this.getToolkit();
    Dimension dim = tk.getScreenSize();
    this.setBounds(dim.width/2-200, dim.height/2-50, 400, 100);
    this.setResizable(false);
  }

  void OKButton_actionPerformed(ActionEvent e) {
    try {
      btn.setEnabled(true);
      removeConfig();
      this.dispose();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    btn.setEnabled(true);
    this.dispose();
  }

  private void populateConfig() {
    try {
    Config config = new Config(GConfig.configFile);
    JPortInfo[] jp = config.getConfig();
    for (int i=0; i<jp.length; i++) {
      configCombo.addItem(jp[i].toString());
    }
    } catch (Exception ee) {
      JOptionPane.showConfirmDialog(this, "Error populating config", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void removeConfig() {
    try {
    String selectedConfig = (String)configCombo.getSelectedItem();
    System.out.println(selectedConfig);
    Config config = new Config(GConfig.configFile);
    JPortInfo jPortInfo = new JPortInfo();
    StringTokenizer st = new StringTokenizer(selectedConfig);
    if (st.hasMoreTokens()) jPortInfo.protocol=st.nextToken();
    if (st.hasMoreTokens()) jPortInfo.src=st.nextToken();
    if (st.hasMoreTokens()) jPortInfo.dest=st.nextToken();
    if (st.hasMoreTokens()) jPortInfo.application=st.nextToken();
    config.deleteConfig(jPortInfo);
    config=null;
    } catch (Exception ex) {
      JOptionPane.showConfirmDialog(this, "Error removing config", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}