

GWTSecurity is a library that allows a GWT application to utilize the security features provided by [Spring Security](http://static.springsource.org/spring-security/site).

Starting with version 1.3.2, GWTSecurity is using jdk1.7, gwt2.7.0 and spring-security 3.2.0+.  Previous versions of spring-security and gwt may not work as expected.

# Maven User #
Add repository and dependency to your pom.xml
```
   <dependencies>
       <dependency>
           <groupId>com.google.code.gwtsecurity</groupId>
           <artifactId>gwtsecurity-core</artifactId>
           <version>1.3.3</version>
       </dependency>
   </dependencies>
```
or
```
   <dependencies>
       <dependency>
           <groupId>com.google.code.gwtsecurity</groupId>
           <artifactId>gwtsecurity-requestfactory</artifactId>
           <version>1.3.3</version>
       </dependency>
   </dependencies>
```
# Basic Usage #
## add ##
```
   <inherits name="com.gwt.ss.GwtSecurity"/>
```
or
```
   <inherits name="com.gwt.ss.requestfactory.GwtSecurityWithRequestFactory" />
```
into your projct.gwt.xml,and method in RemoteService must throws GwtSecurityException to receive security notification
```
   public interface GreetingService extends RemoteService {

       String greetServer(String name) throws GwtSecurityException;

   }
```
## config web.xml ##
  1. assign spring context location
```
     <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
    </context-param>
```
  1. add spring security filter
```
     <filter>
        <filter-name>springSecurityFilterChain</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>springSecurityFilterChain</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```
  1. add associate listeners
```
     <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <listener>
        <listener-class>org.springframework.security.web.session.HttpSessionEventPublisher</listener-class>
    </listener>
```
  1. setup your servlet configuration
## config spring seccurity context file ##
  1. include aop naming space
```
     <beans:beans ...
         xmlns:aop="http://www.springframework.org/schema/aop"
         xsi:schemaLocation="...
           http://www.springframework.org/schema/aop 
           http://www.springframework.org/schema/aop/spring-aop.xsd"
```
  1. Enabling @AspectJ Support
```
     <aop:aspectj-autoproxy/>
```
  1. create gwt spring security bean
```
     <beans:bean class="com.gwt.ss.GwtExceptionTranslator"/>
```
  1. config other security setting
## Request Factory Configuration ##
Initialize your [RequestFactory](https://developers.google.com/web-toolkit/doc/latest/DevGuideRequestFactory) implementations with a [LoginableRequestTransport](http://gwtsecurity.googlecode.com/svn/javadoc/latest/com/gwt/ss/requestfactory/client/loginable/LoginableRequestTransport.html) instance.


## Test / Demo Module ##
We are currently working on a test module for gwtsecurity that will provide comprehensive integration testing.

We are in the very basic stages but a simple test is available.

The easiest way to run the test is using maven:

```
mvn clean package verify
```