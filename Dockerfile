FROM ubu_my_java:latest
COPY docker_run/config.yml /home/cinematrix/
ADD docker_run/parser_config /home/cinematrix/parser_config
ADD docker_run/xlink_dir /home/cinematrix/xlink_dir
COPY target/cinematrix-video-app-1.0.1-alpha.jar /home/cinematrix/
EXPOSE 8020 8021
WORKDIR /home/cinematrix/
CMD ["java", "-jar", "cinematrix-video-app-1.0.1-alpha.jar", "server", "config.yml"]