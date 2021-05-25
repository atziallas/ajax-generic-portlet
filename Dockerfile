FROM ibaiborodine/liferay-portal-ce:7.4.0-ga1-jdk8-buster
RUN wget https://services.gradle.org/distributions/gradle-6.8.3-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-*.zip
ENV PATH="/opt/gradle/gradle-6.8.3/bin:${PATH}"
#RUN export PATH=/opt/gradle/gradle-6.8.3/bin:$PATH
#RUN echo "export PATH=/opt/gradle/gradle-6.8.3/bin:$PATH" > /etc/profile.d/gradle.sh
#RUN chmod +x /etc/profile.d/gradle.sh
#RUN sudo /etc/profile.d/gradle.sh