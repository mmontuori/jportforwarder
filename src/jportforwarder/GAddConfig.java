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


public class GAddConfig extends JFrame {
  JPanel jPanel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField sourceText = new JTextField();
  JTextField destinationText = new JTextField();
  JTextField applicationText = new JTextField();
  JButton OKButton = new JButton();
  JButton cancelButton = new JButton();
  JButton btn;
  JComboBox protocolCombo = new JComboBox();

  public GAddConfig(JButton btn) {
    try {
      this.btn=btn;
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    GAddConfig gAddConfig = new GAddConfig(new JButton(""));
    gAddConfig.show();
  }
  private void jbInit() throws Exception {
    jPanel1.setLayout(xYLayout1);
    jLabel1.setText("Protocol:");
    jLabel2.setText("Source:");
    jLabel3.setText("Destination:");
    jLabel4.setText("Application:");
    sourceText.setColumns(10);
    destinationText.setColumns(10);
    applicationText.setColumns(10);
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
    jPanel1.add(jLabel2,   new XYConstraints(31, 47, -1, -1));
    jPanel1.add(jLabel3,   new XYConstraints(7, 77, -1, -1));
    jPanel1.add(jLabel4,  new XYConstraints(10, 108, -1, -1));
    jPanel1.add(sourceText,    new XYConstraints(83, 43, -1, -1));
    jPanel1.add(destinationText,    new XYConstraints(83, 73, -1, -1));
    jPanel1.add(applicationText,    new XYConstraints(83, 104, -1, -1));
    jPanel1.add(protocolCombo,      new XYConstraints(83, 12, 111, -1));
    jPanel1.add(cancelButton, new XYConstraints(92, 144, -1, -1));
    jPanel1.add(OKButton, new XYConstraints(38, 144, -1, -1));
    populateProtocol();
    Toolkit tk = this.getToolkit();
    Dimension dim = tk.getScreenSize();
    this.setBounds(dim.width/2-105, dim.height/2-100, 210, 200);
  }

  void OKButton_actionPerformed(ActionEvent e) {
    try {
      Config config = new Config("ports.txt");
      JPortInfo jPortInfo = new JPortInfo(  (String)protocolCombo.getSelectedItem(),
                                            sourceText.getText(),
                                            destinationText.getText(),
                                            applicationText.getText());
      config.addConfig(jPortInfo);
      btn.setEnabled(true);
      this.dispose();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    btn.setEnabled(true);
    this.dispose();
  }

  private void populateProtocol() {
    protocolCombo.addItem("tcp");
    protocolCombo.addItem("udp");
    protocolCombo.addItem("tcp-ssl");
    protocolCombo.addItem("ssl-tcp");
  }
}