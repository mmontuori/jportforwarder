/**
 * <p>Title: JPortForwarder</p>
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

import java.io.*;
import java.util.*;

public class Config {

  private String configFile=null, line=null;
  private BufferedReader br;
  private PrintWriter pw;
  private StringTokenizer st;
  private Vector v;

  public Config(String configFile) {
    this.configFile=configFile;
  }
  public static void main(String[] args) throws Exception {
    Config config1 = new Config("ports.txt");
    JPortInfo[] ci = config1.getConfig();
    /*for (int i=0; i<ci.length; i++) {
      System.out.println("PROTOCOL : " + ci[i].protocol);
      System.out.println("LOCALHOST : " + ci[i].localhost);
      System.out.println("REMOTEHOST : " + ci[i].remotehost);
      System.out.println("APPLICATION : " + ci[i].application);
    }
    JPortInfo JPortInfo = new JPortInfo("tcp","81","www.yahoo.com:90",null);
    config1.addConfig(JPortInfo);
    */
    /*ci[0] = new JPortInfo("tcp", "80", "www.yahoo.com:80", null);
    ci[1] = new JPortInfo("tcp", "80", "www.yahoo.com:80", null);
    System.out.println(config1.compareJPortInfo(ci[0], ci[1]));*/
    JPortInfo configInfo = new JPortInfo("tcp", "81", "www.yahoo.com:90", null);
    config1.deleteConfig(configInfo);
  }

  public void modifyConfig(JPortInfo oldConfigInfo, JPortInfo newConfigInfo)
        throws Exception {
    deleteConfig(oldConfigInfo);
    addConfig(newConfigInfo);
  }

  public void addConfig(JPortInfo configInfo) throws Exception {
    if (configFile==null || configInfo==null) {
      throw new Exception("Config file or JPortInfo object cannot be null");
    }
    pw = new PrintWriter(new FileWriter(configFile, true));
    line =  configInfo.protocol + " " +
            configInfo.src + " " +
            configInfo.dest;
    if (configInfo.application != null && !configInfo.application.trim().equals("")) {
            line += " " + configInfo.application;
    }
    pw.println(line);
    pw.flush();
    pw.close();
    pw=null;
  }

  public void deleteConfig(JPortInfo configInfo) throws Exception {
    br = new BufferedReader(new FileReader(configFile));
    v = new Vector(50,50);
    line=null;
    while ((line=br.readLine()) != null) {
      v.add(line);
    }
    br.close();
    br=null;
    pw = new PrintWriter(new FileWriter(configFile));
    for (int i=0; i<v.size(); i++) {
      if (!compareConfigInfo(configInfo, getConfigInfo((String)v.elementAt(i))) &&
          !((String)v.elementAt(i)).trim().equals("")) {
        pw.println((String)v.elementAt(i));
      }
    }
    pw.flush();
    pw.close();
    pw=null;
  }

  private JPortInfo getConfigInfo(String line) {
    st = new StringTokenizer(line);
    JPortInfo ci = new JPortInfo();
    if (st.hasMoreTokens()) ci.protocol=(String)st.nextToken();
    if (st.hasMoreTokens()) ci.src=(String)st.nextToken();
    if (st.hasMoreTokens()) ci.dest=(String)st.nextToken();
    if (st.hasMoreTokens()) {
      ci.application=(String)st.nextToken();
    } else {
      ci.application=null;
    }
    return ci;
  }

  private boolean compareConfigInfo(JPortInfo ci1, JPortInfo ci2) {
    boolean ret=false;
    if (ci1.application==null) ci1.application="";
    if (ci2.application==null) ci2.application="";
    if (ci1.protocol.equals(ci2.protocol) &&
        ci1.src.equals(ci2.src) &&
        ci1.dest.equals(ci2.dest) &&
        ci1.application.equals(ci2.application)) {
      ret=true;
    }
    return ret;
  }

  public JPortInfo[] getConfig() throws Exception {
    JPortInfo[] configInfo=null;
    Vector lines = new Vector();
    br = new BufferedReader(new FileReader(configFile));
    line=null;
    while ((line=br.readLine()) != null) {
      if (!line.trim().startsWith("#") &&
          !line.trim().equals("")) {
        lines.add(line);
      }
    }
    configInfo = new JPortInfo[lines.size()];
    for (int i=0; i<lines.size(); i++) {
      configInfo[i] = new JPortInfo();
    }
    for (int i=0; i<lines.size(); i++) {
      st = new StringTokenizer((String)lines.elementAt(i));
      while (st.hasMoreTokens()) {
        configInfo[i].protocol=st.nextToken();
        if (st.hasMoreTokens()) configInfo[i].src=st.nextToken();
        if (st.hasMoreTokens()) configInfo[i].dest=st.nextToken();
        if (st.hasMoreTokens()) configInfo[i].application=st.nextToken();
      }
    }
    br.close();
    br=null;
    return configInfo;
  }
}