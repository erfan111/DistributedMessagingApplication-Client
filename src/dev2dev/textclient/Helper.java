package dev2dev.textclient;

import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import java.text.ParseException;
import java.text.StringCharacterIterator;

@SuppressWarnings("unused")
public class Helper {

    // ************************************************ Sip Uri Helper *************************************************

    public static String getAddressFromSipUri(String uri) {
        return uri.substring(uri.indexOf("@") + 1);
    }

    public static String getUserNameFromSipUri(String uri) {
        return uri.substring(uri.indexOf(":") + 1, uri.indexOf("@"));
    }

    public static String getPortFromSipUri(String uri) {
        return getPortFromAddress(getAddressFromSipUri(uri));
    }

    // *********************************************** Sip Header Helper ***********************************************

    public static ToHeader createToHeader(AddressFactory af, HeaderFactory hf, String to) throws ParseException {
        return createToHeader(af, hf, getUserNameFromSipUri(to), getAddressFromSipUri(to));
    }

    public static FromHeader createFromHeader(AddressFactory af, HeaderFactory hf, String from) throws ParseException {
        return createFromHeader(af, hf, getUserNameFromSipUri(from), getAddressFromSipUri(from));
    }

    public static ToHeader createToHeader(AddressFactory af, HeaderFactory hf, String username, String address)
            throws ParseException {
        Address toNameAddress = createSipAddress(af, username, address);
        ToHeader toHeader = hf.createToHeader(toNameAddress, null);
        return toHeader;
    }

    public static FromHeader createFromHeader(AddressFactory af, HeaderFactory hf, String username, String address)
            throws ParseException {
        Address fromNameAddress = createSipAddress(af, username, address);
        FromHeader fromHeader = hf.createFromHeader(fromNameAddress, "textclientv1.0");
        return fromHeader;
    }

    // ********************************************** Sip Address Helper ***********************************************

    public static Address createSipAddress(AddressFactory af, String username, String address) throws ParseException {
        SipURI Address = af.createSipURI(username, address);
        javax.sip.address.Address NameAddress = af.createAddress(Address);
        NameAddress.setDisplayName(username);
        return NameAddress;
    }

    // ********************************************** String Address Helper ***********************************************

    public static String getPortFromAddress(String address){
        return address.substring(address.indexOf(":") + 1);
    }

    public static String getIpFromAddress(String address){
        return address.substring(0, address.indexOf(":"));
    }

    // ****************************************************** End ******************************************************
}
