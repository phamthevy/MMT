package com.example.thevy.mmt1;

import android.location.Location;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClientThread extends Thread{
        Location YOU=new Location("1.0,1.0");
        Location finish=new Location("1.0,1.0");

        String vitri;
        String address;
        double distance;

        public TcpClientThread(String Address,String loc) {
            super();
            vitri = loc;
            address=Address;
        }

        @Override
        public void run() {


            try {
                String sentence = vitri;
                String modifiedSentence;

                //Create client socket, connect to server
                Socket clientSocket = new Socket(address, 9999);

                //Create output stream attached to socket
                DataOutputStream outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());

                //Create input stream attached to socket
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //Send to server
                 outToServer.writeBytes(sentence+'\n');

                //Read line from server
                modifiedSentence = inFromServer.readLine();
                MapsActivity.getStatus("1");
                MapsActivity.getDataT(modifiedSentence);


                String[] target = modifiedSentence.split("_");
                //marker2.visible(true);
                finish.setLatitude(Double.parseDouble(target[1]));
                finish.setLongitude(Double.parseDouble(target[0]));

                modifiedSentence = inFromServer.readLine();

                MapsActivity.getDataO(modifiedSentence);



                while(true){
                    modifiedSentence=inFromServer.readLine();
                    if (modifiedSentence.equals("You lose")) break;else MapsActivity.getDataO(modifiedSentence);

                    YOU.setLatitude(MapsActivity.latitude);
                    YOU.setLongitude(MapsActivity.longitude);
                    distance = YOU.distanceTo(finish);
                    if(distance<=500000){
                        outToServer.writeBytes("DONE"+'\n');
                        MapsActivity.getStatus("2");
                        outToServer.close();
                        clientSocket.close();
                        return;
                    }
                }
                MapsActivity.getStatus("3");
                outToServer.writeBytes("Received+'\n'");

                outToServer.close();
                clientSocket.close();
            }
            catch (UnknownHostException e) {
                System.err.println("Not found");
            }
            catch (IOException e) {
                System.err.println("Can't connect");
            }

        }

}
