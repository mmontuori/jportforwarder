JPortForwarder Project

JPortForwarder is a multithreaded TCP Port Forwarder application.

The following feature have been developed:
	- Generic TCP Port Forwarging.  Listen for a port and forward to an ip:port.
	- Multihoming TCP Port Forwarding.  Listen for an ip:port and forward to an ip:port.
	- FTP Application gateway, capable of forwarding FTP traffic in PASV and regular mode.
	- SSL Support, capable of binding SSL on every feature specified above.
	- Flexible configuration file to allow multiple redirection to run on a single VM.

In order to use the SSL feature you need to have the JSSE distribution available from java.sun.com
in the CLASSPATH, then generate a certificate file to attach to the process with a password.
See usage from program for syntax.

