package br.com.comercio.service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

@Service
public class CriptografiaService {
	private String IV = "AAAAAAAAAAAAAAAA";
	private String textopuro = "244.049.750-90";
	private String chaveencriptacao = "0123456789abcdef";

	public void main(String[] args) {

		try {
			String encriptar = encriptar(textopuro);
			String decriptar = decriptar(encriptar);
			System.out.println("Decriptado: " + decriptar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String encriptar(String texto) throws Exception {
		Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(this.chaveencriptacao.getBytes("UTF-8"), "AES");
		encripta.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(this.IV.getBytes("UTF-8")));
		byte[] doFinal = encripta.doFinal(texto.getBytes("UTF-8"));
		String textoEncriptado = DatatypeConverter.printBase64Binary(doFinal);
		//System.out.println("Texto encriptado:" + textoEncriptado);
		return textoEncriptado;
	}

	public String decriptar(String texto) throws Exception {
		Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(this.chaveencriptacao.getBytes("UTF-8"), "AES");
		byte[] textoEmBytes = DatatypeConverter.parseBase64Binary(texto);
		decripta.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(this.IV.getBytes("UTF-8")));
		return new String(decripta.doFinal(textoEmBytes), "UTF-8");
	}
}
