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
                // This installs Playwright browsers required for tests
                bat 'mvn -Dplaywright.cli.install=true test -DskipTests'
            }
        }

        stage('Run Playwright TestNG Tests') {
            steps {
                // Runs pluto.xml (your TestNG suite), same as IntelliJ
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            // Publish Surefire TestNG reports
            junit 'target/surefire-reports/*.xml'

            // Archive Playwright reports, videos, traces
            archiveArtifacts artifacts: 'reports/**/*.*, traces/**/*.*, videos/**/*.*', fingerprint: true
        }
    }
}
