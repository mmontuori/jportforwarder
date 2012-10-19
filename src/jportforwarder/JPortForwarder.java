/**
 * Title: JPortForwarder
 * <p>
 * Copyright: Copyright 2005 Michael Montuori
 * <p>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * @author Michael Montuori
 * @version 0.1
 */

package jportforwarder;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.*;
import javax.net.ssl.*;
//import com.sun.net.ssl.*;
import java.security.*;

public class JPortForwarder {

	private static boolean debug = true;

	private Vector ports;

	private String portFile, sslFile, sslPassword;

	private boolean ssfInitialized = false, sfinitialized = false;

	private static Hashtable dataConn;

	private SSLContext ctx;

	private KeyManagerFactory kmf;

	private KeyStore ks;

	private SSLServerSocketFactory ssf = null;

	private SSLSocketFactory sf;

	public JPortForwarder() {
	}

	public static void usage() {
		System.err
				.println("Usage java jportforwarder.JPortForewarder {port File}"
						+ " [sslfile] [sslFilePassword]");
		System.exit(-1);

	}

	public static void main(String[] args) {
		System.out.println("JPortForwarder v.0.1.1");
		System.out.println("(C)2005 Michael Montuori");
		System.out.println("Report errors to montuori@sourceforge.net");
		System.out.println();
		if (args.length < 1) {
			usage();
		}
		JPortForwarder pf = new JPortForwarder();
		pf.portFile = args[0];
		if (args.length == 3) {
			pf.sslFile = args[1];
			pf.sslPassword = args[2];
		}
		pf.populatePorts();
		pf.init();
	}

