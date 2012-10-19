/**
 * Title:        JPortForwarder<p>
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

class JPortInfo {
  public String protocol;
  public String src;
  public String dest;
  public String application;
  public JPortInfo() {}
  public JPortInfo(String protocol, String src, String dest,
                    String application) {
    this.protocol=protocol;
    this.src=src;
    this.dest=dest;
    this.application=application;
  }
  public String toString() {
    String ret=protocol+" "+src+" "+dest;
    if (application!=null) {
      ret+=" "+application;
    }
    return ret;
  }
}