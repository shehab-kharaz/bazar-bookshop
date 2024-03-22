# Use a Java base image
FROM openjdk:latest

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the Java source code and all JAR files to the container
COPY CatalogMain.java ./
COPY catalog.txt ./
COPY catalog_log.txt ./
COPY spark-core-2.9.1.jar ./
COPY slf4j-api-1.7.32.jar ./
COPY logback-classic-1.2.6.jar ./
COPY logback-core-1.2.6.jar ./
COPY json-20210307.jar ./
COPY junit-3.8.1.jar ./
COPY javax.servlet-api-4.0.1.jar ./
COPY jetty-server-10.0.1.jar ./
COPY jetty-util-10.0.1.jar ./
COPY jetty-http-10.0.1.jar ./
COPY jetty-io-10.0.1.jar ./

# Compile the Java source code
RUN javac -cp ".:spark-core-2.9.1.jar:slf4j-api-1.7.32.jar:logback-classic-1.2.6.jar:logback-core-1.2.6.jar:json-20210307.jar:junit-3.8.1.jar:javax.servlet-api-4.0.1.jar:jetty-server-10.0.1.jar:jetty-util-10.0.1.jar:jetty-http-10.0.1.jar:jetty-io-10.0.1.jar" CatalogMain.java

# Expose port 4567
EXPOSE 4567

# Specify the command to run your Java application
CMD ["java", "-cp", ".:spark-core-2.9.1.jar:slf4j-api-1.7.32.jar:logback-classic-1.2.6.jar:logback-core-1.2.6.jar:json-20210307.jar:junit-3.8.1.jar:javax.servlet-api-4.0.1.jar:jetty-server-10.0.1.jar:jetty-util-10.0.1.jar:jetty-http-10.0.1.jar:jetty-io-10.0.1.jar", "CatalogMain"]