	public static InetAddress getInetAddress(String host) {
		InetAddress iaddr = null;
		try {
			iaddr = InetAddress.getAllByName(host)[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iaddr;
	}

	private void init() {
		Enumeration enu = ports.elements();
		while (enu.hasMoreElements()) {
			JPortInfo info = (JPortInfo) enu.nextElement();
			final String protocol = (String) info.protocol;
			final String src = (String) info.src;
			String tempPort;
			String tempHost;
			if (src.indexOf(":") > -1) {
				StringTokenizer st1 = new StringTokenizer(src, ":");
				tempHost = st1.nextToken();
				tempPort = st1.nextToken();
			} else {
				tempHost = null;
				tempPort = info.src;
			}
			final String daemonHost = tempHost;
			final String port = tempPort;
			final String dest = (String) info.dest;
			final String application = info.application;
			Thread daemon = new Thread() {
				public void run() {
					try {
						StartTCP s = null;
						if (protocol.equalsIgnoreCase("ssl-tcp")) {
							if (!ssfInitialized) {
								System.setProperty("http.keepAlive", "false");
								Security
										.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

								char[] passphrase = null;
								if (sslPassword != null && sslFile != null) {
									passphrase = sslPassword.toCharArray();
								} else {
									usage();
								}

								ctx = SSLContext.getInstance("SSL");
								kmf = KeyManagerFactory.getInstance("SunX509");
								ks = KeyStore.getInstance("JKS");

								ks.load(new FileInputStream(sslFile),
										passphrase);
								kmf.init(ks, passphrase);
								ctx.init(kmf.getKeyManagers(), null, null);

								ssf = ctx.getServerSocketFactory();
								ssfInitialized = true;
							}
						}
						if (protocol.equalsIgnoreCase("tcp-ssl")) {
							/*
							 * System.setProperty("http.keepAlive", "false");
							 * Security.addProvider( new
							 * com.sun.net.ssl.internal.ssl.Provider());
							 *
							 * char[] passphrase=null; if (sslPassword != null &&
							 * sslFile != null) { passphrase =
							 * sslPassword.toCharArray(); } else { usage(); }
							 *
							 * ctx = SSLContext.getInstance("SSL"); kmf =
							 * KeyManagerFactory.getInstance("SunX509"); ks =
							 * KeyStore.getInstance("JKS");
							 *
							 * ks.load(new FileInputStream(sslFile),
							 * passphrase); kmf.init(ks, passphrase);
							 * ctx.init(kmf.getKeyManagers(), null, null); sf =
							 * ctx.getSocketFactory();
							 *
							 * //sf =
							 * (SSLSocketFactory)SSLSocketFactory.getDefault();
							 */
							System.setProperty("http.keepAlive", "false");
							java.security.Security
									.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
							System.setProperty("http.keepAlive", "false");

							if (sslFile != null) {
								System.setProperty("javax.net.ssl.trustStore",
										sslFile);
							}
							ctx = SSLContext.getInstance("SSL");
							kmf = KeyManagerFactory.getInstance("SunX509");
							ks = KeyStore.getInstance("JKS");
							if (sslPassword != null) {
								ks.load(new FileInputStream(sslFile),
										sslPassword.toCharArray());
								kmf.init(ks, sslPassword.toCharArray());
								ctx.init(kmf.getKeyManagers(), null, null);
								sf = ctx.getSocketFactory();
							} else {
								sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
							}

						}
						if (protocol.startsWith("tcp")
								|| protocol.equalsIgnoreCase("ssl-tcp")) {
							s = new StartTCP(protocol, daemonHost, Integer
									.parseInt(port), dest, application, ssf, sf);
						} else {
							System.out
									.println("Currently only TCP is supported");
						}
						s.init();
					} catch (Exception e) {
						System.err.println("run()");
						e.printStackTrace();
					}
				}
			};
			daemon.start();
		}
	}

	/**
	 * This method populates a Vector of PortInfo data from a file named
	 * ports.txt which is structured as the following protocol {[host:]listening
	 * port} {host:forwarding port} [application handler]
	 *
	 * protocol = tcp or udp (crrently supports only tcp) [host:]listening port
	 * host = address to bind ip deamon to port = ip port to listen to
	 * host:forwarding port host = host or ip to forward the socket port = port
	 * to forward application handler = ftp
	 */
	private void populatePorts() {
		System.out.print("Reading " + portFile + " file...");
		try {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(portFile));
			} catch (FileNotFoundException fnfe) {
				System.err.println("File " + portFile + " not found!");
				System.exit(-1);
			}
			ports = new Vector();
			String line = null;
			while ((line = in.readLine()) != null) {
				if (!line.trim().equals("")) {
					if (!line.substring(0, 1).equals("#")) {
						JPortInfo info = new JPortInfo();
						StringTokenizer st = new StringTokenizer(line);
						info.protocol = st.nextToken();
						info.src = st.nextToken();
						info.dest = st.nextToken();
						if (st.hasMoreTokens()) {
							info.application = st.nextToken();
						}
						ports.add(info);
					}
				}
			}
			System.out.println(" done");
			System.out.println();
		} catch (Exception e) {
			System.err.println("Problem reading " + portFile + " file!");
			System.exit(-1);
		}
	}

	private class StartTCP {

		private int port;

		private String destIp, daemonHost, protocol, application;

		private int destPort;

		private ServerSocket serverSocket;

		private SSLServerSocketFactory ssf;

		private SSLSocketFactory sf;

		private Socket source, destination;

		private InetAddress bindedIp;

		public StartTCP(String protocol, String daemonHost, int port,
				String dest, String application) {
			this(protocol, daemonHost, port, dest, application, null, null);
		}

		public StartTCP(String protocol, String daemonHost, int port,
				String dest, String application, SSLServerSocketFactory ssf,
				SSLSocketFactory sf) {
			this.port = port;
			StringTokenizer st = new StringTokenizer(dest, ":");
			this.destIp = (String) st.nextToken();
			this.destPort = Integer.parseInt(st.nextToken());
			this.daemonHost = daemonHost;
			this.protocol = protocol;
			this.application = application;
			this.bindedIp = bindedIp;
			this.ssf = ssf;
			this.sf = sf;
			if (daemonHost == null) {
				System.out.print("Forwarding Socket " + protocol + "{*:" + port
						+ "}" + " to " + protocol + "{" + destIp + ":"
						+ destPort + "}");
			} else {
				System.out.print("Forwarding Socket " + protocol + "{"
						+ daemonHost + ":" + port + "}" + " to " + protocol
						+ "{" + destIp + ":" + destPort + "}");
			}
			if (application != null) {
				System.out.println(" with " + application + " handling");
			} else {
				System.out.println();
			}
		}

