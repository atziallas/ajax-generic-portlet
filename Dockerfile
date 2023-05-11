FROM ibaiborodine/liferay-portal-ce:7.4.0-ga1-jdk8-buster
RUN wget https://services.gradle.org/distributions/gradle-6.8.3-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-*.zip
ENV PATH="/opt/gradle/gradle-6.8.3/bin:${PATH}"