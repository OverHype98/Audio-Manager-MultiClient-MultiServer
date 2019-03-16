# Audio-Manager-MultiClient-MultiServer

Audio File Manager An application that manages your personal music collection. Each song will have the following properties: title, genre and rating. The program  allows: - adding / deleting entries in the collection - the data will be read from the keyboard; customized exceptions will be generated for non-valid data; - saving / restoring information to a file;
Adding, modifying songs will be done through a modal dialog window.
Storing data and reading data from database(MariaDB).
The project is multi client multi server and also uses threads.
To run the project right click on properties->java build path -> add json-simple-1.1 to the ClientApp and to the ServerApp add json-simple-1.1 and mariadb-java-client-2.3.0 libraries.
The database must be named music , the table Muzica with the following columns : id INT , title VARCHAR , genre VARCHAR, rating INT.
The Server app must run first, then the client app.
