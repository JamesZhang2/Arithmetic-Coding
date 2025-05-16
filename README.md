# Arithmetic Coding

This is a proof-of-concept Java implementation of arithmetic coding (and Huffman coding for benchmarking). This project is submitted as my final project to ORIE 4742.

## Conventions

- The encoders can only encode ASCII characters (0-127) and uses 128 as the end-of-file (aka stop) symbol.
- Decoders must be constructed using the same probability model (for AC) or codes/Huffman tree (for Huffman coding) as the encoder. Otherwise, the output would not make sense (and is not guaranteed to terminate since the end-of-file symbol may be encoded differently).
- A new instance of a probability model must be created for each encoder and decoder since the probability models can have internal states depending on the text it has read.
- For arithmetic coding, the encoded bytes represent a decimal fraction 0.(bytes) with infinitely many zeros padded at the end.
- Only basic I/O functionalities are implemented: Encoding or decoding files currently reads the entire file into memory and then perform the operations. Therefore, attempting to encode or decode large files may result in an out-of-memory error.

## Arithmetic coding

The encoder and decoder takes a probability model that predicts the probability of each character given the text it has seen so far, $P(x_n = c | x_1, \ldots, x_{n - 1})$.

The fixed probability model always assigns fixed probabilities $p_c$ to each character $c$, regardless of what it has read.

The Dirichlet model is an adaptive model that counts the frequency $f_c$ of each character in the previous context and then assigns the following probability:

$$P(x_n = c | x_1, \ldots, x_{n - 1}) = \frac{f_c + \alpha}{\sum_i (f_i + \alpha)}$$

where $\alpha$ is a hyperparameter. Note that a smaller $\alpha$ means that the model is more responsive to the context, assigning greater probabilities to the symbols that it has seen.

The encoder and decoder supports renormalization and underflow handling. For encoding, we keep track of the possible doubles that can be used to encode what we've seen so far as a range `[low, high)`. If the range of possible doubles falls entirely in $[0, \frac{1}{2})$ or $[\frac{1}{2}, 1)$, we output a bit and renormalize the interval, scaling it up by 2. If the range of possible doubles falls entirely in $[\frac{1}{4}, \frac{3}{4})$, we scale up the interval and remember that we had an underflow condition. When the interval finally falls inside $[0, \frac{1}{2})$ or $[\frac{1}{2}, 1)$, we output a bit and then immediately output the opposite bit $t$ times, where $t$ is the number of times that the underflow condition happened. We then reset the number of underflow conditions to 0.

For decoding, we also keep track of a range `[low, high)` as well as a truncated version of the encoded bitstring, `encoded`. As more characters are decoded, we bring in more and more bits from the encoded bitstring. The binary fraction represented by the bitstring is therefore in the range `[encoded, encoded + LSB brought in)`, where the lower bound is what the double would be if all the later bits are 0 and the upper bound is what the double would be if all the later bits are 1. If the encoded bitstring range falls entirely inside the range `[low, high)`, we can decode a character and shrink the range `[low, high)` to be the range for that character within the original interval.

If `[low, high)` falls entirely in $[0, \frac{1}{2})$ or $[\frac{1}{2}, 1)$ or $[\frac{1}{4}, \frac{3}{4})$, we renormalize by scaling up the range by 2 (centered at $\frac{1}{4}, \frac{3}{4}, \frac{1}{2}$ respectively), and scaling `encoded` accordingly, then bring in a new bit from the encoded bitstring. We keep decoding until the end-of-file symbol has been decoded.

## Huffman coding

## References

- David J.C. MacKay's book *Information Theory, Inference, and Learning Algorithms*
- Wikipedia page on arithmetic coding: https://en.wikipedia.org/wiki/Arithmetic_coding
- Wikipedia page on Huffman coding: https://en.wikipedia.org/wiki/Huffman_coding
- https://www.youtube.com/watch?v=RFWJM8JMXBs
- https://michaeldipperstein.github.io/arithmetic.html
- I asked ChatGPT a few high-level conceptual questions and to look up documentation, but didn't use it to generate large chunks of code.
- I used Alice's Adventures in Wonderland as a sample text. This is found in The Project Gutenberg: https://www.gutenberg.org/ebooks/11. I replaced non-ASCII characters with ASCII characters.
- The 10,000 English words for benchmarking are from https://www.mit.edu/~ecprice/wordlist.10000.
