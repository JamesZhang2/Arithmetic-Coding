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

The Dirichlet model is an adaptive model that counts the frequency $f_c$ of each character in the context it has seen and then assigns the following probability:

$$P(x_n = c | x_1, \ldots, x_{n - 1}) = \frac{f_c + \alpha}{\sum_i (f_i + \alpha)}$$

where $\alpha$ is a hyperparameter. Note that a smaller $\alpha$ means that the model is more responsive to the context, assigning greater probabilities to the symbols that it has seen.

The Bigram Dirichlet model is an adaptive model which uses a Dirichlet model for each character. It counts the frequency $f_{p, c}$ of each character c given that the previous character is p in the context it has seen. It then assigns the following probability:

$$P(x_n = c | x_1, \ldots, x_{n - 2}, x_{n - 1} = p) = \frac{f_{p, c} + \alpha}{\sum_i (f_{p, i} + \alpha)}$$

This aims to take advantage of the fact that in English and other languages, the letters in the text are not independent of each other. For example, in English, the next letter after q is almost always u.

The encoder and decoder supports renormalization and underflow handling. For encoding, we keep track of the possible doubles that can be used to encode what we've seen so far as a range `[low, high)`. If the range of possible doubles falls entirely in $[0, \frac{1}{2})$ or $[\frac{1}{2}, 1)$, we output a bit and renormalize the interval, scaling it up by 2. If the range of possible doubles falls entirely in $[\frac{1}{4}, \frac{3}{4})$, we scale up the interval and remember that we had an underflow condition. When the interval finally falls inside $[0, \frac{1}{2})$ or $[\frac{1}{2}, 1)$, we output a bit and then immediately output the opposite bit $t$ times, where $t$ is the number of times that the underflow condition happened. We then reset the number of underflow conditions to 0.

For decoding, we also keep track of a range `[low, high)` as well as a truncated version of the encoded bitstring, `encoded`. As more characters are decoded, we bring in more and more bits from the encoded bitstring. The binary fraction represented by the bitstring is therefore in the range `[encoded, encoded + LSB brought in)`, where the lower bound is what the double would be if all the later bits are 0 and the upper bound is what the double would be if all the later bits are 1. If the encoded bitstring range falls entirely inside the range `[low, high)`, we can decode a character and shrink the range `[low, high)` to be the range for that character within the original interval.

If `[low, high)` falls entirely in $[0, \frac{1}{2})$ or $[\frac{1}{2}, 1)$ or $[\frac{1}{4}, \frac{3}{4})$, we renormalize by scaling up the range by 2 (centered at $\frac{1}{4}, \frac{3}{4}, \frac{1}{2}$ respectively), and scaling `encoded` accordingly, then bring in a new bit from the encoded bitstring. We keep decoding until the end-of-file symbol has been decoded.

## Huffman coding

Huffman coding is a symbol code that assigns a codeword for each symbol. By our convention, the symbols are a subset of 0 to 128, inclusive, where 128 stands for the end-of-file symbol. We use a greedy algorithm to decide which codewords to assign to each symbol: we take the two least frequent symbols and assign them the longest codewords, where the last bit of the codeword is 0 and 1, respectively. We then merge the two symbols into one and repeat until there is just one symbol left.

The implementation uses a priority queue and a union-find data structure to keep track of which symbols have been merged. We construct the Huffman tree using the greedy algorithm, where the leaves represent the symbols, and the path to reach to leaf represent the codeword: we add a 0 when moving left and a 1 when moving right. In addition, we keep a codeword table `codes` to look up the codewords for each symbol efficiently, which is convenient for encoding.

For encoding, we encode character by character using the codeword table, and then add the codeword for the end-of-file symbol at the end. For decoding, since Huffman coding is a prefix code, it is uniquely decodable, and we can decode by moving down the tree according to the encoded bitstring. When we reach a leaf, we output that symbol and start again from the root. We know that the decoding is complete when we reach the end-of-file symbol.

## Benchmarking results

For benchmarking, we used the following files. Some of the files are randomly generated while others are from the internet.

- `alice_full.txt`: A full copy of *Alice's Adventures in Wonderland by Lewis Carroll* obtained from The Project Gutenberg.
- `all_a.txt`: 1 million copies of the letter 'a'.
- `biased_random_50.txt`,`biased_random_99.txt`: A randomly-generated string of 1 million ASICC characters (0-127, inclusive), with 50% and 99% probability of generating an 'a' and equal probability to generate any other character. The characters are independent of each other.
- `english_words.txt`: A list of 10,000 English words separated by newlines, provided by MIT.
- `random_bits.txt`: A randomly-generated string of 1 million characters consisting of '1's and '0's, where each character is independent and generated with equal probability (0.5 probability for '1', 0.5 probability for '0').
- `unif_random.txt`: A randomly-generated string of 1 million ASICC characters (0-127, inclusive), with equal probability to generate any character. The characters are independent of each other.

