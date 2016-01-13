# Introduction #
[Spring Security ](http://static.springsource.org/spring-security/site/) always require a login entry when security exception occur,
In Gwt, you have two choices to handle security exception,
  1. provide a stand alone login page,
  1. or handle by gwt itself.

Gwtsecurity detect handle exception affair(using AOP) when security exception occurs, <br />
gwtsecury distinguish whether request offer by RemoteService or not.<br />
If true, gwtsecurity direct report [GwtSecurityException](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/exceptions/GwtSecurityException.html) to front end gwt app,
<br />otherwise gwtsecurity bypass to spring's mechanism.


# Details #
First,add aop context into your configuration and add following config.<br />
(Activate proxy-target-class to get a fast speed.)
```
    <aop:aspectj-autoproxy proxy-target-class="true"/>
```
then add a [GwtExceptionTranslator](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/GwtExceptionTranslator.html) bean to handle Security Exception.
```
    <beans:bean class="com.gwt.ss.GwtExceptionTranslator"/>
```

Gwtsecurity propose two type [GwtSecurityException](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/exceptions/GwtSecurityException.html).
| [GwtAuthenticationException](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/exceptions/GwtAuthenticationException.html) | means user not yet authenticated.<br>, gwt app has duty to provide a login entry for user sign-in. <br>
<tr><td> <a href='http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/exceptions/GwtAccessDeniedException.html'>GwtAccessDeniedException</a> </td><td> means user has not enough authority to access then resource.<br />user should get a warning this time. </td></tr></tbody></table>

The demo app, I intent to handle security exception by gwt app itself(I also provide a stand aloe login page).<br>
<table><thead><th> Gwt RPC login when user not yet authenticated</th></thead><tbody>
<tr><td> <img src='http://gwtsecurity.googlecode.com/svn/resources/gssdemoSimpleLogin.png' /> </td></tr>
<tr><td> or redirect to spring login entry(jsp page)  </td></tr>
<tr><td> <img src='http://gwtsecurity.googlecode.com/svn/resources/login_jsp.png' /> </td></tr></tbody></table>


I configure spring security config as follows:<br />
First,I config spring's login entry url to handle non-gwt login event.<br>
<pre><code>   &lt;form-login login-page="/login.jsp" authentication-failure-url="/login.jsp?authfailed=true"/&gt;<br>
</code></pre>
Next, I construct a bean to handle gwt login event:<br>
<pre><code>   &lt;beans:bean class="com.gwt.ss.GwtUsernamePasswordAuthority"&gt;<br>
       &lt;beans:property name="authenticationManager" ref="authenticationManager"/&gt;<br>
   &lt;/beans:bean&gt;<br>
</code></pre>
Finally,I need a RemoteService to process gwt login.<br>
<pre><code>    GwtLoginAsync loginService = GwtLoginAsync.Util.getInstance(getLoginUrl());<br>
    loginService.j_gwt_security_check(userName, password, new AsyncCallback&lt;Void&gt;() {<br>
        @Override<br>
        public void onFailure(Throwable caught) {<br>
           //fail notify <br>
        }<br>
        @Override                          <br>
        public void onSuccess(Void result) {<br>
           //success notify<br>
        }<br>
    }<br>
</code></pre>
In the same way,logout can process from gwt app.<br />
(config success-handler-ref instead of logout-success-url instead here.)<br>
<pre><code>    &lt;logout invalidate-session="true" success-handler-ref="logoutSuccessHandler" logout-url="/j_spring_security_logout"/&gt;<br>
    &lt;beans:bean id="logoutSuccessHandler" class="com.gwt.ss.GwtLogoutSuccessHandler" p:logoutSuccessUrl="/"/&gt;<br>
</code></pre>
Then<br>
<pre><code>    GwtLogoutAsync logoutService = GwtLogoutAsync.Util.getInstance(getLogoutUrl());<br>
    logoutService.j_gwt_security_logout(new AsyncCallback&lt;Void&gt;() {<br>
        @Override<br>
        public void onFailure(Throwable caught) {<br>
            //fail notify<br>
        }                <br>
        @Override<br>
        public void onSuccess(Void result) {<br>
           //success notify<br>
        }<br>
    }<br>
</code></pre>
Alternative,logout also can process from url "<a href='http://context_path/j_spring_security_logout'>http://context_path/j_spring_security_logout</a>"<br>
<br>
Sometimes, you may enabled "session-management"<br>
<pre><code>    &lt;session-management invalid-session-url="/login.jsp?sessionInvalid=true"&gt;<br>
        &lt;concurrency-control max-sessions="1" expired-url="/login.jsp?sessionExpired=true"/&gt;<br>
    &lt;/session-management&gt;<br>
</code></pre>
Then your need to construct a <a href='http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/GwtSessionManagement.html'>GwtSessionManagement</a> bean<br>
to throw a <a href='http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/client/exceptions/GwtAccessDeniedException.html'>GwtAccessDeniedException</a> when session is invalid.<br>
<br /><span>(Notice: previous proxy-target-class must be set to "false")</span>
<pre><code>    &lt;beans:bean class="com.gwt.ss.GwtSessionManagement"/&gt;<br>
</code></pre>