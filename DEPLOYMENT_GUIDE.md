# Guia de Deploy - Webservices SPC

## Índice
- [1. Pré-requisitos](#1-pré-requisitos)
- [2. Instalação Passo a Passo](#2-instalação-passo-a-passo)
- [3. Deploy no Tomcat](#3-deploy-no-tomcat)
- [4. Dicas para Produção](#4-dicas-para-produção)
- [5. Links Úteis](#5-links-úteis)

---

## 1. Pré-requisitos
- Java 8 (JDK)
- Apache Tomcat 8.x ou 9.x
- Apache Axis2
- Sistema operacional Linux ou Windows

## 2. Instalação Passo a Passo
1. Instale o Java 8:
   ```bash
   sudo apt-get install openjdk-8-jdk
   ```
2. Instale o Tomcat e configure:
   - Baixe de https://tomcat.apache.org/
   - Extraia e configure o diretório
3. Instale o Axis2:
   - Baixe o WAR ou distribua os arquivos em `WEB-INF`
   - Certifique-se de que módulos e configs estejam em `WEB-INF/modules` e `WEB-INF/conf`
4. Compile o projeto:
   - Use Ant, Maven ou Gradle
   - Exemplo com Ant:
     ```bash
     ant -buildfile build.xml
     ```
5. Gere o WAR (opcional):
   ```bash
   jar -cvf wsorpec.war -C WebContent/ .
   ```
6. Faça o deploy no Tomcat:
   ```bash
   cp wsorpec.war /caminho/do/tomcat/webapps/
   ./bin/startup.sh
   ```
7. Verifique o deploy acessando:
   - `http://<servidor>:8080/wsorpec/services/`

## 3. Deploy no Tomcat
- Copie o WAR ou pasta do projeto para `webapps/`
- Reinicie o Tomcat após alterações
- Verifique logs em caso de erro

## 4. Dicas para Produção
- Utilize HTTPS
- Altere credenciais padrão do Axis2
- Restrinja acesso à administração
- Faça backup dos arquivos de configuração

## 5. Links Úteis
- [Documentação Técnica](./TECHNICAL_DOCUMENTATION.md)
- [Referência de API](./API_REFERENCE.md)
- [Troubleshooting](./TROUBLESHOOTING.md) 