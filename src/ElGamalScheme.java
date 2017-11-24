import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Security of the ElGamalScheme algorithm depends on the difficulty of computing discrete logs
 * in a large prime modulus
 *
 * - Theorem 1 : a in [Z/Z[p]] then a^(p-1) [p] = 1
 * - Theorem 2 : the order of an element split the order group
 */
public final class ElGamalScheme {
    public static BigInteger p;
    public static BigInteger g;
    public static BigInteger h;
    private static BigInteger x;

    public static ArrayList<BigInteger> sk;
    public static ArrayList<BigInteger> pk;

    public static BigInteger TWO = new BigInteger("2");

    /**
     * Generate the public key and the secret key for the ElGamalScheme encryption.
     *
     * @param n key size
     */
    public static void KeyGen(int n) {
        // (a) take a random prime p with getPrime() function. p = 2 * p' + 1 with prime(p') = true
        p = getPrime(n, 40, new Random());
        // (b) take a random element in [Z/Z[p]]* (p' order)
        g = randNum(p, new Random());
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamalScheme.TWO);

        while (!g.modPow(pPrime, p).equals(BigInteger.ONE)) {
            if (g.modPow(pPrime.multiply(ElGamalScheme.TWO), p).equals(BigInteger.ONE))
                g = g.modPow(TWO, p);
            else
                g = randNum(p, new Random());
        }

        // (c) take x random in [0, p' - 1]
        x = randNum(pPrime.subtract(BigInteger.ONE), new Random());
        h = g.modPow(x, p);

        // secret key is (p, x) and public key is (p, g, h)
        sk = new ArrayList<>(Arrays.asList(p, x));
        pk = new ArrayList<>(Arrays.asList(p, g, h));
        // [0] = pk, [1] = sk
    }

    public static ArrayList<BigInteger> getPublicKey()
    {
       return pk;
    }

    protected static ArrayList<BigInteger> getPrivateKey()
    {
        return sk;
    }

    /**
     * Encrypt ElGamalScheme
     * @param (pk) public key
     * @param message message
     */
    public static List<BigInteger> Encrypt(ArrayList<BigInteger>pk, BigInteger message) {
        BigInteger p = pk.get(0);
        BigInteger g = pk.get(1);
        BigInteger h = pk.get(2);
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamalScheme.TWO);
        // TODO [0, N -1] or [1, N-1] ?
        BigInteger r = randNum(pPrime, new Random());
        // encrypt couple (g^r, m * h^r)
        return new ArrayList<>(Arrays.asList(g.modPow(r, p), message.multiply(h.modPow(r, p))));
    }

    /**
     * Encrypt ElGamalScheme homomorphe
     *
     * @param message message
     */
    public static ArrayList<BigInteger> Encrypt_Homomorph(ArrayList<BigInteger> pk,BigInteger message) {

        BigInteger p = pk.get(0);
        BigInteger g = pk.get(1);
        BigInteger h = pk.get(2);

        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamalScheme.TWO);
        // TODO [0, N -1] or [1, N-1] ?
        BigInteger r = randNum(pPrime, new Random());

        // encrypt couple (g^r, h^r * g^m)
        BigInteger hr = h.modPow(r, p);
        BigInteger gm = g.modPow(message, p);

        return new ArrayList<>(Arrays.asList(g.modPow(r, p), hr.multiply(gm)));
    }

    /**
     * Decrypt ElGamalScheme
     *
     * @param (gr,mhr) (g^r, m * h^r)
     * @return the decrypted message
     */
    public static BigInteger Decrypt(ArrayList<BigInteger> sk,BigInteger gr, BigInteger mhr) {
        BigInteger p = sk.get(0);
        BigInteger x = sk.get(1);
        BigInteger hr = gr.modPow(x, p);
        return mhr.multiply(hr.modInverse(p)).mod(p);
    }

    /**
     * Decrypt ElGamalScheme homomorphe
     * Remarque : il faudra quand même faire une recherche exhaustive de log discret (g^m)
     * @param (gr,mhr) (g^r, h^r * g^m)
     * @return the decrypted message
     */
    public static BigInteger Decrypt_homomorphe(ArrayList<BigInteger> secretKey,BigInteger g, BigInteger gr, BigInteger hrgm) {
        BigInteger p = secretKey.get(0);
        BigInteger x = secretKey.get(1);

        BigInteger hr = gr.modPow(x,p);
        BigInteger gm = hrgm.multiply(hr.modInverse(p)).mod(p);

        BigInteger m = BigInteger.ONE;
        BigInteger gm_prime = g.modPow(m, p);

        long startTime = System.currentTimeMillis();
        while (!gm_prime.equals(gm)) {
            m = m.add(BigInteger.ONE);
            gm_prime = g.modPow(m, p);

            //Project specific constraint
            if(System.currentTimeMillis() > startTime + (5 * 1000))
            {
                return new BigInteger("0");
            }
        }
        return m;
    }

    /**
     * Return a prime p = 2 * p' + 1
     *
     * @param nb_bits   is the prime representation
     * @param certainty probability to find a prime integer
     * @param prg       random
     * @return p
     */
    public static BigInteger getPrime(int nb_bits, int certainty, Random prg) {
        BigInteger pPrime = new BigInteger(nb_bits, certainty, prg);
        // p = 2 * pPrime + 1
        BigInteger p = pPrime.multiply(TWO).add(BigInteger.ONE);

        while (!p.isProbablePrime(certainty)) {
            pPrime = new BigInteger(nb_bits, certainty, prg);
            p = pPrime.multiply(TWO).add(BigInteger.ONE);
        }
        return p;
    }

    /**
     * Return a random integer in [0, N - 1]
     *
     * @param N
     * @param prg
     * @return
     */
    public static BigInteger randNum(BigInteger N, Random prg) {
        return new BigInteger(N.bitLength() + 100, prg).mod(N);
    }

    public static void main(String[] args) {
        ElGamalScheme.KeyGen(200);

        System.out.println("Message : 12");
//        List<BigInteger> encrypt1 = ElGamalScheme.Encrypt_Homomorph(pk,new BigInteger("-1"));
//        List<BigInteger> encrypt2 = ElGamalScheme.Encrypt_Homomorph(pk,new BigInteger("0"));
//
//        BigInteger a = encrypt1.get(0).multiply(encrypt2.get(0));
//        BigInteger b = encrypt1.get(1).multiply(encrypt2.get(1));
//        System.out.println("Decrypted : " + ElGamalScheme.Decrypt_homomorphe(sk,g,a,b));

        List<BigInteger> encrypt1 = ElGamalScheme.Encrypt_Homomorph(pk,new BigInteger("-1"));
        List<BigInteger> encrypt2 = ElGamalScheme.Encrypt_Homomorph(pk,new BigInteger("0"));

        BigInteger a = encrypt1.get(0).multiply(encrypt2.get(0));
        BigInteger b = encrypt1.get(1).multiply(encrypt2.get(1));

        System.out.println("Decrypted : " + ElGamalScheme.Decrypt_homomorphe(sk,g,a,b));

//        System.out.println("Decrypted : " + ElGamalScheme.Decrypt(sk,encrypt1.get(0),encrypt1.get(1)));
    }
}
