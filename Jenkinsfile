pipeline 
{
    agent any
    tools {
        jdk "OpenJDK-21"
        maven "M3"
    }
   
  environment {
        BRANCH_NAME = "${env.BRANCH_NAME}" // The current branch name
        TAG_NAME = "${env.GIT_TAG}" // The current branch name
    }
    stages 
    {
        stage('Workspace Cleaning') {
            steps {
                cleanWs()
                   }
        }
        
        stage('Process Branch') {
            steps {
                echo "Processing branch: ${env.BRANCH_NAME}"
            }
        }
    
stage('Clone Repository') {
            steps {
                //checkout scmGit(branches: [[name: '*/develop']], extensions: [], 
                checkout scmGit(branches: [[name: "*/${BRANCH_NAME}"]], extensions: [],
                //checkout scmGit(branches: [[name: '*/PASTRL-337']], extensions: [], 
                userRemoteConfigs: [[url: 'git@github.com:infocube-dev-team/irccs-common.git']])
            }
        }

                stage('Build package') {
                steps {
                    
		    //sh('mvn clean package -DskipTests -U')
			sh('mvn clean deploy -DaltDeploymentRepository=nexus::default::http://10.99.88.21:8081/irccs-common/')
            }
        }
         
        
        
        /*
        
        stage ('OWASP Dependency-Check Vulnerabilities') {
            steps {
                
                dependencyCheck additionalArguments: ''' 
                    -o "./" 
                    -s "/var/lib/jenkins/workspace/irccs-auth/target/quarkus-app/*.jar"
                    -f "ALL"
                    --disableYarnAudit
                    --prettyPrint
                    --nvdApiKey 572689c4-08aa-4b65-b489-c570c11b5efd
                    --nvdApiDelay=6000''', odcInstallation: 'OWASP-DC'

                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
                
            }
        }
        
               stage('SonarQube analysis') { 
    steps { sh('mvn clean verify sonar:sonar -DskipTests -Dsonar.projectKey=irccs-auth -Dsonar.host.url=http://10.99.88.146:9000 -Dsonar.login=sqa_0fa2c64ee059b0dc935d0431811c86d018610057')

       }
     }*/

                    /* stage('Upload to Nexus') {
                    
                    when {
                expression { params.ModuleUploadToNexus == "Yes" }
            }
            steps {
                            nexusArtifactUploader( 
                            credentialsId: 'nexus',
                            groupId: 'themes',
                            nexusUrl: '10.99.88.21:8081/',
                            nexusVersion: 'nexus3', protocol: 'http', repository:'irccs-common',
                            version: '$Branch',
                                artifacts: [


                                [artifactId: "theme",
                                file: "/var/jenkins_home/workspace/Build/portal/gesac/themes/gesac-theme/dist/gesac-theme.war",
                                type: "war"]
                            ]
                        );

                    } 
                
                    
                }
     */
        
           
        }
    }
