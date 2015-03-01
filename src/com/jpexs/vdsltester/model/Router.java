package com.jpexs.vdsltester.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.InputStream;
import java.io.OutputStream;

import com.jpexs.vdsltester.controller.Arbiter;

/**
 * Class for communication with router
 * @author JPEXS
 */
public abstract class Router {

    private String adress = "10.0.0.138";
    private int port = 23;
    private Socket sock;
    protected boolean connected = false;
    private InputStream is;
    private OutputStream os;
    private boolean debugMode = true;
    protected String connectionUserName = "admin";
    protected String connectionPassword = "admin";
    public String name;
    protected boolean loggedIn=false;
    protected String fakeFile=null;
    
    public static final int TELNET_IAC=255;
    public static final int TELNET_WILL=251;
    public static final int TELNET_WONT=252;
    public static final int TELNET_DO=253;
    public static final int TELNET_DONT=254;
        
    public static final int TELNET_OPTION_ECHO=1;
    public static final int TELNET_OPTION_SUPPRESS_GO_AHEAD=3;
    public static final int TELNET_OPTION_TERMINAL_TYPE=24;
    
    private boolean finishing=false;
    
    public void setFakeFile(String file){
       this.fakeFile=file;
    }

    
    protected synchronized boolean isFinishing()
    {
       return finishing;
    }
    
    protected synchronized void setFinishing()
    {
       this.finishing=true;
    }
    
