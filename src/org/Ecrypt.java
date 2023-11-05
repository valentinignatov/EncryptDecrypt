package org;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Ecrypt {
	private static final String DIR = System.getProperty("user.dir");

	public static void main(String args[]) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		System.out.println("1. generates & save new keys");
		System.out.println("2. store an encrypted message");
		System.out.println("3. load an encrypted message");
		System.out.println("4. exit");

		Scanner input = new Scanner(System.in);
		System.out.print("Enter a line of text: ");
		int line = input.nextInt();
		System.out.println("You selected: " + line);

		switch (line) {
		case 1: {
			new Ecrypt().generateAndSaveNewKeys();
			break;
		}
		case 2: {
			Scanner inputString = new Scanner(System.in);
			System.out.print("Enter a line of text: ");
			String lineString = inputString.nextLine();
			System.out.println("You selected: " + lineString);

			new Ecrypt().storeAnEncryptedMessage(lineString);
			break;
		}
		case 3: {
			new Ecrypt().loadAnEncryptedMessage();
			break;
		}
		case 4: {
			System.exit(0);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + line);
		}
	}

	private void generateAndSaveNewKeys() {
		// Create a KeyPairGenerator object
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Initialize the KeyPairGenerator with a certain key size
		keyPairGenerator.initialize(512);

		// Generate the keys
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// Extract the keys
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		writeKeyFile("privateKey", privateKey.getEncoded());
		writeKeyFile("publicKey", publicKey.getEncoded());
//		readFile("spongebob");
	}

	private void storeAnEncryptedMessage(String message) {
		PublicKey publicKey = readPublicKeyFile("publicKey.txt");

		Cipher cipher = null;

		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			cipher.update(message.getBytes());
			writeFile("StoredCryptedMessage", cipher.doFinal());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private void loadAnEncryptedMessage() {
		PrivateKey privateKey = readPrivateKeyFile("privateKey.txt");
		Cipher cipher = null;

		byte[] decrypted;
		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.update(readFile("StoredCryptedMessage.txt"));
			decrypted = cipher.doFinal();
			System.out.println("decrypted: " + new String(decrypted, "UTF8"));
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | UnsupportedEncodingException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeFile(String fileName, byte[] message) {

		fileName = fileName + ".txt";

		if (!new File(fileName).exists()) {
			System.out.println("File " + fileName + " does not exist");
			try {
				new File(fileName).createNewFile();
				System.out.println("Created " + fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		InputStream in = new ByteArrayInputStream(message);
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(constructPath(fileName));
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			System.out.println("The date was written successfully");

		} catch (IOException e) {
			e.printStackTrace();
		}

//        FileWriter fw;
//		try {
//			// attach a file to FileWriter
//			fw = new FileWriter(DIR + fileName + ".txt");
//			
//			// read character wise from string and write into FileWriter
//	        for (int i = 0; i < message.length(); i++) 
//	            fw.write(message.charAt(i)); 
//	        
//	        System.out.println("Writing successful"); 
//	        
//	        //close the file  
//	        fw.close(); 
//	        
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	private void writeKeyFile(String fileName, byte[] bytesKey) {

		fileName = fileName + ".txt";

		if (!new File(fileName).exists()) {
			System.out.println("File " + fileName + " does not exist");
			try {
				new File(fileName).createNewFile();
				System.out.println("Created " + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStream in = new ByteArrayInputStream(bytesKey);
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(constructPath(fileName));
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			System.out.println("The date was written successfully");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] readFile(String fileName) {
		InputStream input;
		byte[] arr = null;

		try {
			input = new FileInputStream(constructPath(fileName));
			arr = input.readAllBytes();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return arr;
	}

	private PublicKey readPublicKeyFile(String fileName) {
		InputStream input;
		byte[] arr;

		try {
			input = new FileInputStream(constructPath(fileName));
			arr = input.readAllBytes();
			X509EncodedKeySpec spec = new X509EncodedKeySpec(arr);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(spec);
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	private PrivateKey readPrivateKeyFile(String fileName) {
		InputStream input;
		byte[] arr;

		try {
			input = new FileInputStream(constructPath(fileName));
			arr = input.readAllBytes();
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(arr);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(spec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}

	}

	private String readInputStream(InputStream input) {
		StringBuilder textBuilder = new StringBuilder();

		try (Reader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
			int c = 0;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return textBuilder.toString();
	}

	private String constructPath(String filename) {
		StringBuilder builder = new StringBuilder();

		builder.append(DIR);
		builder.append("\\");
		builder.append(filename);
//		builder.append(".txt");

		return builder.toString();
	}

	private void smth() {
		KeyPairGenerator generator;
		Cipher cipher;
		byte[] encryptedBytes = null;
		byte[] message = "Hello, World!".getBytes();

		byte[] values = new byte[53];
		for (int i = 0; i < values.length; i++) {
			values[i] = 1;
		}

		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(512);
			KeyPair keypair = generator.generateKeyPair();
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, keypair.getPublic());
			encryptedBytes = cipher.doFinal(message);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(encryptedBytes);
		System.out.println("key length:" + encryptedBytes.length);
	}
}