		public void init() throws Exception {
			if (daemonHost == null) {
				if (protocol.startsWith("tcp")) {
					serverSocket = new ServerSocket(port);
				} else if (protocol.equalsIgnoreCase("ssl-tcp")) {
					serverSocket = ssf.createServerSocket(port);
				}
			} else {
				if (protocol.startsWith("tcp")) {
					bindedIp = JPortForwarder.getInetAddress(daemonHost);
					if (bindedIp != null) {
						if (ssf != null) {
							serverSocket = ssf.createServerSocket(port, 0,
									bindedIp);
						} else {
							serverSocket = new ServerSocket(port, 0, bindedIp);
						}
					} else {
						throw new IOException("Error ip " + daemonHost
								+ " not found!");
					}
				} else if (protocol.equalsIgnoreCase("ssl-tcp")) {
					bindedIp = JPortForwarder.getInetAddress(daemonHost);
					if (bindedIp != null) {
						serverSocket = ssf
								.createServerSocket(port, 0, bindedIp);
						serverSocket = new ServerSocket(port, 0, bindedIp);
					} else {
						throw new IOException("Error ip " + daemonHost
								+ " not found!");
					}
				}
			}

			while (true) {
				try {
					source = serverSocket.accept();
					System.out.println("Open host "
							+ source.getInetAddress().getHostName() + "/"
							+ source.getInetAddress().getHostAddress()
							+ " connected to port " + port + " going to "
							+ destIp + ":" + destPort);
					if (application != null
							&& application.equalsIgnoreCase("cdtp")) {
						int oldTimeout = source.getSoTimeout();
						String connId = null;
						try {
							source.setSoTimeout(100);
							connId = (new BufferedReader(new InputStreamReader(
									source.getInputStream()))).readLine();
							System.out.println("String received from client: "
									+ connId);
						} catch (InterruptedIOException ie) {
							try {
								source.setSoTimeout(oldTimeout);
							} catch (Exception ee) {
								ee.printStackTrace();
							}
						} catch (Exception ee) {
							try {
								source.setSoTimeout(oldTimeout);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (debug)
								ee.printStackTrace();
						}
					}
					if (daemonHost == null) {
						if (protocol.equalsIgnoreCase("tcp-ssl")) {
							destination = sf.createSocket(destIp, destPort);
						} else {
							destination = new Socket(destIp, destPort);
						}
					} else {
						if (protocol.equalsIgnoreCase("tcp-ssl")) {
							destination = sf.createSocket(destIp, destPort,
									bindedIp, 0);
						} else {
							destination = new Socket(destIp, destPort,
									bindedIp, 0);
						}
					}
					source.setSoTimeout(72000000);
					destination.setSoTimeout(72000000);
					final StartIO io1 = new StartIO(source.getInputStream(),
							destination.getOutputStream(), destination
									.getInputStream(),
							source.getOutputStream(), source, destination,
							true, application, bindedIp);
					final StartIO io2 = new StartIO(destination
							.getInputStream(), source.getOutputStream(), source
							.getInputStream(), destination.getOutputStream(),
							destination, source, false, application, bindedIp);
					Thread src = new Thread() {
						public void run() {
							try {
								if (application != null
										&& application.equalsIgnoreCase("ftp")) {
									io1.initFtp();
								} else {
									io1.init();
								}
							} catch (Exception exe) {
								if (JPortForwarder.debug) {
									System.err.println("src()");
									exe.printStackTrace();
								}
							}
						}
					};
					Thread dst = new Thread() {
						public void run() {
							try {
								if (application != null
										&& application.equalsIgnoreCase("ftp")) {
									io2.initFtp();
								} else {
									io2.init();
								}
							} catch (Exception e) {
								if (JPortForwarder.debug) {
									System.err.println("dst()");
									e.printStackTrace();
								}
							}
						}
					};
					src.start();
					dst.start();
				} catch (Exception e) {
					System.err.println("error");
					e.printStackTrace();
				}
			}
		}

	}