    protected synchronized void checkNotFinishing(){
       if(isFinishing())
       {
          throw new FinishException();
       }
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public void setConnectionUserName(String connectionUserName) {
        this.connectionUserName = connectionUserName;
    }

    public Router(String name) {
       this.name=name;
    }

   @Override
   public String toString() {
      return name;
   }



    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getAdress() {
        return adress;
    }

    public boolean isConnected() {
        return connected||(fakeFile!=null);
    }

    public int getPort() {
        return port;
    }

    public int getRouterHeaderLength()
    {
       return -1;
    }

    public void setAddressAndPort(String adress, int port) {
        this.adress = adress;
        this.port = port;
        if (connected) {
            disconnect();
        }
    }

    /**
     * Initializes connection to the router
     * @return True on success
     */
    public boolean connect() throws IOException {
        if (connected||(fakeFile!=null)) {
            return true;
        }
        Arbiter.inform("connectingStart");
        sock = new Socket(InetAddress.getByName(adress), port);
        is = sock.getInputStream();
        os = sock.getOutputStream();
        connected = true;
        sock.setSoTimeout(100);
        Arbiter.inform("connectingFinish");
        return true;
    }

    /**
     * Log on the router
     */
    public void login() throws IOException {
        if(loggedIn||(fakeFile!=null)){
           return;
        }

        connect();
        Arbiter.inform("loggingInStart");
        sock.setSoTimeout(2000);        
        Arbiter.inform("loggingIn1");
        String line = readAndStopAfterChar(':');
        Arbiter.inform("loggingIn2");
        readByte(); //space        
        Arbiter.inform("loggingIn3");
        if ((line.toLowerCase().indexOf("login") > -1) || (line.toLowerCase().indexOf("user") > -1)) {
            sendLine(connectionUserName);
            readLine();
            line = readAndStopAfterChar(':');
            readByte(); //space            
            readLine();
        }
        if (line.toLowerCase().indexOf("password") > -1) {
            sendLine(connectionPassword);
                  readLines();
                  loggedIn=true;            
                  Arbiter.inform("loggingInFinish");
            return;
        }
        sock.setSoTimeout(com.jpexs.vdsltester.model.Main.socketTimeout);
        loggedIn=true;
        Arbiter.inform("loggingInFinish");
    }

    /**
     * Reads one byte from the router
     * @return One byte or -1 on error
     */
    public int readByte() throws IOException {
        try {
            if (!connect()) {
                return -1;
            }
            int i=is.read();
            if(i==TELNET_IAC){
                 receivedIAC();
                 i=readByte();
            }
            return i;
        } catch (SocketTimeoutException ex) {
        }
        return 0;
    }

    /**
     * Reads one byte from the router
     * @return One byte or -1 on error
     */
    public int readByteDontWait() throws IOException {
        if (!connect()) {
            return -1;
        }
        int i=is.read();
        if(i==TELNET_IAC){
                 receivedIAC();
                 i=readByteDontWait();
        }
        return i;
        
    }

    
    protected void sendTelnetDo(int option) throws IOException{
       sendTelnetCommand(TELNET_DO,new byte[]{(byte)option});
       ProgramLog.print("<SENT TELNET COMMAND DO "+option+">");
    }
    
    protected void sendTelnetWill(int option) throws IOException{
       sendTelnetCommand(TELNET_WILL,new byte[]{(byte)option});
       ProgramLog.print("<SENT TELNET COMMAND WILL "+option+">");
    }
    
    protected void sendTelnetDont(int option) throws IOException{
       sendTelnetCommand(TELNET_DONT,new byte[]{(byte)option});
       ProgramLog.print("<SENT TELNET COMMAND DONT "+option+">");
    }
    
    protected void sendTelnetWont(int option) throws IOException{       
       sendTelnetCommand(TELNET_WONT,new byte[]{(byte)option});
       ProgramLog.print("<SENT TELNET COMMAND WONT "+option+">");
    }
    
    protected void sendTelnetCommand(int command,byte params[]) throws IOException{
       os.write(TELNET_IAC);
       os.write(command);
       if(params!=null){
          os.write(params);
       }       
    }
    
    /**
     * IAC Telnet code received
     */
    protected void receivedIAC() throws IOException{
       int command=is.read();
       int option=-1;
       switch(command){
          case TELNET_WILL:
          case TELNET_DO:
          case TELNET_DONT:
          case TELNET_WONT:
             option=is.read();
             break;
          default:
                ProgramLog.print("<UNKNOWN TELNET COMMAND "+command+">");
                return;
       }
       
       switch(command){
          case TELNET_WILL:                 
               ProgramLog.print("<TELNET COMMAND WILL "+option+">");
             switch(option){
                case TELNET_OPTION_ECHO:
                   sendTelnetDo(TELNET_OPTION_ECHO);
                   break;
                case TELNET_OPTION_SUPPRESS_GO_AHEAD:
                   sendTelnetDo(TELNET_OPTION_SUPPRESS_GO_AHEAD);
                   break;
                case TELNET_OPTION_TERMINAL_TYPE:
                   sendTelnetDont(TELNET_OPTION_TERMINAL_TYPE);
                   break;
                default:
                   sendTelnetDont(option);
                   break;
             }
             break;
          case TELNET_DO:
             ProgramLog.print("<TELNET COMMAND DO "+option+">");
             sendTelnetWont(option);
             break;
          case TELNET_DONT:
             ProgramLog.print("<TELNET COMMAND DONT "+option+">");
             break;
          case TELNET_WONT:
             ProgramLog.print("<TELNET COMMAND WONT "+option+">");
             break;
       }
       
    }
    
    /**
     * Reads one line from router
     * @return Line which was read
     */
    public String readLine() throws IOException {
        //ProgramLog.println("readLine start");
        if (!connect()) {
            return "";
        }
        try {
            boolean end = false;
            int i = 0;
            int prev = 0;
            String line = "";
            if (debugMode) {
               ProgramLog.startIncoming();
                //ProgramLog.print("<\"");
            }
            do {
                i = is.read();
                if(i==TELNET_IAC){
                   receivedIAC();
                   continue;
                }
                //ProgramLog.println("readline:"+i);
                if ((i != '\n') && (i != '\r')) {
                    //ProgramLog.println("A");
                    line += (char) i;
                    //ProgramLog.println("B");
                }
                if (debugMode) {
                    ProgramLog.print("" + (char) i);
                }
                if ((i == '\n') && (prev == '\r')) {
                    if (debugMode) {
                        //ProgramLog.println("\"");
                       //ProgramLog.println();
                       //ProgramLog.print("\n");
                    }
                    //ProgramLog.println("C");
                    return line;
                }
                if (checkRouterHeader(line)) {
                    if (debugMode) {
                        ProgramLog.print(((char) i)+"");                        
                        //"\""
                    }
                   // ProgramLog.println("D");
                    return null;
                }
                prev = i;
                if (i == -1) {
                    //ProgramLog.println("E");
                    break;
                }                
            } while (!end);
        } catch (SocketTimeoutException ex) {
        }
        if (debugMode) {
            //ProgramLog.println("\"");
        }
        return null;
    }

    /**
     * Send one command (line) to the router
     * @param line Command to send
     * @return True on success
     */
    public boolean sendLine(String line) throws IOException {
        if (debugMode) {
            //ProgramLog.println(">\"" + line + "\"");
           ProgramLog.startOutcoming();
           ProgramLog.println(line);
        }
        if (!connect()) {
            return false;
        }
        os.write((line + "\r\n").getBytes());
        return true;
    }

    /**
     * Reads from server and stops reading after str is read
     * @param str String to stop after
     * @return True when found
     */
    public boolean readAndStopAfterString(String str) throws IOException {
        if (!connect()) {
            return false;
        }
        boolean end = false;
        int i = 0;
        int prev = 0;
        String line = "";
        do {
            i = is.read();
            if(i==TELNET_IAC){
                   receivedIAC();
                   continue;
            }
            if ((i != '\n') && (i != '\r')) {
                line += (char) i;
            }
            if ((i == '\n') && (prev == '\r')) {
                if (debugMode) {
                    ProgramLog.println("Router header not found");
                }
                return false;
            }
            if (line.equals(str)) {
                if (debugMode) {
                    ProgramLog.println("Router header found");
                }
                return true;
            }
            prev = i;
        } while (!end);

        return false;
    }

    /**
     * Reads from server and stops reading after c is read
     * @param c Char to stop after
     * @return String before c
     */
    public String readAndStopAfterChar(char c) throws IOException {
        if (!connect()) {
            return "";
        }
        String line = "";
        try {
            boolean end = false;
            int i = 0;
            int prev = 0;
            do {
                i = is.read();
                if(i==-1){
                   throw new NoAnswerException();
                }
                if(i==TELNET_IAC){
                   receivedIAC();
                   continue;
                }
                //if ((i != '\n') && (i != '\r')) {
                    line += (char) i;
                //}
                /*if((i=='\n')&&(prev=='\r')){
                if(debugMode)
                ProgramLog.println("Read till char - EOL:"+line);
                return line;
                }*/
                if (i == c) {
                    if (debugMode) {
                       ProgramLog.startIncoming();
                        ProgramLog.print(line);
                    }
                    return line;
                }
                prev = i;
            } while (!end);
        } catch (SocketTimeoutException ex) {
        }
        return line;
    }

    /**
     * Disconnects router
     */
    public void disconnect() {
        forceDisconnect();
    }
    
    public void forceDisconnect(){
        loggedIn = false;
        connected = false;
        try {
            is.close();            
        } catch (Exception ex) {
        }
        try {
            os.close();
        } catch (Exception ex) {
        }
        try {
            sock.close();
        } catch (Exception ex) {
        }
    }

    /**
     * Sends request to the router and returns answer
     * @param command Command to send
     * @return Array of returned lines
     */
    public ArrayList<String> sendRequest(String command) throws IOException {
        if(fakeFile!=null)
        {
           return sendFakeRequest(command, fakeFile);
        }
        sendLine(command);
        readLine();
        ArrayList<String> ret = readLines();
        if (ret == null) {
            throw new NoAnswerException();
        }
        return ret;
    }
    
    /**
     * Sends measuring request to the router and returns answer
     * @param command Command to send
     * @return Array of returned lines
     */
    public ArrayList<String> sendMeasureRequest(String command) throws IOException {
       checkNotFinishing();
       return sendRequest(command);
    }

    /**
     * Sends command to the router
     * @param command Command to send
     */
    public void sendCommand(String command) throws IOException {
        if(fakeFile!=null)
        {
           return;
        }
        sendLine(command);
        readLine();
        readLines();
    }
    
    /**
     * Sends measure command to the router
     * @param command Command to send
     */
    public void sendMeasureCommand(String command) throws IOException {
       checkNotFinishing();
       sendCommand(command);
    }

    public abstract boolean checkRouterHeader(String header);

    public ArrayList<String> readLines() throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            boolean end = false;
            int i = 0;
            int prev = 0;
            String line = "";
            if (debugMode) {
                 ProgramLog.startIncoming();
                //ProgramLog.print("<\"");
            }
            do {
                i = is.read();
                if(i==TELNET_IAC){
                   receivedIAC();
                   continue;
                }
                if ((i != '\n') && (i != '\r')) {
                    line += (char) i;
                }
                if (debugMode) {
                    ProgramLog.print("" + (char) i);
                }
                if ((i == '\n') && (prev == '\r')) {
                    if (debugMode) {
                        //ProgramLog.println("\"");
                        //ProgramLog.print("\n");
                    }
                    ret.add(line);
                    line = "";
                }
                if (checkRouterHeader(line)) {
                    /*if (debugMode) {
                        ProgramLog.print(((char) i) + ""); //\""
                    }*/
                    return ret;
                }
                prev = i;
                if (i == -1) {
                    return null;
                }
            } while (!end);
        } catch (SocketTimeoutException ex) {
        }
        if (debugMode) {
            //ProgramLog.println("\"");
        }
        return null;
    }

    public RouterMeasurement doMeasure() throws IOException {
       return doMeasure(null);
    }

    public abstract RouterMeasurement doMeasure(HashSet<String> needs) throws IOException;

    public ArrayList<String> sendFakeRequest(String command,String file) throws IOException {        
        ArrayList<String> ret=new ArrayList<String>();
        BufferedReader br=new BufferedReader(new FileReader(file));
        String s;
        boolean buffer=false;
        int routerHeaderLength=getRouterHeaderLength();
        loopread:while((s=br.readLine())!=null){
           boolean isheader=false;
           if(routerHeaderLength==-1)
           {
                 for(int i=0;i<s.length();i++){
                    if(checkRouterHeader(s.substring(0,i+1))){
                       isheader=true;
                       if(!buffer){
                          if(command.equals(s.substring(i+1))){
                              buffer=true;
                              continue loopread;
                          }
                       }

                    }
                 }
           }else{
              if(s.length()>=routerHeaderLength)
              {
                 if(checkRouterHeader(s.substring(0,routerHeaderLength)))
                 {
                       isheader=true;
                       if(!buffer){
                          if(command.equals(s.substring(routerHeaderLength))){
                              buffer=true;
                              continue loopread;
                          }
                       }
                 }
              }
           }
           if(buffer&&(!isheader)){
              ret.add(s);
           }
           if(buffer&&(isheader)){
              buffer=false;
           }
        }
        return ret;
   }    
}
