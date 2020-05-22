# Arquitectura original del paquete com.jcraft.jroar

Originalmente, este paquete se dividía en estos 4 componentes: las páginas de la interfaz web, los distintos tipos de “Sources”, los distintos tipos de clientes y el servidor http.  
  
Respecto a las páginas de la interfaz web, este componente estaba formado por todas las clases que heredan de Page y se encargaban, tanto de proporcionar una interfaz web primitiva, como de permitir al usuario interactuar con JRoar.   
  
Las páginas de Stat y Debug mostraban información técnica sobra JRoar para facilitar la depuración y el desarrollo, la clase HomePage se encargaba de mostrar información sobre los puntos de montura y el número de conexiones abiertas a estos puntos, y las clases Mount, Drop, Ctrl y Shout permitían al usuario añadir y eliminar puntos de montura desde la interfaz web.  
  
En cuanto a los distintos tipos de “Sources”, son las distintas fuentes de las que ecuchar el audio. Las fuentes podían ser tanto archivos ogg y listas de reproducción, que eran gestionadas por la clase PlayList, como url en las que se esten haciendo streaming de audio, que eran tratadas por la clase Proxy. También se pueden retransmitir streamings de iceCast2 con la clase Ice.  
  
Los clientes son todas las clases que heredan de Client y permitían a JRoar actuar no solo como servidor sino también como cliente. Destaca el ShoutClient con el que JRoar podía retransmitir el audio de un punto de montura a un punto de montura de un servidor iceCast2.  
  
Finalmente, el servidor http se encargaba de servir las páginas de la interfaz web y de establecer las conexiones a los diferentes puntos de montura, permitiendo a los distintos clientes escuchar el audio retransmitido en estos puntos.  
  
Todo el sistema se iniciaba desde la clase JRoar, encargada de leer los parámetros en el método main y crear el HttpServer con la configuración establecida en estos parámetros. Además, esta clase, que hereda de Applet, inicializaba la interfaz web.  
  
También cabe destacar que JRoar permitía usar el protocolo PeerCast, aunque actualmente este protocolo de comunicación ya no es utilizado.  
  
Es importante tener en cuenta que este paquete com.jcraft.jroar utiliza clases y componentes del paquete com.jcraft.jogg para tratar los archivos y streaming en formato ogg y del paquete com.jcraft.jorbis para emisión de audio y para el uso del reproductor JOrbisPlayer.  
  
**Enlace al UML JRoar Original:** [UML JRoar Original](UML_JRoar_Original.png)

# Evolución del paquete com.jcraft.jroar e integración con Spring
Respecto a la evolución de JRoar cabe destacar la eliminación de todo lo relacionado con el difunto protocolo PeerCast y el reproductor JOrbisPlayer y la transformación de la primitiva interfaz web vía applet en una interfaz web actual basada en Spring.  
  
Las antiguas clases que heredaban Page tenían el problema de que en sus métodos kick se encargaban tanto de la lógica de negocio como de generar el HTML para la interfaz web. Al crear una nueva interfaz web la parte de HTML que realizaban estos métodos no es necesaria, pero sí que es muy importante la parte lógica. Por ello, se crearon nuevos métodos que reutilizan esta lógica de negocio sin la parte encargada del HTML. Desde la clase ControlController del paquete jroar.web se llama a estos nuevos métodos para poder añadir y eliminar puntos de montura. Por tanto la conexión entre la nueva interfaz web y el paquete com.jcraft.jroar se realiza a través de estos métodos.  
  
La otra conexión entre la interfaz web y el paquete com.jcraft.jroar es a través de la clase JRoar, en la que se ha sustituido el método main, que permitía iniciar la aplicación por consola, por un método intall. Este método install permite iniciar la aplicación con las características indicadas en el HashMap de entrada. El InstallerControler del paquete jroar.web recoge los datos introducidos por el usuario y llama al método a este método install con ellos, permitiendo iniciar la aplicación desde el instalador web.  
  
**Enlace al UML JRoar Evolución:** [UML JRoar Evolución](UML_JRoar_Evolución.png)
  
# Arquitectura del paquete jroar.web
La estructura actual del sistema esta divida en dos paquetes, el paquete jroar.code donde se encontra el código modificado de JRoar y el paquete jroar.web donde se encuentran todos los archivos necesarios para la interfaz web.  La implementación de esta interfaz con Spring se basa en el uso de una arquitectura Modelo-Vista-Controlador, en la que las vistas son los archivos html y sus hojas de estilos css y JavaScript, los controladores son las clases contenidas en el paquete jroar.web.controller, y el modelo son las clases de los paquetes jroar.web.model, jroar.web.repositories y jroar.web.services que permiten almacenar y tratar los datos de la aplicación.  
  
En cuanto al paquete jroar.web.controller, las clases de este paquete se encargan de gestionar la información intercambiada con las plantillas html que componen la vista a través de objetos model. Un objeto model permite comunicar datos entre las vistas y los diferentes servicios y repositorios a través de un controlador, por lo que son objetos clave en la arquitectura de Spring.  
  
En el paquete de jroar.web.model se encuentran los tipos de datos y objetos crados para poder almacenar la información de una forma estructurada. Además, en este paquete se encuentran las clases que harán de entidades en la base de datos, tales como los comentarios (Comment), las estadísticas o la información de los usuarios (User).  
  
En el paquete de repositorios, jroar.web.repositories, se encuentran las clases que van a comunicar la base de datos, implementada en H2, con el resto del sistema. Estas clases permitirán a los controladores guardar información y extraer información a partir de consultas (Query) o búsquedas a partir de parámetros, posibles gracias a JPA.  
  
En cuanto al paquete services, jroar.web.services, las clases contenidas en él permiten la reutilización de código ya que se encargan de tareas necesarias en múltiples controladores.  
  
Por último, el paquete jroar.web.security está dedicado a contener las clases que implementan la seguridad en la web. Estas clases se encargan de restringir el acceso a las distintas URLs según las credenciales de los usuarios. Los posibles roles de los usuarios son: USER y ADMIN. El rol USER tiene permitido el crear puntos de montura, ver y editar su perfil, escribir comentarios y dar like/dislike a las emisiones. El ADMIN, además de los permisos del rol USER, tiene permitido eliminar los puntos de montura.

**Enlace al UML Spring Evolución:** [UML Spring Evolución](UML_Spring_Evolución.png)
