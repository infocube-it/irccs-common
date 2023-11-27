# irccs-common

### How to set you project

Add in *pom.xml*
```
     <distributionManagement>
        <snapshotRepository>
            <id>nexusdeploymentrepo</id>
            <url>http://nexus.infocube.it:8089/repository/irccs-common/</url>
        </snapshotRepository>
    </distributionManagement>
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

replace **SECRET** with nexus credentials