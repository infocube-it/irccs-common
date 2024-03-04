package org.quarkus.irccs.authentication.exceptions;


import org.quarkus.irccs.authentication.enums.AuthCodes;


public class IrccsExceptions extends  RuntimeException{
    private AuthCodes type;

    public IrccsExceptions(String msg) {
        super(msg);
    }

    public IrccsExceptions(AuthCodes authCodes) {
        super(authCodes.label);
        this.type = authCodes;
    }
    public IrccsExceptions(String msg, AuthCodes authCodes) {
        super(msg);
        this.type = authCodes;
    }


}