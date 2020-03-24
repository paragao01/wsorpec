package br.wsorpec.log;

import java.util.Calendar;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

import java.text.SimpleDateFormat;

public class Log {
	private FileWriter writer;
	private boolean debug;
	private String logPrefix;
	
	public Log(boolean dbg, String prefix) {
		try {
			Date agora = new Date();
			debug = dbg;
			logPrefix = prefix;
			SimpleDateFormat amd = new SimpleDateFormat("'" + logPrefix	+ "'yyyyMMdd");
			String logName = amd.format(agora);			
			writer = new FileWriter(logName, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registro(String protocolo, String rcvsnd, String msg) {
		String registro = "";
		try {			
			// create a java calendar instance
			Calendar calendar = Calendar.getInstance();
			
			// get a java.util.Date from the calendar instance.
			// this date will represent the current instant, or "now".
			java.util.Date now = calendar.getTime();
			
			// a java current time (now) instance
			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

			// Date agora = new Date();
			// String logTime = hms.format(agora);
			// String registro = currentTimestamp + " " + logTime + " " + msg +
			// "\n";
			
			//if(msg==null){msg="";}
			
			registro = Util.formataCampo(GlobalConstants.IPREMOTO, 15)+" "+Util.formataCampo(currentTimestamp.toString(), 23) + " " + Util.formataCampo(protocolo, 24) + " " + rcvsnd	+ " "+msg.length()+" [" + msg + "]\n";			
			writer.write(registro);
			writer.flush();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (debug)
			System.out.print(registro);
	}

	public void close() throws Exception {
		writer.close();
	}
	
}
