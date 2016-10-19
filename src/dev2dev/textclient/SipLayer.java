package dev2dev.textclient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class SipLayer implements SipListener {

    // *********************************************** Private variable ************************************************

    private MessageProcessor messageProcessor;

    private String username;

    private SipFactory sipFactory;
    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;

    private SipProvider sipProvider;
    private SipStack sipStack;

    // ************************************************* Constructors **************************************************

    public SipLayer(String username, String ip, int port) throws Exception {
        setUsername(username);
        initSip(ip, port);
    }

    // ************************************************ Helper methods *************************************************

    private SipStack createSipStack(String ip) throws PeerUnavailableException {

        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "TextClient");
        properties.setProperty("javax.sip.IP_ADDRESS", ip);
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "textclient.txt");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "textclientdebug.log");

        return sipFactory.createSipStack(properties);
    }

    private void initSip(String ip, int port) throws Exception {

        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        sipStack = createSipStack(ip);
        headerFactory = sipFactory.createHeaderFactory();
        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();

        ListeningPoint tcp = sipStack.createListeningPoint(port, "tcp");
        ListeningPoint udp = sipStack.createListeningPoint(port, "udp");

        sipProvider = sipStack.createSipProvider(tcp);
        sipProvider.addSipListener(this);
        sipProvider = sipStack.createSipProvider(udp);
        sipProvider.addSipListener(this);
    }

    // ************************************************ Message methods ************************************************

    /**
     * This method uses the SIP stack to send a message.
     */
    public void sendMessage(String to, String message) throws ParseException,
            InvalidArgumentException, SipException {


        SipURI requestURI = addressFactory.createSipURI(getUsername(), Helper.getAddressFromSipUri(to));
        requestURI.setTransportParam("udp");

        FromHeader fromHeader = Helper.createFromHeader(addressFactory, headerFactory, getUsername(), getAddress());

        ToHeader toHeader = Helper.createToHeader(addressFactory, headerFactory, to);

        ArrayList viaHeaders = new ArrayList();

        viaHeaders.add(getSelfViaHeader());

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1,
                Request.MESSAGE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");

        Request request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards,contentTypeHeader,
                message);

        request.addHeader(getSelfContactHeader());

        sipProvider.sendRequest(request);
    }

    public void createResponseForReceivedMessage(Request req, int response_status_code) {
        Response response ;
        try {
            response = messageFactory.createResponse(response_status_code, req);
            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
            toHeader.setTag("888"); //This is mandatory as per the spec.
            ServerTransaction st = sipProvider.getNewServerTransaction(req);
            st.sendResponse(response);
        } catch (Throwable e) {
            e.printStackTrace();
            messageProcessor.processError("Can't send OK reply.");
        }
    }

    // ********************************************* SipListener Interface *********************************************

    /**
     * This method is called by the SIP stack when a response arrives.
     */
    public void processResponse(ResponseEvent evt) {
        //when send message we get response of send Request
        Response response = evt.getResponse();
        int status = response.getStatusCode();

//		switch (status){
//			case 100:
//				messageProcessor.processInfo("--TRYING");
//				break;
//			case 180:
//				messageProcessor.processInfo("--RINGING");
//				break;
//			default:
//				messageProcessor.processInfo(String.valueOf(status));
//		}

        if ((status >= 200) && (status < 300)) { //Success!
            messageProcessor.processInfo("--Sent");
            System.out.println("Message Recieved " + status);
            return;
        }

	    messageProcessor.processError("Previous message not sent: " + status);
    }

    /**
     * This method is called by the SIP stack when a new request arrives.
     */
    public void processRequest(RequestEvent evt) {
        //when anyone sends message to me

        Request req = evt.getRequest();

        String method = req.getMethod();
        if (!method.equals("MESSAGE")) { //bad request type.
            messageProcessor.processError("Bad request type: " + method);
            return;
        }

        FromHeader from = (FromHeader) req.getHeader(FromHeader.NAME);
        messageProcessor.processMessage(from.getAddress().toString(),
                new String(req.getRawContent()));

        createResponseForReceivedMessage(req, 200);
    }

    /**
     * This method is called by the SIP stack when there's no answer
     * to a message. Note that this is treated differently from an error
     * message.
     */
    public void processTimeout(TimeoutEvent evt) {
        messageProcessor
                .processError("Previous message not sent: " + "timeout");
    }

    /**
     * This method is called by the SIP stack when there's an asynchronous
     * message transmission error.
     */
    public void processIOException(IOExceptionEvent evt) {
        messageProcessor.processError("Previous message not sent: "
                + "I/O Exception");
    }

    /**
     * This method is called by the SIP stack when a dialog (session) ends.
     */
    public void processDialogTerminated(DialogTerminatedEvent evt) {
    }

    /**
     * This method is called by the SIP stack when a transaction ends.
     */
    public void processTransactionTerminated(TransactionTerminatedEvent evt) {
    }


    // ************************************************ Get/Set methods ************************************************

    public String getAddress(){
        return getHost() + ":" + getPort();
    }

    private ContactHeader getSelfContactHeader() throws ParseException {
        Address contactAddress = Helper.createSipAddress(addressFactory, getUsername(), getAddress());
        return headerFactory.createContactHeader(contactAddress);
    }

    private ViaHeader getSelfViaHeader() throws ParseException, InvalidArgumentException {
        ViaHeader viaHeader = headerFactory.createViaHeader(getHost(), getPort(), "udp", "branch1");
        return viaHeader;
    }

    public String getHost() {
        String host = sipStack.getIPAddress();
        return host;
    }

    public int getPort() {
        int port = sipProvider.getListeningPoint().getPort();
        return port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public void setMessageProcessor(MessageProcessor newMessageProcessor) {
        messageProcessor = newMessageProcessor;
    }

    // ***************************************************** End *******************************************************

}
