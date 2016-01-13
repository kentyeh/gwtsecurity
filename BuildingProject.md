# Introduction #

This project is developed under [Maven](http://maven.apache.org/).
So you need to setup maven first.

All prebuilt jars for download are available here:
| [gwtsecurity-core-1.3.2.jar](http://search.maven.org/remotecontent?filepath=com/google/code/gwtsecurity/gwtsecurity-requestfactory/1.3.2/gwtsecurity-requestfactory-1.3.2.jar) | Core Library |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------|
| [gwtsecurity-requestfactory-1.3.2.jar](http://search.maven.org/remotecontent?filepath=com/google/code/gwtsecurity/gwtsecurity-core/1.3.2/gwtsecurity-core-1.3.2.jar)           | Request Factory Library |

# Details #

You can download this code using [Subversion](http://subversion.tigris.org/).  Run the command:
```
svn co https://gwtsecurity.googlecode.com/svn/tags/gwtsecurity-1.3.2 
```

Then, run the command:
```
cd gwtsecurity-1.3.2
mvn install
```
to install library to your local repository.

You can download the demo application using [Subversion](http://subversion.tigris.org/).  Run the command:
```
svn co https://gwtsecurity.googlecode.com/svn/tags/gwt4ssdemo-1.3.0
```

Then, run the command:
```
cd gwt4ssdemo-1.3.0
mvn package
```