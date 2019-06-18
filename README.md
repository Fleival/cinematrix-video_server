# **Cinematrix video server**

<br>

[![Starting Cinematrix Server](https://img.youtube.com/vi/0MnbAeajM9A/0.jpg)](https://www.youtube.com/watch?v=0MnbAeajM9A "Starting Cinematrix Server - Click to Watch!")

<br>

[![Cinematrix Server and Android App in action](https://img.youtube.com/vi/FRB0rUeTU9c/0.jpg)](https://www.youtube.com/watch?v=FRB0rUeTU9c "Cinematrix Server and Android App in action - Click to Watch!")

-   implemented on the Dropwizard framework and runs in the Jetty servlet
    container;

-   the “logical core” of the server is a multi-threaded high-performance HTML
    parser with flexible java-architecture for extending implementations to
    other sources;

>   *(Tech stack: Jsoup, VTD-xml, Java8 Stream Api, Java 8 Completable Future)*

-   \-configuration is read from yml-files;

>   *(Tech stack: SnakeYml)*

-   MySQL is selected as a database.

-   \-server uses Hibernate and SpringJPA managing transactions in the service
    layer (MODEL-DAO- \@ Transactional_DAO_SERVICE);

>   *(Tech stack: MySQL, Hibernate, SpringJPA)*

-   implemented caching during running of the “core” -Into and -Out xml using
    the VTD-xml (world fastest xml processor);

>   *(Tech stack: VTD-xml)*

-   \-for the server management and running of the Android application the API
    RESTFull service is implemented;

>   *(Tech stack: Jackson, Jersey)*
