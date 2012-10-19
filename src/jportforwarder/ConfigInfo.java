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

public class ConfigInfo {
  public String protocol;
  public String localhost;
  public String remotehost;
  public String application;
  public ConfigInfo() {}
  public ConfigInfo(String protocol, String localhost, String remotehost,
                    String application) {
    this.protocol=protocol;
    this.localhost=localhost;
    this.remotehost=remotehost;
    this.application=application;
  }
  public String toString() {
    String ret=protocol+" "+localhost+" "+remotehost;
    if (application!=null) {
      ret+=" "+application;
    }
    return ret;
  }
}