	private class StartIO {
		private String application, returnIp, dataSocket;

		private InputStream is, fis;

		private OutputStream os, fos;

		private Socket socket, fSocket;

		private boolean display = false;

		private byte[] bt = new byte[4096];

		private int count = 0, returnPort, dataPort;

		private ServerSocket dataServerSocket;

		private InetAddress bindedIp;

		private Thread ftpThread;

		public StartIO(InputStream is, OutputStream os, InputStream fis,
				OutputStream fos, Socket socket, Socket fSocket,
				boolean display, String application, InetAddress bindedIp) {
			this.is = is;
			this.os = os;
			this.fis = fis;
			this.fos = fos;
			this.socket = socket;
			this.fSocket = fSocket;
			this.display = display;
			this.application = application;
			this.bindedIp = bindedIp;
		}

		public void init() throws Exception {
			try {
				while (true) {
					try {
						count = is.read(bt);
						if (count == -1) {
							close();
							break;
						}
					} catch (Exception e) {
						if (JPortForwarder.debug) {
							e.printStackTrace();
						}
					}
					if (count > 0) {
						os.write(bt, 0, count);
						os.flush();
					}
					if (socket != null && socket.isClosed()) {
						throw new IOException(
								"Terminating IOThread: socket closed.");
					}
				}
			} catch (Exception e) {
				close();
				if (JPortForwarder.debug) {
					e.printStackTrace();
				}
				throw new Exception("init() " + e.getMessage());
			}
		}

