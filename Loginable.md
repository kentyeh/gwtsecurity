# Introduction #
[GwtSecurity](http://code.google.com/p/gwtsecurity/) reports Spring Security Error to your GWT RPC function,
You then start a login process and send remote procedure call again when login succeed.

More and more your application growing up and there is to much boring code to write the same thing.
Here is to show you how to avoid those annoying jobs.

# Details #
Originally, your remote service method throws [GwtSecurityException](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/exceptions/GwtSecurityException.html) to acknowledge that server may occur a security exception.
```
    public interface RemoteCall extends RemoteService {
        RtnType request(ParamType param) throws GwtSecurityException;
    }
```
and you have a asynchronous pair interface.
```
    public interface RemoteCallAsync{
        void request(ParamType parameter, AsyncCallback<RtnType> callback);
    }
```
You instantiate a RemoteCallAsync to proceed remote call.
```
   RemoteCallAsync rc = (RemoteCallAsync) GWT.create(RemoteCall.class);  
```
You invoke function call and everything controll by yourself when exception happen.
```
   rc.request(param,new AsyncCallback<RtnType>(){
       @Override
       public void onFailure(Throwable caught) {
          //You had prepare a Login interface,
          //re-issue request again when user login succeed,
          //or consider how to handle user cancel login process.
       }
       @Override
       public void onSuccess(RtnType result) {
       }
   });
```

I found it seem not so easy to implement.
So from 1.0.3, I add a [loginable](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/package-summary.html) module.

Now, your asynchronous interface need to inherit from [LoginableAsync](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/LoginableAsync.html)
```
    public interface RemoteCallAsync extends LoginableAsync{
        void request(ParamType parameter, AsyncCallback<RtnType> callback);
    }
```

Then prepare a [loginBox](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/AbstractLoginBox.html) that implements [HasLoginHandler](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/HasLoginHandler.html)
for two functionality.
  1. Start to login process when invoke on [startLogin](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/HasLoginHandler.html#startLogin(java.lang.Throwable))
  1. Whenever user login succeed or user cancel login must fire a [LoginEvent](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/LoginEvent.html).

Finally, Let GwtSecurity create a proxy wrapper for your asynchronous interface.
```
    RemoteCallAsync proxy = GWT.create(RemoteCallAsync.class);
```

In fact, proxy is a [LoginableService](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/LoginableService.html) object.
It need to be assigned a RemoteService Object and a [loginBox](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/AbstractLoginBox.html).
```
    LoginableService<RemoteCallAsync> loginService = (LoginableService<RemoteCallAsync>) proxy;
    //Create a Remote Service
    RemoteCallAsync remoteService = (RemoteCallAsync) GWT.create(RemoteCall.class);
    //Assign Remote Service
    loginService.setRemoteService(remoteService);
    //Assign login box
    loginService.setHasLoginHandler(loginbox);
```

Now you can using proxy object like RemoteCallAsync before but no worry about security exception.
When security exception occur, proxy will initiate the login procedure.
If login succeed, request will issue again,
or you need to handle a [LoginCancelException](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/LoginCancelException.html) if user cancel login.

Now we have a unusual issue: How about a series of discrete requests?
```
    public void doDiscreteRequests(){
        asyncProxy.request1(param... ,new AsyncCallBack<?> callback(){
           ....
        });
        asyncProxy.request2(param... ,new AsyncCallBack<?> callback(){
           ....
        });
    }
```
We know that,both two requests are asynchronous and independent even one happen exception does not influence another.
So it is possible to invoke startLogin() twice.

My suggest is:

make LoginBox singleton and can be invoke stargLogin() twice to show the same dialog.

Because proxy obeject register event handler to LoginBox.
When login completed, both requests will send again
or both request need to handle [LoginCancelException](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/LoginCancelException.html).