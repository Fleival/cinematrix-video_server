# **Cinematrix video server**

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

-   implemented caching during operation of the “core” -Into and -Out xml using
    the VTD-xml (world fastest xml processor);

>   *(Tech stack: VTD-xml)*

-   \-for the server management and operation of the Android application the API
    RESTFull service is implemented;

>   *(Tech stack: Jackson, Jersey)*
