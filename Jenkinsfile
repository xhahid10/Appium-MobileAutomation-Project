pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.6'
        jdk 'JDK 11'
    }
    
    parameters {
        choice(name: 'TEST_SUITE', choices: ['android-single', 'deposit-money-test', 'withdrawal-tests', 'hamburger-menu-navigation', 'multi-device-sequential'], description: 'Select test suite to run')
        string(name: 'BUILD_NAME', defaultValue: '', description: 'LambdaTest build name (leave empty for auto-generated)')
        string(name: 'SUITE_XML_FILE', defaultValue: '', description: 'Custom TestNG suite file (optional)')
    }
    
    environment {
        LT_USERNAME = credentials('LAMBDATEST_USERNAME')
        LT_ACCESS_KEY = credentials('LAMBDATEST_ACCESS_KEY')
        LT_APP_ID = credentials('LAMBDATEST_APP_ID')
        JAVA_HOME = tool 'JDK11'
        ANDROID_HOME = '/Users/mohammedshahid/Library/Android/sdk'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Clean Workspace') {
            steps {
                sh 'mvn clean'
            }
        }
        
        stage('Setup Build Name') {
            steps {
                script {
                    // Generate unique build name if not provided
                    if (params.BUILD_NAME == null || params.BUILD_NAME.trim() == '') {
                        def timestamp = new Date().format("yyyyMMdd_HHmmss")
                        def suiteName = params.TEST_SUITE.replace('-', '_').replace('_', ' ').toUpperCase()
                        env.BUILD_NAME = "${suiteName}_${timestamp}"
                    } else {
                        env.BUILD_NAME = params.BUILD_NAME
                    }
                    
                    // Determine suite XML file
                    if (params.SUITE_XML_FILE != null && params.SUITE_XML_FILE.trim() != '') {
                        env.SUITE_XML_FILE = params.SUITE_XML_FILE
                    } else {
                        env.SUITE_XML_FILE = "src/test/java/org/RunFiles/${params.TEST_SUITE}.xml"
                    }
                    
                    echo "Using Build Name: ${env.BUILD_NAME}"
                    echo "Using Suite File: ${env.SUITE_XML_FILE}"
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    try {
                        sh """
                            mvn test \
                            -DsuiteXmlFile="${env.SUITE_XML_FILE}" \
                            -Dwebdriver.http.factory=jdk-http-client \
                            -DLT_USERNAME=${LT_USERNAME} \
                            -DLT_ACCESS_KEY=${LT_ACCESS_KEY} \
                            -DLT_APP_ID=${LT_APP_ID} \
                            -DBUILD_NAME="${env.BUILD_NAME}" \
                            -Dextent.reporter.spark.out=test-output/ExtentReport.html \
                            -Dextent.reporter.spark.documentTitle="PFG Automation Report" \
                            -Dextent.reporter.spark.reportName="PFG Test Results"
                        """
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Test execution failed: ${e.message}")
                    }
                }
            }
        }
        
        stage('Archive Reports') {
            steps {
                // Archive all test output files
                archiveArtifacts artifacts: 'test-output/**/*', fingerprint: true
                
                // Create a simple HTML report index
                sh '''
                    echo "<html><body>" > test-output/index.html
                    echo "<h1>Test Reports</h1>" >> test-output/index.html
                    echo "<p><strong>Build Name:</strong> ${env.BUILD_NAME}</p>" >> test-output/index.html
                    echo "<p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>" >> test-output/index.html
                    echo "<p><strong>Suite File:</strong> ${env.SUITE_XML_FILE}</p>" >> test-output/index.html
                    echo "<ul>" >> test-output/index.html
                    echo "<li><a href='ExtentReport.html'>Extent Report</a></li>" >> test-output/index.html
                    echo "<li><a href='screenshots/'>Screenshots</a></li>" >> test-output/index.html
                    echo "</ul>" >> test-output/index.html
                    echo "</body></html>" >> test-output/index.html
                '''
                
                // Archive the index file
                archiveArtifacts artifacts: 'test-output/index.html', fingerprint: true
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
            
            // Send email notification
            emailext (
                subject: "Pipeline ${currentBuild.result}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <p>Build Status: ${currentBuild.result}</p>
                    <p>Build Number: ${env.BUILD_NUMBER}</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>
                    <p>Test Suite: ${params.TEST_SUITE}</p>
                    <p>Build Name: ${env.BUILD_NAME}</p>
                    <p>Suite File: ${env.SUITE_XML_FILE}</p>
                """,
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: 'your.email@example.com'
            )
        }
        
        success {
            echo 'Tests executed successfully!'
        }
        
        failure {
            echo 'Test execution failed!'
        }
    }
} 