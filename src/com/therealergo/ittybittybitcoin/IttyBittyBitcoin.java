//	MIT License
//
//	Copyright (c) 2018 Kienan Ahner-McHaffie (therealergo.com)
//
//	Permission is hereby granted, free of charge, to any person obtaining a copy
//	of this software and associated documentation files (the "Software"), to deal
//	in the Software without restriction, including without limitation the rights
//	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//	copies of the Software, and to permit persons to whom the Software is
//	furnished to do so, subject to the following conditions:
//
//	The above copyright notice and this permission notice shall be included in all
//	copies or substantial portions of the Software.
//
//	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//	SOFTWARE.

package com.therealergo.ittybittybitcoin;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class IttyBittyBitcoin {
	/** The installed version of IttyBittyBitcoin. */
	public static final String version = "1.0BR";
	
	/** Array where every position's index is mapped to the base-58 encoded character representing that index. */
	private static final char[] base58ToChar = new char[] {
			'1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',      'J', 'K', 'L', 'M', 'N',      'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',      'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	/** Array where every position's index is mapped to the base-58 value of the ASCII/UTF-8 character there, or -1 if the character is not valid in base-58 encoding. */
	private static final byte[] charToBase58 = new byte[] {
			  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, 
			  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, 
			  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, 
			  -1,   0,   1,   2,   3,   4,   5,   6,   7,   8,  -1,  -1,  -1,  -1,  -1,  -1, 
			  -1,   9,  10,  11,  12,  13,  14,  15,  16,  -1,  17,  18,  19,  20,  21,  -1, 
			  22,  23,  24,  25,  26,  27,  28,  29,  30,  31,  32,  -1,  -1,  -1,  -1,  -1, 
			  -1,  33,  34,  35,  36,  37,  38,  39,  40,  41,  42,  43,  -1,  44,  45,  46, 
			  47,  48,  49,  50,  51,  52,  53,  54,  55,  56,  57,  -1,  -1,  -1,  -1,  -1
	};
	
	/** The MessageDigest instance used for SHA-256 hashing. */
	private MessageDigest   messageDigestSHA256;
	/** The RIPEMD160Digest instance used for RMD-160 hashing. */
	private RIPEMD160Digest messageDigestRMD160;
	
	/** The Bouncy Castle instance representing the secp256k1 curve that Bitcoin uses to encrypt private keys. */
	private X9ECParameters curve;
	/** The domain instance holding the prespecified parameters to the secp256k1 curve. */
	private ECDomainParameters domain;
	
	/** Constructor used to initialize the Bouncy Castle and Java Security instances used by IttyBittyBitcoin. */
	public IttyBittyBitcoin() {
		// Add Bouncy Castle's security provider once at startup
		Security.addProvider(new BouncyCastleProvider());
		
		// Create the Digests used for RMD-160 and SHA-256 hashing
		try {
			messageDigestSHA256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Failed to find SHA-256 algorithm: Some functions may not work properly!");
			e.printStackTrace();
		}
		messageDigestRMD160 = new RIPEMD160Digest();
		
		// Create the Bouncy Castle curve and Domain instances used for secp256k1 cryptography
		curve = SECNamedCurves.getByName("secp256k1");
		domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
	}
	
	/** Test if the given String 'a' matches String 'b,' returning a human readable test result with String name 't.' */
	private String performTest(String a, String b, String t) {
		if (a.equals(b)) {
			return "<" + t + ">\n -- PASS: " + a + " == " + b + "\n";
		} else {
			return "<" + t + ">\n -- FAIL: " + a + " != " + b + "\n";
		}
	}
	
	/** Perform a series of tests to check that all IttyBittyBitcoin functions are working as intended. Test log is returned as a String. */
	public String performTests() {
		String results = "";
		results = results + performTest(encodeHex(decodeHex("000000")), "000000", "Hexadecimal encode/decode");
		results = results + performTest(encodeBase58(decodeBase58("1111111111111111111114oLvT2")), "1111111111111111111114oLvT2", "Base-58 encode/decode");
		results = results + performTest(encodeHex(decodeBase58("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")), "000111D38E5FC9071FFCD20B4A763CC9AE4F252BB4E48FD66A835E252ADA93FF480D6DD43DC62A641155A5", "Hex<->Base-58 re-encode, leading 0's/1's");
		results = results + performTest(encodeBase58(decodeHex("000111D38E5FC9071FFCD20B4A763CC9AE4F252BB4E48FD66A835E252ADA93FF480D6DD43DC62A641155A5")), "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz", "Hex<->Base-58 re-encode, leading 0's/1's");
		results = results + performTest(encodeHex(checksum(decodeHex("aa"))), "E51600D4", "Checksum computation");
		results = results + performTest(encodeHex   (                  (                      (privateKeyToPublicPoint(decodeHex("01"), false)))), "0479BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", "Public point computation");
		results = results + performTest(encodeHex(publicPointToPublicKey(decodeBase58("11111111111111111111LDo1Uoe"))), "59EC04C8998D69E15E3E9386A8FB6456C9F7892B", "Public key computation");
		results = results + performTest(encodeHex   (                  (publicPointToPublicKey(privateKeyToPublicPoint(decodeHex("01"), false)))), "91B24BF9F5288532960AC687ABB035127B1D28A5", "Public key computation");
		results = results + performTest(encodeBase58(publicKeyToAddress(decodeHex("0000000000000000000000000000000000000000"))), "1111111111111111111114oLvT2", "Address computation");
		results = results + performTest(encodeBase58(publicKeyToAddress(publicPointToPublicKey(privateKeyToPublicPoint(decodeHex("01"), false)))), "1EHNa6Q4Jz2uvNExL497mE43ikXhwF6kZm", "Address computation");
		results = results + performTest(encodeBase58(publicKeyToAddress(publicPointToPublicKey(privateKeyToPublicPoint(findVanityAddress("1B", true), true)))).substring(0, 2), "1B", "Vanity address search");
		return results;
	}

	/** Convert the given unsigned base-2 number, stored as an array of bytes, into a hexadecimal string. Any 0-padding on the left of the hexadecimal string is preserved. */
	public String encodeHex(byte[] value) {
		return DatatypeConverter.printHexBinary(value);
	}
	
	/** Convert the given hexadecimal string to an unsigned base-2 number stored as an array of bytes. Any 0-padding on the left of the hexadecimal string is preserved. */
	public byte[] decodeHex(String hexString) {
		return DatatypeConverter.parseHexBinary(hexString);
	}
	
	/** Converts the given value, an unsigned base-2 array of bytes, to a base-58 value. Any 1-padding on the left of the base-58 string is preserved. */
	public String encodeBase58(byte[] value) {
		// Convert value to two's complement for the BigInteger constructor, by appending a sign byte of 0x00
		byte[] valueIn = new byte[value.length + 1];
		valueIn[0] = 0;
		for (int i = 0; i<value.length; i++) {
			valueIn[i+1] = value[i];
		}
		
		String ret = "";
		BigInteger integerValue = new BigInteger(valueIn);
		while (integerValue.compareTo(BigInteger.ZERO) != 0) {
			int rem = integerValue.remainder(new BigInteger(new byte[]{58})).byteValue();
			ret = base58ToChar[rem] + ret;
			integerValue = integerValue.divide(new BigInteger(new byte[]{58}));
		}
		
		// Pad out the final base-58 string to preserve any leading 1's
		for (int i = 0; i<value.length && value[i]==0; i++) {
			ret = '1' + ret;
		}
		return ret;
	}
	
	/** Converts the given base-58 value to a an unsigned base-2 array of bytes. Any 1-padding on the left of the base-58 string is preserved. */
	public byte[] decodeBase58(String base58String) {
		BigInteger integerValue = BigInteger.ZERO;
		for (int i = 0; i<base58String.length(); i++) {
			int characterAsciiValue = (int)base58String.charAt(i);
			if (characterAsciiValue>127) {
				throw new RuntimeException("Invalid character for Base-58 encoding!");
			}
			byte characterBase58Value = charToBase58[characterAsciiValue];
			if (characterBase58Value<0) {
				throw new RuntimeException("Invalid character for Base-58 encoding!");
			}
			integerValue = integerValue.multiply(new BigInteger(new byte[]{58})).add(new BigInteger(new byte[]{characterBase58Value}));
		}
		byte[] convertedValue = integerValue.toByteArray();
		
		// Remove sign bit as it is unnecessary for us and causes issues with leading 1's
		if (convertedValue[0]==0) {
			convertedValue = Arrays.copyOfRange(convertedValue, 1, convertedValue.length);
		}
		
		// Count number of leading 1's in input base-58 string
		int leadingOnes = 0;
		for (int i = 0; i<base58String.length() && base58String.charAt(i)=='1'; i++) {
			leadingOnes++;
		}
		
		// Pad out the final set of bytes to preserve any leading 1's
		byte[] finalValue = new byte[leadingOnes + convertedValue.length];
		for (int i = 0; i<convertedValue.length; i++) {
			finalValue[i + leadingOnes] = convertedValue[i];
		}
		return finalValue;
	}
	
	/** Compute the SHA-256 hash of the given value. */
	public byte[] hashSHA256(byte[] value) {
		return messageDigestSHA256.digest(value);
	}
	
	/** Compute checksum of the given value, which is the last 4 bytes of the result of SHA-256 hashing the value twice. */
	public byte[] checksum(byte[] value) {
		// Perform SHA-256 hash on value twice
		byte[] doubleSHA256 = 
				messageDigestSHA256.digest(
				messageDigestSHA256.digest(
						value));
		
		// Return first 4 bytes of double-SHA-256-hashed value
		return new byte[]{doubleSHA256[0], 
						  doubleSHA256[1], 
						  doubleSHA256[2], 
						  doubleSHA256[3]};
	}
	
	/** Compute the public point that this private key represents. This point is used to compute the public key and address. */
	public byte[] privateKeyToPublicPoint(byte[] privateKey, boolean compressed) {
		// Convert privateKey to two's complement for the BigInteger constructor, by appending a sign byte of 0x00
		byte[] privateKeyIn = new byte[privateKey.length + 1];
		privateKeyIn[0] = 0;
		for (int i = 0; i<privateKey.length; i++) {
			privateKeyIn[i+1] = privateKey[i];
		}
		
		// Use BouncyCastle to convert the private key into a public key
		BigInteger d = new BigInteger(privateKeyIn);
        ECPoint q = domain.getG().multiply(d);
        
        ECPublicKeyParameters publicParams = new ECPublicKeyParameters(q, domain);
        return publicParams.getQ().getEncoded(compressed);
	}
	
	/** Compute the SHA-256 hash followed by the RIME MD-160 hash of the given value, turning the given public point into a public key. */
	public byte[] publicPointToPublicKey(byte[] value) {
		// SHA-256 hash the value
		byte[] sha256HashedValue = messageDigestSHA256.digest(value);
		
		// RIME MD-160 hash the value
		messageDigestRMD160.update(sha256HashedValue, 0, sha256HashedValue.length);
		byte[] rmd160HashedValue = new byte[messageDigestRMD160.getDigestSize()];
		messageDigestRMD160.doFinal(rmd160HashedValue, 0);
		
		// Return the final value
		return rmd160HashedValue;
	}
	
	/** Compute the address that is represented by the given public key. This is the number which, when encoded in base-58, is used to publicly identify this bitcoin wallet. */
	public byte[] publicKeyToAddress(byte[] value) {
		// Pre-check that the key is in the correct form
		if (value.length != 20) {
			throw new RuntimeException("Input value must be 20-byte (160-bit) to be converted to a public address.");
		}
		
		// Prepend 0x00, the version number
		byte[] appendedValue = new byte[value.length + 1];
		for (int i = 0; i<value.length; i++) {
			appendedValue[i+1] = value[i];
		}
		appendedValue[0] = 0;
		
		// Append the checksum
		byte[] checksum = checksum(appendedValue);
		byte[] finalAddress = new byte[appendedValue.length + 4];
		for (int i = 0; i<appendedValue.length; i++) {
			finalAddress[i] = appendedValue[i];
		}
		finalAddress[appendedValue.length + 0] = checksum[0];
		finalAddress[appendedValue.length + 1] = checksum[1];
		finalAddress[appendedValue.length + 2] = checksum[2];
		finalAddress[appendedValue.length + 3] = checksum[3];
		return finalAddress;
	}
	
	/** Convenience function successively calling publicKeyToAddress(...) and publicPointToPublicKey(...). See those for documentation. */
	public byte[] publicPointToAddress(byte[] publicPoint) {
		return publicKeyToAddress(publicPointToPublicKey(publicPoint));
	}
	
	/** Convenience function successively calling publicPointToPublicKey(...) and privateKeyToPublicPoint(...). See those for documentation. */
	public byte[] privateKeyToPublicKey(byte[] privateKey, boolean compressed) {
		return publicPointToPublicKey(privateKeyToPublicPoint(privateKey, compressed));
	}
	
	/** Convenience function successively calling publicKeyToAddress(...), publicPointToPublicKey(...), and privateKeyToPublicPoint(...). See those for documentation. */
	public byte[] privateKeyToAddress(byte[] privateKey, boolean compressed) {
		return publicKeyToAddress(publicPointToPublicKey(privateKeyToPublicPoint(privateKey, compressed)));
	}
	
	/** Returns a new pseudorandomly-generated private key. */
	public byte[] generateRandomPrivateKey() {
		Random random = new Random();
		byte[] randomBytes = new byte[8];
		random.nextBytes(randomBytes);
		return hashSHA256(randomBytes);
	}
	
	/** Find a vanity Bitcoin address, which will start with the given String 'vanity.' The private key that corresponds to the address will be returned. This is done probabilistically, and can take enormous amounts of time and CPU for longer vanity Strings. */
	public byte[] findVanityAddress(String vanity, boolean compressed) {
		// Check to see if the vanity string could actually be found at all
		try {
			decodeBase58(vanity);
		} catch (Exception e) {
			throw new RuntimeException("Unable to search for vanity address: Given vanity text cannot be base-58 encoded!", e);
		}
		if (!vanity.startsWith("1")) {
			throw new RuntimeException("Unable to search for vanity address: Given vanity text does not start with a '1'!");
		}
		
		// Search random private keys, trying to find a a private key that has a public key matching the vanity string
		Random random = new Random();
		byte[] privateKey = new byte[8];
		byte[] address;
		String addressBase58;
		while (true) {
			random.nextBytes(privateKey);
			
			address = publicKeyToAddress(publicPointToPublicKey(privateKeyToPublicPoint(privateKey, compressed)));
			addressBase58 = encodeBase58(address);
			if (addressBase58.startsWith(vanity)) {
				break;
			}
		}
		return privateKey;
	}
}
