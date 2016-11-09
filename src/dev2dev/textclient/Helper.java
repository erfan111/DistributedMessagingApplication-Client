package dev2dev.textclient;

import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import java.text.ParseException;

@SuppressWarnings("unused")
class Helper {

    public static String getHeaderValue(Header header){
        return header.toString().trim().split(" ")[1];
    }

    // ************************************************ Sip Uri Helper *************************************************

    static String getAddressFromSipUri(String uri) {
        return uri.substring(uri.indexOf("@") + 1);
    }

    private static String getUserNameFromSipUri(String uri) {
        return uri.substring(uri.indexOf(":") + 1, uri.indexOf("@"));
    }

    public static String getPortFromSipUri(String uri) {
        return getPortFromAddress(getAddressFromSipUri(uri));
    }

    // *********************************************** Sip Header Helper ***********************************************

    static ToHeader createToHeader(AddressFactory af, HeaderFactory hf, String to) throws ParseException {
        return createToHeader(af, hf, getUserNameFromSipUri(to), getAddressFromSipUri(to));
    }

    public static FromHeader createFromHeader(AddressFactory af, HeaderFactory hf, String from) throws ParseException {
        return createFromHeader(af, hf, getUserNameFromSipUri(from), getAddressFromSipUri(from));
    }

    static ToHeader createToHeader(AddressFactory af, HeaderFactory hf, String username, String address)
            throws ParseException {
        Address toNameAddress = createSipAddress(af, username, address);
        return hf.createToHeader(toNameAddress, null);
    }

    static FromHeader createFromHeader(AddressFactory af, HeaderFactory hf, String username, String address)
            throws ParseException {
        Address fromNameAddress = createSipAddress(af, username, address);
        return hf.createFromHeader(fromNameAddress, "textclientv1.0");
    }

    // ********************************************** Sip MyAddress Helper ***********************************************

    static Address createSipAddress(AddressFactory af, String username, String address) throws ParseException {
        SipURI Address = af.createSipURI(username, address);
        javax.sip.address.Address NameAddress = af.createAddress(Address);
        NameAddress.setDisplayName(username);
        return NameAddress;
    }

    // ********************************************** String MyAddress Helper ***********************************************

    public static String getPortFromAddress(String address){
        return address.substring(address.indexOf(":") + 1);
    }

    public static String getIpFromAddress(String address){
        return address.substring(0, address.indexOf(":"));
    }

    // ****************************************************** End ******************************************************
}
