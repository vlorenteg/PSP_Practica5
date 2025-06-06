package practica5PSP;

	import java.awt.Color;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.io.IOException;
	import javax.net.ssl.SSLServerSocket; //
	import javax.net.ssl.SSLServerSocketFactory; //
	import javax.net.ssl.SSLSocket; //
	import java.net.SocketException;
	import javax.swing.JButton;
	import javax.swing.JFrame;
	import javax.swing.JScrollPane;
	import javax.swing.JTextArea;
	import javax.swing.JTextField;

	public class ServidorChat extends JFrame implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		static SSLServerSocket servidor; //
		static final int PUERTO = 44444;
		static int CONEXIONES = 0;
		static int ACTUALES = 0;
		static int MAXIMO = 3;
		static JTextField mensaje = new JTextField("");
		static JTextField mensaje2 = new JTextField("");
		private JScrollPane scrollpane1;
		static JTextArea textarea;
		JButton salir = new JButton("Salir");
		static SSLSocket[] tabla = new SSLSocket[MAXIMO]; //

		public ServidorChat()
		{
			super(" VENTANA DEL SERVIDOR DE CHAT ");
			setLayout(null);
			mensaje.setBounds(10, 10, 400, 30);
			add(mensaje);
			mensaje.setEditable(false);
			mensaje2.setBounds(10, 348, 400, 30);
			add(mensaje2);
			mensaje2.setEditable(false);
			textarea = new JTextArea();
			scrollpane1 = new JScrollPane(textarea);
			scrollpane1.setBounds(10, 50, 400, 300);
			add(scrollpane1);
			salir.setBounds(420, 10, 100, 30);
			add(salir);
			textarea.setEditable(false);
			salir.addActionListener(this);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		public static void main(String args[]) throws Exception
		{
			// establecer la información del fichero del almacén de claves
			System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\aerocool\\Desktop\\GRADO SUPERIOR\\ECLIPSE SEGUNDO CURSO\\PSP\\PSP_Practica5\\src\\ServerKeyStore.jks"); // el nombre del fichero del almacén
			System.setProperty("javax.net.ssl.keyStorePassword", "studium2023"); // la contraseña
			try
			{
				SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault(); //
				servidor = (SSLServerSocket) sslFactory.createServerSocket(PUERTO); //
				System.out.println("Servidor iniciado...");
				ServidorChat pantalla = new ServidorChat();
				pantalla.setBounds(0, 0, 540, 450);
				pantalla.setVisible(true);
				mensaje.setText("Número de conexiones actuales: " + 0);
				// Se usa un bucle para controlar el número de conexiones.
				// Dentro del bucle el servidor espera la conexión del cliente y cuando se conecta se crea un socket
				while (CONEXIONES < MAXIMO)
				{
					SSLSocket socket; //
					try
					{
						socket = (SSLSocket) servidor.accept(); //
					} catch (SocketException ex)
					{
						// Sale por aquí si pulsamos el botón salir
						break;
					}
					// El socket creado se añade a la tabla, se incrementa el número de conexiones
					// y se lanza el hilo para gestionar los mensajes del cliente que se acaba de conectar
					tabla[CONEXIONES] = socket;
					CONEXIONES++;
					ACTUALES++;
					HiloServidor hilo = new HiloServidor(socket);
					hilo.start();
				}
				// Si se alcanzan 3 conexiones o se pulsa el botón Salir, el programa se sale del bucle.
				// Al pulsar Salir se cierra el ServerSocket lo que provoca una excepción (SocketException)
				// en la sentencia accept(), la excepción se captura y se ejecuta un break para salir del bucle
				if (!servidor.isClosed())
				{
					try
					{
						mensaje2.setForeground(Color.red);
						mensaje2.setText("Máximo Nº de conexiones establecidas: " + CONEXIONES);
						servidor.close();
					} catch (IOException ex)
					{
						ex.printStackTrace();
					}
				} else
				{
					System.out.println("Servidor finalizado...");
				}

			} catch (Exception ex)
			{
				System.out.println("Error en las comunicaciones:" + ex);
			}

		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == salir)
			{
				try
				{
					if (servidor != null) {
						servidor.close();
					}
				} catch (IOException ex)
				{
					ex.printStackTrace();
				}
				System.exit(0);
			}
		}
	}