In addition to the Huffman, AC with Fixed Probability (with equal probability for each character), AC with Dirichlet, and AC with Bigram Dirichlet encoding schemes, we also included the size of the original file and the size of the file compressed by `zip`, an industry-standard compression scheme.

Here are the benchmarking results:

|                        | alice_full | all_a   | biased_random_50 | biased_random_99 | english_words | random_bits | unif_random |
|------------------------| ---------- | ------- | ---------------- | ---------------- | ------------- | ----------- | ----------- |
| original               | 144580     | 1000000 | 1000000          | 1000000          | 75879         | 1000000     | 1000000     |
| bigram (alpha=0.01)    | 62729      | 9       | 565343           | 18880            | 31563         | 125015      | 873581      |
| bigram (alpha=1)       | 64879      | 232     | 557154           | 21625            | 32796         | 125428      | 863490      |
| bigram (alpha=100)     | 95295      | 12281   | 622121           | 35964            | 48858         | 146097      | 861912      |
| dirichlet (alpha=0.01) | 79329      | 8       | 552319           | 18676            | 34668         | 125009      | 858692      |
| dirichlet (alpha=1)    | 79383      | 232     | 552255           | 18699            | 34778         | 125230      | 858613      |
| dirichlet (alpha=100)  | 83996      | 12280   | 553894           | 28064            | 39193         | 137122      | 858790      |
| fixed_prob             | 123755     | 876405  | 869581           | 876278           | 57738         | 876405      | 862693      |
| huffman                | 79888      | 125001  | 553187           | 133572           | 34932         | 187411      | 860248      |
| zip                    | 52824      | 1154    | 652319           | 33462            | 27994         | 159144      | 876965      |

As percentage of original file size:

|                        | alice_full | all_a   | biased_random_50 | biased_random_99 | english_words | random_bits | unif_random |
|------------------------| ---------- | ------- | ---------------- | ---------------- | ------------- | ----------- | ----------- |
| original               | 100.00%    | 100.00% | 100.00%          | 100.00%          | 100.00%       | 100.00%     | 100.00%     |
| bigram (alpha=0.01)    | 43.39%     | 0.00%   | 56.53%           | 1.89%            | 41.60%        | 12.50%      | 87.36%      |
| bigram (alpha=1)       | 44.87%     | 0.02%   | 55.72%           | 2.16%            | 43.22%        | 12.54%      | 86.35%      |
| bigram (alpha=100)     | 65.91%     | 1.23%   | 62.21%           | 3.60%            | 64.39%        | 14.61%      | 86.19%      |
| dirichlet (alpha=0.01) | 54.87%     | 0.00%   | 55.23%           | 1.87%            | 45.69%        | 12.50%      | 85.87%      |
| dirichlet (alpha=1)    | 54.91%     | 0.02%   | 55.23%           | 1.87%            | 45.83%        | 12.52%      | 85.86%      |
| dirichlet (alpha=100)  | 58.10%     | 1.23%   | 55.39%           | 2.81%            | 51.65%        | 13.71%      | 85.88%      |
| fixed_prob             | 85.60%     | 87.64%  | 86.96%           | 87.63%           | 76.09%        | 87.64%      | 86.27%      |
| huffman                | 55.26%     | 12.50%  | 55.32%           | 13.36%           | 46.04%        | 18.74%      | 86.02%      |
| zip                    | 36.54%     | 0.12%   | 65.23%           | 3.35%            | 36.89%        | 15.91%      | 87.70%      |

## References

- David J.C. MacKay's book *Information Theory, Inference, and Learning Algorithms*
- Wikipedia page on arithmetic coding: https://en.wikipedia.org/wiki/Arithmetic_coding
- Wikipedia page on Huffman coding: https://en.wikipedia.org/wiki/Huffman_coding
- https://www.youtube.com/watch?v=RFWJM8JMXBs
- https://michaeldipperstein.github.io/arithmetic.html
- I asked ChatGPT a few high-level conceptual questions and to look up documentation, but didn't use it to generate large chunks of Java code.
- I used Alice's Adventures in Wonderland as a sample text. This is found in The Project Gutenberg: https://www.gutenberg.org/ebooks/11. I replaced non-ASCII characters with ASCII characters.
- The 10,000 English words for benchmarking are from https://www.mit.edu/~ecprice/wordlist.10000.
