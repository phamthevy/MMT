package com.example.thevy.mmt1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UdpClientThread extends Thread{

    String dstAddress;
    int dstPort;
    String data;

    public UdpClientThread(String addr, int port, String data_send) {
        super();
        dstAddress = addr;
        dstPort = port;
        data = data_send;
    }

    @Override
    public void run() {

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(dstAddress);
            // send request
            byte[] buf = new byte[256];
            buf = data.getBytes();
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length, address, dstPort);
            socket.send(packet);
            socket.close();


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
