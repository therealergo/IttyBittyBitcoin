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

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Scanner;

public class IttyBittyBitcoinCommandLine {
	/** Convert the given String into a boolean, throwing an error if the String is not in the right form. */
	private static boolean decodeBoolean(String value) {
		if (value.equals("true") || value.equals("True") || value.equals("TRUE") || value.equals("1")) {
			return true;
		}
		if (value.equals("false") || value.equals("False") || value.equals("FALSE") || value.equals("0")) {
			return false;
		}
		throw new RuntimeException("Unrecognized boolean input: '" + value + "'.");
	}
	
	/** Entry point for command-line mode of IttyBittyBitcoin. */
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		// Initialize variables for interactive mode, and enter interactive mode loop
		boolean interactiveMode = false;
		boolean startup = true;
		try (Scanner sc = new Scanner(System.in)) {
			while (interactiveMode || startup) {
				startup = false;
				
				// Wrap commands in try command so that exceptions do not cause us to leave interactive mode
				try {
					// Prevent errors caused by incorrect arguments
					if (args.length == 0 || args==null) {
						args = new String[]{""};
					}
					
					// Perform each command if applicable
					if (args.length == 2 && (args[0].equals("checksum") || args[0].equals("-"))) {
						System.out.println("Computed checksum: " + IttyBittyBitcoin.encodeHex(IttyBittyBitcoin.checksum(IttyBittyBitcoin.decodeHex(args[1]))));
					} else if (args.length == 3 && (args[0].equals("privateKeyToPublicPoint") || args[0].equals("kp"))) {
						System.out.println("Public point: " + IttyBittyBitcoin.encodeHex(IttyBittyBitcoin.privateKeyToPublicPoint(IttyBittyBitcoin.decodeHex(args[1]), decodeBoolean(args[2]))));
					} else if (args.length == 2 && (args[0].equals("publicPointToPublicKey") || args[0].equals("pK"))) {
						System.out.println("Public key: " + IttyBittyBitcoin.encodeHex(IttyBittyBitcoin.publicPointToPublicKey(IttyBittyBitcoin.decodeHex(args[1]))));
					} else if (args.length == 2 && (args[0].equals("publicKeyToAddress") || args[0].equals("Ka"))) {
						System.out.println("Address: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.publicKeyToAddress(IttyBittyBitcoin.decodeHex(args[1]))));
					} else if (args.length == 2 && (args[0].equals("publicPointToAddress") || args[0].equals("pa"))) {
						System.out.println(IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.publicPointToAddress(IttyBittyBitcoin.decodeHex(args[1]))));
					} else if (args.length == 3 && (args[0].equals("privateKeyToPublicKey") || args[0].equals("kK"))) {
						System.out.println("Public key: " + IttyBittyBitcoin.encodeHex(IttyBittyBitcoin.privateKeyToPublicKey(IttyBittyBitcoin.decodeHex(args[1]), decodeBoolean(args[2]))));
					} else if (args.length == 3 && (args[0].equals("privateKeyToAddress") || args[0].equals("ka"))) {
						System.out.println("Address: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.privateKeyToAddress(IttyBittyBitcoin.decodeHex(args[1]), decodeBoolean(args[2]))));
					} else if (args.length == 2 && (args[0].equals("generateRandom") || args[0].equals("gR"))) {
						System.out.println("Generating random keyset...");
						byte[] privateKey = IttyBittyBitcoin.generateRandomPrivateKey();
						System.out.println("Generated private key: " + IttyBittyBitcoin.encodeHex(privateKey));
						System.out.println("Generated key's address: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.privateKeyToAddress(privateKey, decodeBoolean(args[1]))));
					} else if (args.length == 3 && (args[0].equals("generateFromHash") || args[0].equals("gH"))) {
						System.out.println("Generating keyset from hash...");
						byte[] privateKey = IttyBittyBitcoin.decodeHex(args[1]);
						System.out.println("Generated private key: " + IttyBittyBitcoin.encodeHex(privateKey));
						System.out.println("Generated key's address: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.privateKeyToAddress(privateKey, decodeBoolean(args[2]))));
					} else if (args.length == 4 && (args[0].equals("generateFromStringSHA256") || args[0].equals("gS"))) {
						System.out.println("Generating keyset from SHA-256 hashed String...");
						byte[] privateKey = IttyBittyBitcoin.hashSHA256(args[1].replace("\\n", "\n").replace("\\t", "\t").replace("\\s", " ").replace("\\\\", "\\").getBytes(args[2]));
						System.out.println("Generated private key: " + IttyBittyBitcoin.encodeHex(privateKey));
						System.out.println("Generated key's address: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.privateKeyToAddress(privateKey, decodeBoolean(args[3]))));
					} else if (args.length == 3 && (args[0].equals("findVanityAddress") || args[0].equals("v"))) {
						System.out.println("Searching for vanity address...");
						byte[] privateVanity = IttyBittyBitcoin.findVanityAddress(args[1], decodeBoolean(args[2]));
						System.out.println("Vanity address found!");
						System.out.println("Vanity address private key: " + IttyBittyBitcoin.encodeHex(privateVanity));
						System.out.println("Vanity address: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.privateKeyToAddress(privateVanity, decodeBoolean(args[2]))));
					} else if (args.length == 2 && (args[0].equals("encodeBase58") || args[0].equals("eB"))) {
						System.out.println("Base-58 encoded value: " + IttyBittyBitcoin.encodeBase58(IttyBittyBitcoin.decodeHex(args[1])));
					} else if (args.length == 2 && (args[0].equals("encodeHex") || args[0].equals("eH"))) {
						System.out.println("Hexadecimal encoded value: " + IttyBittyBitcoin.encodeHex(IttyBittyBitcoin.decodeBase58(args[1])));
					} else if (args.length == 1 && (args[0].equals("performTests") || args[0].equals("t"))) {
						System.out.println(IttyBittyBitcoin.performTests());
					} else if (args.length == 1 && (args[0].equals("interactiveMode") || args[0].equals("i"))) {
						System.out.println(interactiveMode ? "Leaving interactive mode. Goodbye!" : "Entering interactive mode. Welcome!");
						interactiveMode = !interactiveMode;
					} else if (args.length == 1 && (args[0].equals("help") || args[0].equals("Help") || args[0].equals("?") || args[0].equals("/?") || args[0].equals("-?") || args[0].equals("h") || args[0].equals("H"))) {
						System.out.println("IttyBittyBitcoin V" + IttyBittyBitcoin.version + "");
						System.out.println("------------");
						System.out.println("Description: ");
						System.out.println("	IttyBittyBitcoin is a set of well-documented free open-source cross-platform Java tools for working with bitcoin addresses. It can either be used as a library or run from the command line.");
						System.out.println("------------");
						System.out.println("Command reference: ");
						System.out.println("['checksum'                 or 'c' ] <a>");
						System.out.println("    <a>: Hexadecimal input: Value to compute checksum of.");
						System.out.println("    Compute checksum of the given value, which is the last 4 bytes of the result of SHA-256 hashing the value twice.");
						System.out.println("['privateKeyToPublicPoint'  or 'kp'] <a> <b>");
						System.out.println("    <a>: Hexadecimal input: Private key.");
						System.out.println("    <b>: Boolean     input: Point compressed?");
						System.out.println("    Compute the public point that this private key represents. This point is used to compute the public key and address.");
						System.out.println("['publicPointToPublicKey'   or 'pK'] <a>");
						System.out.println("    <a>: Hexadecimal input: Public point.");
						System.out.println("    Compute the SHA-256 hash followed by the RIME MD-160 hash of the given value, turning the given public point into a public key.");
						System.out.println("['publicKeyToAddress'       or 'Ka'] <a>");
						System.out.println("    <a>: Hexadecimal input: Public key.");
						System.out.println("    Compute the address that is represented by the given public key. This is the number which, when encoded in base-58, is used to publicly identify this bitcoin wallet.");
						System.out.println("['publicPointToAddress'     or 'pa'] <a>");
						System.out.println("    <a>: Hexadecimal input: Public point.");
						System.out.println("    Convenience function successively calling publicKeyToAddress <...> and publicPointToPublicKey <...>. See those for documentation.");
						System.out.println("['privateKeyToPublicKey'    or 'kK'] <a> <b>");
						System.out.println("    <a>: Hexadecimal input: Private key.");
						System.out.println("    <b>: Boolean     input: Point compressed?");
						System.out.println("    Convenience function successively calling publicPointToPublicKey <...> and privateKeyToPublicPoint <...>. See those for documentation.");
						System.out.println("['privateKeyToAddress'      or 'ka'] <a> <b>");
						System.out.println("    <a>: Hexadecimal input: Private key.");
						System.out.println("    <b>: Boolean     input: Point compressed?");
						System.out.println("    Convenience function successively calling publicKeyToAddress <...>, publicPointToPublicKey <...>, and privateKeyToPublicPoint <...>. See those for documentation.");
						System.out.println("['generateRandom'           or 'gR'] <a>");
						System.out.println("    <a>: Boolean     input: Point compressed?");
						System.out.println("    Creates a new pseudorandomly-generated keypair.");
						System.out.println("['generateFromHash'         or 'gH'] <a> <b>");
						System.out.println("    <a>: Hexadecimal input: Hash value, typically 256-bit.");
						System.out.println("    <b>: Boolean     input: Point compressed?");
						System.out.println("    Creates a keypair from the given hash value.");
						System.out.println("['generateFromStringSHA256' or 'gS'] <a> <b> <c>");
						System.out.println("    <a>: String      input: String to be hashed. '\\s' will escape as a space character, '\\n' will escape as a newline character, and '\\t' will escape as a tab character. '\\\\' can be used to escape a backslash.");
						System.out.println("    <a>: String      input: Charset of string to be hashed. Typically one of 'US-ASCII' or 'UTF-8'.");
						System.out.println("    <c>: Boolean     input: Point compressed?");
						System.out.println("    Creates a keypair from the SHA-256 hash value of the given String.");
						System.out.println("['findVanityAddress'        or 'v' ] <a> <b>");
						System.out.println("    <a>: Base-58     input: Vanity string to search for.");
						System.out.println("    <b>: Boolean     input: Point compressed?");
						System.out.println("    Find a vanity Bitcoin address, which will start with the given String. This is done probabilistically, and can take enormous amounts of time and CPU for longer vanity Strings.");
						System.out.println("['encodeBase58'             or 'eB'] <a>");
						System.out.println("    <a>: Hexadecimal input: Hexadecimal number to be converted to base-58.");
						System.out.println("    Convert the given hexadecimal value to base-58.");
						System.out.println("['encodeHex'                or 'eH'] <a>");
						System.out.println("    <a>: Base-58     input: Base-58 number to be converted to hexadecimal.");
						System.out.println("    Convert the given base-58 value to hexadecimal.");
						System.out.println("['performTests'             or 't' ]");
						System.out.println("    Perform a series of tests to make sure that IttyBittyBitcoin is functioning properly.");
						System.out.println("['interactiveMode'          or 'i' ]");
						System.out.println("    Toggle interactive mode, which allows IttyBittyBitcoin commands to be entered alone without retyping the path to the executable jar.");
						System.out.println("['help'                     or '?' ]");
						System.out.println("    Display this help screen.");
						System.out.println("------------");
						System.out.println("Notes: ");
						System.out.println("    For every private key, both a compressed and uncompressed address can be generated. Those two addresses are completely different, with different balances. This exists because there are two ways of representing the public point. Early bitcoin software may not support the compressed addresses properly, but virtually all modern software will prefer the compressed addresses because of the smaller filesizes they create.");
						System.out.println("    generateFromHash <...> is identical to privateKeyToPublicKey <...>, but both are maintained because they display the resulting output differently.");
						System.out.println("    The amount of time a vanity address takes to generate depends on both the number of and type of characters in it. For example, '1' characters typically take much longer to find than others, while 'A' characters are typcially easier to find. ");
					} else {
						System.out.println("Unable to determine command. Try command \"help\" for a command reference.");
					}
				} catch (Exception e) {
					// Only throw exception when not in interactive mode
					if (interactiveMode) {
						e.printStackTrace();
					} else {
						throw e;
					}
				}
				
				// Wait for next set of commands/arguments when in interactive mode
				if (interactiveMode) {
					String line = sc.nextLine() + " "; // Space is added to prevent last argument being skipped
					
					// Break up given list of arguments by white space
					ArrayList<String> newArgs = new ArrayList<String>();
					String currentArg = "";
					for (int pos = 0; pos<line.length(); pos++) {
						if (Character.isWhitespace(line.charAt(pos))) {
							if (currentArg.length()>0) {
								newArgs.add(currentArg);
								currentArg = "";
							}
						} else {
							currentArg = currentArg + line.charAt(pos);
						}
					}
					
					// Reset list of arguments, allowing the interactive mode loop to begin again
					args = newArgs.toArray(new String[newArgs.size()]);
				}
			}
		}
	}
}
