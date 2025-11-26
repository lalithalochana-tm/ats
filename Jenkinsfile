pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Playwright Browsers') {
            steps {
                bat 'mvn -Dplaywright.cli.install=true test -DskipTests'
            }
        }

        stage('Run Playwright TestNG Tests') {
            steps {
                bat 'mvn clean test -DsuiteXmlFile=cognitestSuite.xml'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'reports/**/*.*, traces/**/*.*, videos/**/*.*', fingerprint: true
        }
    }
}
