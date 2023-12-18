# irccs-common

### How to set you project

Add in *pom.xml*
```
<repositories>
    <repository>
      <id>nexusdeploymentrepo</id>
      <name>i3 Repository</name>
      <url>https://nexus.infocube.it:444/repository/irccs-common/</url>
      <layout>default</layout>
    </repository>
  </repositories>
```

Edit you maven setting added:

```
  <servers>
  ...
    <server>
      <id>nexusdeploymentrepo</id>
      <username>SECRET</username>
      <password>SECRET</password>
    </server>
    ....
  </servers>
  
  <profiles>
  ...
	<profile>
     <id>snapshot</id>
     <repositories>
       <repository>
         <id>nexus-snapshot-repo</id>
         <name>irccs-common</name>
         <url>http://nexus.infocube.it:8089/repository/irccs-common/</url>
       </repository>
     </repositories>
   </profile>
   ....
  </profiles>  
```

### How to run quarkus to local

This env-Setup build using this configuration:
```
    Apache Maven 3.9.4
    Java version: 19.0.2
```


replace **SECRET** with nexus credentials

## How to update version at the project

Run `mvn versions:set -DnewVersion=<version>`
example: `mvn versions:set -DnewVersion=1.0.1-SNAPSHOT`

## How to configure i3-Annotations and i3-client in your micro-services:

Add in you **application.properties**
```
quarkus.index-dependency.i3-annotations.group-id=org.quarkus.irccs
quarkus.index-dependency.i3-annotations.artifact-id=i3-annotations

quarkus.index-dependency.i3-client.group-id=org.quarkus.irccs
quarkus.index-dependency.i3-client.artifact-id=i3-client
```

and in your `pom.xml`

in **dependencies**:
```
    <dependency>
      <groupId>org.quarkus.irccs</groupId>
      <artifactId>i3-client</artifactId>
      <version>${i3.common.version}</version>
    </dependency>

    <dependency>
      <groupId>org.quarkus.irccs</groupId>
      <artifactId>i3-annotations</artifactId>
      <version>${i3.common.version}</version>
    </dependency>
```

in **properties**:

```
    <i3.common.version>[CURRENT VERSION OF COMMON]</i3.common.version>
```
