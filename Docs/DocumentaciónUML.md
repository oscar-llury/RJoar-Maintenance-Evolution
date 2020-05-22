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
  
**Enlace al UML JRoar Original:** [UML JRoar Original](/UML_JRoar_Original.png)