		public void initFtp() throws Exception {
			Vector vec = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			PrintWriter pw = new PrintWriter(os);
			String il = null;
			try {
				while (true) {
					try {
						il = br.readLine();
						if (application.equalsIgnoreCase("ftp")) {
							try {
								il = handleFTPPacket(il);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (il == null || il.startsWith("425")) {
							close();
							break;
						}
					} catch (Exception e) {
						if (JPortForwarder.debug) {
							e.printStackTrace();
						}
						close();
						break;
					}
					if (il != null) {
						pw.println(il);
						pw.flush();
					}
				}
			} catch (Exception e) {
				close();
				if (JPortForwarder.debug) {
					e.printStackTrace();
				}
				throw new Exception("init() " + e.getMessage());
			}
		}

		private String handleFTPPacket(String line) throws Exception {
			boolean portCommand = false, pasvCommand = false;
			String ret = null;

			if (line != null && line.startsWith("PORT")) {
				portCommand = true;
				if (debug) {
					System.out.print("PORT COMMAND HAS BEEN INTERCEPTED: ");
					System.out.println(line);
				}
			}

			if (line != null && line.startsWith("227")) {
				pasvCommand = true;
				if (debug) {
					System.out.print("PASV COMMAND HAS BEEN INTERCEPTED: ");
					System.out.println(line);
				}
			}

			if (portCommand || pasvCommand) {
				StringBuffer temp = null;
				String ipandport = null;
				if (portCommand) {
					ipandport = line.substring(5);
				} else {
					ipandport = line
							.substring(27, line.toString().length() - 1);
					if (ipandport.indexOf(')') > -1) {
						if (debug) {
							System.out.println("IPANDPORT CONTAINS )");
						}
						ipandport = ipandport.replace(')', ' ').trim();
					}
				}

				if (debug) {
					System.out.println("IPANDPORT: " + ipandport);
				}

				temp = new StringBuffer();
				int idx = ipandport.indexOf(",");
				temp.append(ipandport.substring(0, idx));
				temp.append(".");
				int idx1 = ipandport.indexOf(",", idx + 1);
				temp.append(ipandport.substring(idx + 1, idx1));
				temp.append(".");
				int idx2 = ipandport.indexOf(",", idx1 + 1);
				temp.append(ipandport.substring(idx1 + 1, idx2));
				temp.append(".");
				int idx3 = ipandport.indexOf(",", idx2 + 1);
				temp.append(ipandport.substring(idx2 + 1, idx3));

				returnIp = temp.toString();

				int idx4 = ipandport.indexOf(",", idx3 + 1);

				String binPort1 = Integer.toBinaryString(Integer
						.parseInt(ipandport.substring(idx3 + 1, idx4)));
				temp = new StringBuffer();
				for (int i = 0; i < 8 - binPort1.length(); i++) {
					temp.append("0");
				}
				temp.append(binPort1);
				binPort1 = temp.toString();

				ipandport = ipandport.trim();

				String binPort2 = Integer.toBinaryString(Integer
						.parseInt(ipandport.substring(idx4 + 1, ipandport
								.length())));
				temp = new StringBuffer();
				for (int i = 0; i < 8 - binPort2.length(); i++) {
					temp.append("0");
				}
				temp.append(binPort2);
				binPort2 = temp.toString();

				if (debug) {
					System.out
							.println("INT: "
									+ ipandport.substring(idx4 + 1, ipandport
											.length()));
					System.out.println("BINPORTS: " + binPort1 + " - "
							+ binPort2);
				}

				returnPort = Integer.parseInt(binPort1 + binPort2, 2);

				final ServerSocket ftpDataSocket;

				if (bindedIp == null) {
					ftpDataSocket = new ServerSocket(0);
				} else {
					ftpDataSocket = new ServerSocket(0, 0, bindedIp);
				}

				String ip;

				if (bindedIp == null) {
					ip = InetAddress.getLocalHost().getHostAddress().replace(
							'.', ',');
				} else {
					ip = bindedIp.getHostAddress().replace('.', ',');
				}

				String bin = Integer.toBinaryString(ftpDataSocket
						.getLocalPort());
				int port1 = Integer.parseInt(
						bin.substring(0, bin.length() - 8), 2);
				int port2 = Integer.parseInt(bin.substring(bin.length() - 8,
						bin.length()), 2);

				String portCmd;

				if (portCommand) {
					portCmd = "PORT " + ip + "," + port1 + "," + port2;
				} else {
					portCmd = "227 Entering Passive Mode (" + ip + "," + port1
							+ "," + port2 + ")";
				}

				if (debug) {
					System.out.println("COMMAND CHANGED TO: " + portCmd);

				}
				ret = portCmd;

				final boolean portCommandT = portCommand;

				ftpThread = new Thread() {
					private Socket dataSocket, returnData;

					private InputStream datais;

					private OutputStream dataos;

					private InputStream data2is;

					private OutputStream data2os;

					public void run() {
						try {

							dataSocket = ftpDataSocket.accept();

							if (debug) {
								System.out.print("Connecting to returnip "
										+ returnIp + " returnport "
										+ returnPort);

							}
							if (bindedIp != null) {
								returnData = new Socket(returnIp, returnPort,
										bindedIp, 0);
							} else {
								returnData = new Socket(returnIp, returnPort);
							}

							returnData.setSoTimeout(5000);
							dataSocket.setSoTimeout(5000);

							if (portCommandT) {
								datais = dataSocket.getInputStream();
								dataos = returnData.getOutputStream();
							} else {
								datais = returnData.getInputStream();
								dataos = dataSocket.getOutputStream();
							}

							if (portCommandT) {
								data2is = returnData.getInputStream();
								data2os = dataSocket.getOutputStream();
							} else {
								data2is = dataSocket.getInputStream();
								data2os = returnData.getOutputStream();
							}

							final Thread data1 = new Thread() {
								private byte[] data;

								private int cnt = 0;

								public void run() {
									try {
										cnt = 0;
										data = new byte[4096];
										while (true) {
											if (debug) {
												System.out
														.println("Reading from data1...");
											}
											try {
												cnt = datais.read(data);
											} catch (InterruptedIOException se) {
												if (debug) {
													System.out
															.println("Interrupted1 "
																	+ cnt);
												}
											}

											if (debug) {
												System.out.println("CNT: "
														+ cnt);
											}
											if (cnt != -1) {
												dataos.write(data, 0, cnt);
												dataos.flush();
											} else {
												break;
											}
										}
									} catch (Exception e) {
										if (debug) {
											e.printStackTrace();
										}
									} finally {
										try {
											if (debug) {
												System.out
														.println("Closing connections1");
											}
											if (datais != null) {
												datais.close();
											}
											if (data2is != null) {
												data2is.close();
											}
											if (dataos != null) {
												dataos.close();
											}
											if (data2os != null) {
												data2os.close();
											}
											if (ftpDataSocket != null) {
												ftpDataSocket.close();
											}
											if (dataSocket != null) {
												dataSocket.close();
											}
											if (returnData != null) {
												returnData.close();
											}
										} catch (Exception ee) {
											if (debug) {
												ee.printStackTrace();
											}
										}
									}
								}
							};
							Thread data2 = new Thread() {
								private byte[] data2;

								private int data2cnt = 0;

								public void run() {
									try {
										data2cnt = 0;
										data2 = new byte[4096];
										while (true) {
											try {
												data2cnt = data2is.read(data2);
											} catch (InterruptedIOException ie) {
												if (debug) {
													System.out
															.println("Interrupted2 "
																	+ data2cnt);
												}
											}
											if (debug) {
												System.out.println("DATA2CNT: "
														+ data2cnt);
											}
											if (data2cnt != -1) {
												data2os.write(data2, 0,
														data2cnt);
												data2os.flush();
											} else {
												break;
											}
										}
									} catch (Exception e) {
										if (debug) {
											e.printStackTrace();
										}
									} finally {
										try {
											if (debug) {
												System.out
														.println("Closing connections2");
											}
											if (debug) {
												System.out
														.println("Closing data2is");
											}
											if (data2is != null) {
												data2is.close();
											}
											if (debug) {
												System.out
														.println("Closing datais");
											}
											if (datais != null) {
												datais.close();
											}
											if (debug) {
												System.out
														.println("Closing data2os");
											}
											if (data2os != null) {
												data2os.close();
											}
											if (debug) {
												System.out
														.println("Closing dataos");
											}
											if (dataos != null) {
												dataos.close();
											}
											if (debug) {
												System.out
														.println("Closing ftpDataSocket");
											}
											if (ftpDataSocket != null) {
												ftpDataSocket.close();
											}
											if (debug) {
												System.out
														.println("Closing dataSocket");
											}
											if (dataSocket != null) {
												dataSocket.setSoTimeout(1);
											}
											if (debug) {
												System.out
														.println("Closing returnData");
											}
											if (returnData != null) {
												returnData.setSoTimeout(1);
											}
										} catch (Exception ee) {
											if (debug) {
												ee.printStackTrace();
											}
										}
									}
								}
							};

							data1.start();
							data2.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				ftpThread.start();
			} else {
				ret = line;
			}
			return ret;
		}

		public void close() {
			if (display) {
				System.out.println("Close host "
						+ socket.getInetAddress().getHostName() + "/"
						+ socket.getInetAddress().getHostAddress()
						+ " connected to port " + socket.getLocalPort()
						+ " going to "
						+ fSocket.getInetAddress().getHostAddress() + ":"
						+ fSocket.getPort());
			}
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if (os != null) {
					os.flush();
					os.close();
					os = null;
				}
				if (fis != null) {
					fis.close();
					fis = null;
				}
				if (fos != null) {
					fos.flush();
					fos.close();
					fos = null;
				}
				if (socket != null) {
					socket.close();
					socket = null;
				}
				if (fSocket != null) {
					fSocket.close();
					fSocket = null;
				}

			} catch (Exception ex) {
				System.err.println("Error closing connections");
				ex.printStackTrace();
			}
		}
	}

}