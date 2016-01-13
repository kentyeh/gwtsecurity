# Introduction #
Sometimes, we can't put it all into a single page when authentication need.
Like OpenId, it is page oriented(even though you put it into frame,
some provider like Google will send a x-frame-options header to prevent),
go through multiple pages,
and final navigate to destination after pass validation.


# Details #
About using opendId, it can be reference to
[Spring Security Sample](http://git.springsource.org/spring-security/spring-security/trees/master/samples/openid).

[This Sample code](http://code.google.com/p/gwtsecurity/source/browse/#svn%2Ftrunk%2Fgwt4ssdemo%2FgssDemo4)
was rewrite from [demo2 example](http://code.google.com/p/gwtsecurity/source/browse/#svn%2Ftrunk%2Fgwt4ssdemo%2FgssDemo2).

From Spring Sample, which introduce the [openid selector](http://code.google.com/p/openid-selector/) project,
I put them all under openid folder and config it to be filtered none by Spring Security.

In previous example, I use a dialog log-in box that implements [HasLoginHandler](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/loginable/HasLoginHandler.html)
to provide the login functionality.

Nothing special, with page oriented mode,we just need to open a secured page,
and spring security will take over every thing.

First,I create a [OpenIdSelector](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/java/com/gwt/ss/demo4/client/OpenIdSelector.java) that implements HasLoginHandler,
It is a simple panel that mask all the client screen, open a secured entry page and waiting for a LoginEvent trigger.

You should notice that OpenIdSelector using some JSNI invocation within registerJSMethod(),which add a window prototype function.
It's a javascript function to provide html page to fire a Login event.

Because we dont't know how spring security processing.
So I add a anchor link on this mask to provide a opportunity for user cancel the login process.

We need a secured document that match any secured path,so I add a common controller(see [applicationContext-mvc.xml](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/resources/applicationContext-mvc.xml)),
```
<mvc:view-controller path="/**/gwt.openid.verify" view-name="verify"/>
```
to result in [verify.jsp](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/webapp/WEB-INF/jsp/verify.jsp) page when url is endding with "gwt.openid.verify".

Then we look at [GwtStaffServiceAsync.java](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/java/com/gwt/ss/demo4/client/GwtStaffServiceAsync.java),
whenever a login required(RPC to "/gwtsl/staff"),then LoginHandler to open "/gwtsl/staff/gwt.openid.verify" page.

Because path "/gwtsl/staff" is secured. So open "/gwtsl/staff/gwt.openid.verify" lead Spring Security start login process.

And what's the verify.jsp major job?

The major job of verify.jsp is to invoke loginResult to fire a login event to make GWT Ajax re-issue a RPC again.


One thing I have to mention. This sample has method level secure.

My suggestion is to avoid using method level secure.
You can look at [GreetingService Implements](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/java/com/gwt/ss/demo4/server/GwtGreetingServiceImpl.java),
For the sake of method secure, I had to open "/gwtsl/admin/gwt.openid.verify" (see [GwtGreetingServiceAsync.java](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/java/com/gwt/ss/demo4/client/GwtGreetingServiceAsync.java)).
and config same secured path in [configuration](http://code.google.com/p/gwtsecurity/source/browse/trunk/gwt4ssdemo/gssDemo4/src/main/resources/applicationContext-security.xml).

I must say, It's not a good design