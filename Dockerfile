FROM tomcat:10.1-jdk17-openjdk-slim
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]