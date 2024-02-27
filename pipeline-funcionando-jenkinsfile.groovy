pipeline {
  agent any

  environment {
    GIT_URL = 'https://github.com/PriscilaPinto/juice-devsecops.git'
  }

  stages {
    stage('Checkout do GitHub') {
      steps {
        git branch: 'master', url: env.GIT_URL
      }
    }

    stage('SAST utilizando Horusec Scan') {
      steps {
        script {
          try {
            sh 'horusec start -p="./" --disable-docker=true -e=true > /home/pri/relatorio/horusec_report_1.txt 2>&1 || true'

            // Gerar relatório HTML
            //sh 'horusec o /home/pri/relatorio/horusec_report.text'
          } catch (Exception e) {
            echo "Erro durante a execução do Horusec Scan: ${e.message}"
          }
        }
      }
    }

    stage('SCA utilizando Dependecy-check') {
      steps {
        script {
          sh 'mvn org.owasp:dependency-check-maven:check'
        }
      }
    }

    //stage('Build e Run Docker Container do Juice-Shop') {
        //steps {
            //script {
                // Construir a imagem Docker da aplicação Juice Shop
                // 'docker build -t juice-shop .'
                    
                // Executar o contêiner Docker
                //sh 'docker run -d -p 10503:3000 juice-shop'
            //}
        //}
    //}
    stage('OWASP ZAP Scan') {
        steps {
            script {
                def tempFile = "/home/pri/relatorio/zap_relatorio_temp.html"
                def targetFile = "/home/pri/relatorio/zap_relatorio.html"
            
                // Verificar se o relatório já existe na pasta de destino
                if (!fileExists(targetFile)) {
                    sh "/home/pri/Downloads/ZAP_2.14.0/zap.sh -daemon -port 10500 -config api.disableproxy=true -quickurl http://localhost:3000/ -quickout ${tempFile} -quickprogress &"
                    sleep(time: 5, unit: 'MINUTES')
                    sh "if [ -f ${tempFile} ]; then mv ${tempFile} ${targetFile}; fi"
                    sh 'pkill -f ZAP_2.14.0/zap.sh || true'
                } else {
                    sh "/home/pri/Downloads/ZAP_2.14.0/zap.sh -daemon -port 10500 -config api.disableproxy=true -quickurl http://localhost:3000/ -quickout ${tempFile} -quickprogress &"
                    sleep(time: 5, unit: 'MINUTES')
                    sh "mv ${tempFile} /home/pri/relatorio/zap_relatorio_new.html"
                    sh 'pkill -f ZAP_2.14.0/zap.sh || true'
                }
            }
        }
    }
    
    //stage('OWASP ZAP Scan') {
        //steps {
            //script {
                // Iniciar o processo do OWASP ZAP em segundo plano
                //sh '/home/pri/Downloads/ZAP_2.14.0/zap.sh -daemon -port 10500 -config api.disableproxy=true -quickurl http://localhost:3000/ -quickout /home/pri/relatorio/zap_relatorio_8.html -quickprogress &'
                
                // Aguardar um tempo suficiente para que o OWASP ZAP execute os testes (ajuste conforme necessário)
                //sleep(time: 10, unit: 'MINUTES')
                
                // Encerrar explicitamente o processo do OWASP ZAP
                //sh 'pkill -f ZAP_2.14.0/zap.sh || true'
            //}
        //}
    //}


    //stage('Testando se o serviço web está funcionando') {
      //steps {
        //script {
          //def previousStages = currentBuild.rawBuild.getBuildCauses().collect { it.getShortDescription() }
          //if (!previousStages.contains("Houve uma falha")) {
            //currentBuild.result = 'Sucesso'
          //}
        //}
      //}
    //}
  }
}