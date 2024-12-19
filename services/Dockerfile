FROM mongo
RUN mkdir -p /opt/keyfile
RUN openssl rand -base64 756 > /opt/keyfile/mongo-keyfile
RUN chmod 600 /opt/keyfile/mongo-keyfile
RUN chown 999:999 /opt/keyfile/mongo-keyfile
RUN echo "created keyfile"