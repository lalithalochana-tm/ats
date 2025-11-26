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

        stage('Run ALL TestNG XML Suites') {
            steps {
                script {
                    // Run all suite XML files automatically
                    def suites = findFiles(glob: '*.xml')
                    for (suite in suites) {
                        if (suite.name.contains("cognitest")) {
                            echo "Running Suite: ${suite.name}"
                            bat "mvn test -DsuiteXmlFile=${suite.name}"
                        }
                    }
                }
            }
        }

        stage('Run All Java Tests Under src/test/java') {
            steps {
                bat 'mvn -Dtest=* test'
            }
        }
    }

    post {
        always {
            // Publish TestNG XML results
            junit 'target/surefire-reports/*.xml'

            // Archive test artifacts
            archiveArtifacts artifacts: 'reports/**/*.*, traces/**/*.*, videos/**/*.*', fingerprint: true
        }
    }
}
