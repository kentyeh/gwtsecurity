# gwtsecurity
Automatically exported from code.google.com/p/gwtsecurity

# developers
kent.yeh2000, [steven.jardine](https://github.com/sjardine)

GWTSecurity is a library that allows a GWT application to utilize the security features provided by [Spring Security](http://projects.spring.io/spring-security/).

Starting with version 1.3.2, GWTSecurity is using jdk1.7, gwt2.7.0 and spring-security 3.2.0+. Previous versions of spring-security and gwt may not work as expected. 

#Maven User
Add repository and dependency to your pom.xml 

    <dependencies>
        <dependency>
            <groupId>com.google.code.gwtsecurity</groupId>
            <artifactId>gwtsecurity-core</artifactId>
            <version>1.3.3</version>
        </dependency>
    </dependencies>
or

    <dependencies>
        <dependency>
            <groupId>com.google.code.gwtsecurity</groupId>
            <artifactId>gwtsecurity-requestfactory</artifactId>
            <version>1.3.3</version>
        </dependency>
    </dependencies>

#Basic Usage
add

    <inherits name="com.gwt.ss.GwtSecurity"/>
or

    <inherits name="com.gwt.ss.requestfactory.GwtSecurityWithRequestFactory" />

into your projct.gwt.xml,and method in RemoteService? must throws GwtSecurityException? to receive security notification 

    public interface GreetingService extends RemoteService {
        String greetServer(String name) throws GwtSecurityException;
    }
##config web.xml

 1. assign spring context location`

       &nbsp;&nbsp;&nbsp;&nbsp;&lt;context-param&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;contextConfigLocation&lt;/param-name&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;classpath:applicationContext.xml&lt;/param-value&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/context-param&gt;
 2. add spring security filter 

    &nbsp;&nbsp;&nbsp;&nbsp;&lt;filter&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-name&gt;springSecurityFilterChain&lt;/filter-name&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-class&gt;org.springframework.web.filter.DelegatingFilterProxy&lt;/filter-class&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/filter&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-mapping&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;filter-name&gt;springSecurityFilterChain&lt;/filter-name&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/filter-mapping&gt;

 3. add associate listeners 
 

    &nbsp;&nbsp;&nbsp;&nbsp;&lt;listener&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;listener-class&gt;org.springframework.web.context.ContextLoaderListener&lt;/listener-class&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/listener&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;listener&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;listener-class&gt;org.springframework.security.web.session.HttpSessionEventPublisher&lt;/listener-class&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/listener&gt;

 4. setup your servlet configuration 

#config spring seccurity context file

 1. include aop naming space 

    &nbsp;&nbsp;&nbsp;&nbsp;&lt;beans:beans&nbsp;...
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xmlns:aop=&quot;http://www.springframework.org/schema/aop&quot;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xsi:schemaLocation=&quot;...
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;http://www.springframework.org/schema/aop&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;http://www.springframework.org/schema/aop/spring-aop.xsd&quot;

 2. Enabling @AspectJ Support 

    &lt;aop:aspectj-autoproxy/&gt;

 3. create gwt spring security bean 

    &lt;beans:bean class="com.gwt.ss.GwtExceptionTranslator"/&gt;
    

 4. config other security setting 

#Request Factory Configuration
Initialize your [RequestFactory](http://www.gwtproject.org/doc/latest/DevGuideRequestFactory.html) implementations with a LoginableRequestTransport instance. 

#Test / Demo Module
We are currently working on a test module for gwtsecurity that will provide comprehensive integration testing.

We are in the very basic stages but a simple test is available.

The easiest way to run the test is using maven: 

    

GWTSecurity is a library that allows a GWT application to utilize the security features provided by Spring Security.

Starting with version 1.3.2, GWTSecurity is using jdk1.7, gwt2.7.0 and spring-security 3.2.0+. Previous versions of spring-security and gwt may not work as expected.
Maven User

Add repository and dependency to your pom.xml

   <dependencies>
       <dependency>
           <groupId>com.google.code.gwtsecurity</groupId>
           <artifactId>gwtsecurity-core</artifactId>
           <version>1.3.3</version>
       </dependency>
   </dependencies>

or

   <dependencies>
       <dependency>
           <groupId>com.google.code.gwtsecurity</groupId>
           <artifactId>gwtsecurity-requestfactory</artifactId>
           <version>1.3.3</version>
       </dependency>
   </dependencies>

Basic Usage
add

   <inherits name="com.gwt.ss.GwtSecurity"/>

or

   <inherits name="com.gwt.ss.requestfactory.GwtSecurityWithRequestFactory" />

into your projct.gwt.xml,and method in RemoteService? must throws GwtSecurityException? to receive security notification

   public interface GreetingService extends RemoteService {

       String greetServer(String name) throws GwtSecurityException;

   }

config web.xml

    assign spring context location

         <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:applicationContext.xml</param-value>
        </context-param>

    add spring security filter

         <filter>
            <filter-name>springSecurityFilterChain</filter-name>
            <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>springSecurityFilterChain</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>

    add associate listeners

         <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>
        <listener>
            <listener-class>org.springframework.security.web.session.HttpSessionEventPublisher</listener-class>
        </listener>

    setup your servlet configuration 

config spring seccurity context file

    include aop naming space

         <beans:beans ...
             xmlns:aop="http://www.springframework.org/schema/aop"
             xsi:schemaLocation="...
               http://www.springframework.org/schema/aop 
               http://www.springframework.org/schema/aop/spring-aop.xsd"

    Enabling @AspectJ Support

         <aop:aspectj-autoproxy/>

    create gwt spring security bean

         <beans:bean class="com.gwt.ss.GwtExceptionTranslator"/>

    config other security setting 

Request Factory Configuration

Initialize your RequestFactory implementations with a LoginableRequestTransport instance.
Test / Demo Module

We are currently working on a test module for gwtsecurity that will provide comprehensive integration testing.

We are in the very basic stages but a simple test is available.

The easiest way to run the test is using maven:

    mvn clean package verify
