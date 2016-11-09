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

@SuppressWarnings("deprecation")
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
    private boolean isRegistered;

    // ************************************************* Constructors **************************************************

    public SipLayer(String username, String ip, int port) throws Exception {
        setUsername(username);
        isRegistered = false;
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

    void CallregisterRequest(String serverAddress) throws ParseException,
            InvalidArgumentException, SipException {
        SipURI requestURI = addressFactory.createSipURI(getUsername(), serverAddress);
        requestURI.setTransportParam("udp");

        FromHeader fromHeader = Helper.createFromHeader(addressFactory, headerFactory, getUsername(), getAddress());
        ToHeader toHeader = Helper.createToHeader(addressFactory, headerFactory, getUsername(), getAddress());

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        viaHeaders.add(getSelfViaHeader());

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1,
                Request.REGISTER);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);


        Request request = messageFactory.createRequest(requestURI,
                Request.REGISTER, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);

        request.addHeader(getSelfContactHeader());

        request.addHeader(headerFactory.createHeader(ServerConfig.RegisterHeader, ServerConfig.ClientRegister));

        sipProvider.sendRequest(request);
    }

    /**
     * This method uses the SIP stack to send a message.
     */
    void sendMessage(String to, String message) throws ParseException,
            InvalidArgumentException, SipException {


        SipURI requestURI = addressFactory.createSipURI(getUsername(), Helper.getAddressFromSipUri(to));
        requestURI.setTransportParam("udp");

        FromHeader fromHeader = Helper.createFromHeader(addressFactory, headerFactory, getUsername(), getAddress());

        System.out.println("sendMessage:to:" + to);
        ToHeader toHeader = Helper.createToHeader(addressFactory, headerFactory, to);

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();

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

    private void createResponseForReceivedMessage(RequestEvent req, int response_status_code) {
        Response response ;
        try {
            response = messageFactory.createResponse(response_status_code, req.getRequest());
            SipProvider p = (SipProvider) req.getSource();
            p.sendResponse(response);

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


        if ((status >= 200) && (status < 300)) {
            CSeqHeader ch = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
            if (ch.getMethod().equals(Request.REGISTER)){
                setIsRegistered(true);
                messageProcessor.processInfo("REGISTER");
                System.out.println("REGISTER is ok -> code: " + status);
            }else{
                messageProcessor.processInfo("--Sent");
                System.out.println("Message Recieved " + status);
            }
            return;
        }

	    messageProcessor.processError("Previous request not sent: " + status);
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
        messageProcessor.processMessage(from.getAddress().getDisplayName(),
                new String(req.getRawContent()));

        createResponseForReceivedMessage(evt, 200);
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

    private String getAddress(){
        return getHost() + ":" + getPort();
    }

    private ContactHeader getSelfContactHeader() throws ParseException {
        Address contactAddress = Helper.createSipAddress(addressFactory, getUsername(), getAddress());
        return headerFactory.createContactHeader(contactAddress);
    }

    private ViaHeader getSelfViaHeader() throws ParseException, InvalidArgumentException {
        return headerFactory.createViaHeader(getHost(), getPort(), "udp", "branch1");
    }

    String getHost() {
        return sipStack.getIPAddress();
    }

    int getPort() {
        return sipProvider.getListeningPoint().getPort();
    }

    String getUsername() {
        return username;
    }

    boolean getIsRegistered(){
        return isRegistered;
    }

    private void setIsRegistered(boolean isRegistered){
        this.isRegistered = isRegistered;
        if (isRegistered){
            messageProcessor.processClientRegistered();
        }
    }

    private void setUsername(String newUsername) {
        username = newUsername;
    }

    void setMessageProcessor(MessageProcessor newMessageProcessor) {
        messageProcessor = newMessageProcessor;
    }

    // ***************************************************** End *******************************************************

}
