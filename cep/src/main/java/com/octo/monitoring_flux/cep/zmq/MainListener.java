package com.octo.monitoring_flux.cep.zmq;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class MainListener {
	
	public static void main(String[] args) throws InterruptedException {
		  ZContext zContext = new ZContext(1);
		  ZMQ.Socket zContextSocket = zContext.createSocket(ZMQ.PULL);
	      zContextSocket.setLinger(0);
	      zContextSocket.connect("tcp://127.0.0.1:2201");
	      
	      //  Process tasks forever
	       while (!Thread.currentThread ().isInterrupted ()) {
	    	   byte[] data = zContextSocket.recv(0);
	    	   System.out.println("DATA:" + data);
	    	   if (data != null) {
	    		   String string = new String(data);
	               System.out.print(string + '.');
	    	   }
	           //  Do the work
	           Thread.sleep(200);
	       }
	}

}
