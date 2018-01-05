IttyBittyBitcoin
------------

IttyBittyBitcoin is a set of well-documented free open-source cross-platform Java tools for working with bitcoin addresses. It can either be used as a library or run from the command line.

Command reference
------------

``['checksum'                 or 'c' ] [a]``  
> Compute checksum of the given value, which is the last 4 bytes of the result of SHA-256 hashing the value twice.  
``[a]: Hexadecimal input`` Value to compute checksum of.  

``['privateKeyToPublicPoint'  or 'kp'] [a] [b]``  
> Compute the public point that this private key represents. This point is used to compute the public key and address.  
``[a]: Hexadecimal input`` Private key.  
``[b]: Boolean     input`` Point compressed?  

``['publicPointToPublicKey'   or 'pK'] [a]``  
> Compute the SHA-256 hash followed by the RIME MD-160 hash of the given value, turning the given public point into a public key.  
``[a]: Hexadecimal input`` Public point.  

``['publicKeyToAddress'       or 'Ka'] [a]``  
> Compute the address that is represented by the given public key. This is the number which, when encoded in base-58, is used to publicly identify this bitcoin wallet.  
``[a]: Hexadecimal input`` Public key.  

``['publicPointToAddress'     or 'pa'] [a]``  
> Convenience function successively calling publicKeyToAddress [...] and publicPointToPublicKey [...]. See those for documentation.  
``[a]: Hexadecimal input`` Public point.  

``['privateKeyToPublicKey'    or 'kK'] [a] [b]``  
> Convenience function successively calling publicPointToPublicKey [...] and privateKeyToPublicPoint [...]. See those for documentation.  
``[a]: Hexadecimal input`` Private key.  
``[b]: Boolean     input`` Point compressed?  

``['privateKeyToAddress'      or 'ka'] [a] [b]``  
> Convenience function successively calling publicKeyToAddress [...], publicPointToPublicKey [...], and privateKeyToPublicPoint [...]. See those for documentation.  
``[a]: Hexadecimal input`` Private key.  
``[b]: Boolean     input`` Point compressed?  

``['generateRandom'           or 'gR'] [a]``  
> Creates a new pseudorandomly-generated keypair.  
``[a]: Boolean     input`` Point compressed?  

``['generateFromHash'         or 'gH'] [a] [b]``  
> Creates a keypair from the given hash value.  
``[a]: Hexadecimal input`` Hash value, typically 256-bit.  
``[b]: Boolean     input`` Point compressed?  

``['generateFromStringSHA256' or 'gS'] [a] [b] [c]``  
> Creates a keypair from the SHA-256 hash value of the given String.  
``[a]: String      input`` String to be hashed. '\\s' will escape as a space character, '\\n' will escape as a newline character, and '\\t' will escape as a tab character. '\\\\' can be used to escape a backslash.  
``[a]: String      input`` Charset of string to be hashed. Typically one of 'US-ASCII' or 'UTF-8'.  
``[c]: Boolean     input`` Point compressed?  

``['findVanityAddress'        or 'v' ] [a] [b]``  
> Find a vanity Bitcoin address, which will start with the given String. This is done probabilistically, and can take enormous amounts of time and CPU for longer vanity Strings.  
``[a]: Base-58     input`` Vanity string to search for.  
``[b]: Boolean     input`` Point compressed?  

``['encodeBase58'             or 'eB'] [a]``  
> Convert the given hexadecimal value to base-58.  
``[a]: Hexadecimal input`` Hexadecimal number to be converted to base-58.  

``['encodeHex'                or 'eH'] [a]``  
> Convert the given base-58 value to hexadecimal.  
``[a]: Base-58     input`` Base-58 number to be converted to hexadecimal.  

``['performTests'             or 't' ]``  
> Perform a series of tests to make sure that IttyBittyBitcoin is functioning properly.  

``['interactiveMode'          or 'i' ]``  
> Toggle interactive mode, which allows IttyBittyBitcoin commands to be entered alone without retyping the path to the executable jar.  

``['help'                     or '?' ]``  
> Display this help screen.  

Notes
------------

For every private key, both a compressed and uncompressed address can be generated. Those two addresses are completely different, with different balances. This exists because there are two ways of representing the public point. Early bitcoin software may not support the compressed addresses properly, but virtually all modern software will prefer the compressed addresses because of the smaller filesizes they create.  

generateFromHash [...] is identical to privateKeyToPublicKey [...], but both are maintained because they display the resulting output differently.  

The amount of time a vanity address takes to generate depends on both the number of and type of characters in it. For example, '1' characters typically take much longer to find than others, while 'A' characters are typcially easier to find.  
