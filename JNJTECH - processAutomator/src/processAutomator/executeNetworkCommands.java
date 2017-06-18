package processAutomator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class executeNetworkCommands {

	// components for getting the TraceRoute
	static int data;
	static Process traceProc;
	static BufferedInputStream traceInutStream;
	static String traceOutput;
	static String tracePacketLossLocation;

	// components for the execute command method
	static BufferedReader commandOutputReader;
	static StringBuffer comandOutputBuffer;
	static String line;

	// main method components
	static final String adminPassword = "";
	static Process dnsProcess;
	static BufferedReader dnsReader;
	static Frame traceOutputFrame;
	static BufferedWriter dnsWriter;
	static String output;
	static List t;

	// main method
	public static void main(String[] args) {

		// initializing components for DNS flush
		dnsProcess = null;
		dnsReader = null;
		dnsWriter = null;

		// initializing frame for the output of Traceroute
		traceOutputFrame = new Frame();
		t = new List();
		traceOutputFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				traceOutputFrame.dispose();

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

		});
		traceOutputFrame.add(t);

		try {
			// flushing the dnsWriter
			dnsProcess = Runtime.getRuntime().exec("sudo killall -HUP mDNSResponder");

			dnsWriter = new BufferedWriter(new OutputStreamWriter(dnsProcess.getOutputStream()));
			dnsWriter.write(adminPassword);
			dnsWriter.flush();

			dnsWriter = new BufferedWriter(new OutputStreamWriter(dnsProcess.getOutputStream()));

			dnsReader = new BufferedReader(new InputStreamReader(dnsProcess.getInputStream()));

			line = "";
			while ((line = dnsReader.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Sending and printing the TraceRoute
		getTraceRoute();

		// turning the network adapter off(cycle start)
		output = executeCommand("networksetup -setairportpower airport off");

		// waiting 3 seconds before turning the network adapter on
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// turning the network adapter on(cycle stop)
		output = executeCommand("networksetup -setairportpower airport on");

		// deploying the result of the TraceRoute
		traceOutputFrame.setSize(600, 600);
		traceOutputFrame.setVisible(true);

	}

	// to get the trace
	public static synchronized void getTraceRoute() {

		try {
			// executing the command
			traceProc = Runtime.getRuntime().exec("traceroute 8.8.8.8");

			// to output the password
			traceInutStream = new BufferedInputStream(traceProc.getInputStream());

			// to get the result of traceInutStream
			data = 0;

			// initializing tracePacketLossLocation
			tracePacketLossLocation = "";

			// reading the result of the the TraceRoute
			while ((data = traceInutStream.read()) > 0) {

				traceOutput += (char) data;

				// troubleshooting the location of packet loss
				if (data == '*') {
					if (traceOutput.contains("1"))
						tracePacketLossLocation = "PACKET LOSS AT MODEM";
					if (traceOutput.contains("2"))
						tracePacketLossLocation = "PACKET LOSS AT YOUR ISP";
					if (traceOutput.contains("3"))
						tracePacketLossLocation = "PACKET LOSS AT EXTERNAL SERVER";

				}

			}
			if (tracePacketLossLocation.equals(""))
				tracePacketLossLocation = "No Packet Loss";

			System.out.println(tracePacketLossLocation);
			t.add("\n" + tracePacketLossLocation);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// method to execute a command and return the OSs response
	private static String executeCommand(String command) {

		// to get the result
		comandOutputBuffer = new StringBuffer();

		try {

			// execute the command
			dnsProcess = Runtime.getRuntime().exec(command);

			// wait for it to finish
			dnsProcess.waitFor();

			// get the reader of the result
			commandOutputReader = new BufferedReader(new InputStreamReader(dnsProcess.getInputStream()));

			// read the result
			line = "";
			while ((line = commandOutputReader.readLine()) != null) {
				comandOutputBuffer.append(line + "\n");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return comandOutputBuffer.toString();

	}
}