package ECP;

import java.math.BigInteger;

public class ECPManager {

	private static final BigInteger MODUL = new BigInteger("2").pow(256);
	
	public BigInteger hash(String message)
	{
		int length = message.length();
		int mSize = 0;
		int indexForM = 0;
		String messageForDivide = "";
		String messM = "";
		BigInteger Sum = new BigInteger("0");
		BigInteger L = new BigInteger("0");
		BigInteger H = new BigInteger("0");
		BigInteger[] m = null;
		
		if (length < 64)
		{
			String lengthString = Integer.toString(length, 16);
			L = L.add(new BigInteger(lengthString, 16).multiply(new BigInteger("4")));
			while (length < 64)
			{
				message = 0 + message;
				length++;
			}
		}
		if (length > 64)
		{
			messageForDivide = message.substring(0, length - 64);
			mSize = (length) / 64;
			m = new BigInteger[mSize + 3];
			for (int i = 0, j = 1; i < length - 64; i += 64, j++)
			{
				if ((i + 64) == messageForDivide.length())
				{
					messM = messageForDivide.substring(0, 64);
				}
				else if ((i + 64) > messageForDivide.length())
				{
					messM = messageForDivide.substring(i, messageForDivide.length());
		
				}
				else
				{
					
					messM = messageForDivide.substring(i, i + 64);
					
				}
				L = L.add(new BigInteger(Integer.toString(messM.length() * 4)));
				while (messM.length() < 64)
					messM = 0 + messM;
				m[j] = new BigInteger(messM, 16);
				indexForM = j;
				
			}
			message = message.substring(length - 64);
			m[0] = new BigInteger(message, 16);
		}
		else if (length == 64)
		{
			m = new BigInteger[3];
			m[0] = new BigInteger(message, 16);
			
		}
			
		if (m.length > 3 || L.compareTo(BigInteger.ZERO) == 0)
			L = L.add(new BigInteger("256"));
		
		int sizeArrays = 0;
		int[] K1;
		int[] K2;
		int[] K3;
		int[] K4;
		
		if (message.length() % 2 == 0)
			sizeArrays = message.length() >> 1;
		else
			sizeArrays = (message.length() >> 1) + 1;

		
		int[] hexArray = new int[sizeArrays];
		int[] h = new int[sizeArrays];
		
		int indexHex = 0;
		
		for (int i = 0; i < message.length(); i += 2)
		{
			hexArray[indexHex++] = Integer.parseInt(message.substring(i, i + 2), 16);
		}
		
		GOST gost = new GOST(hexArray, h);
		
		K1 = gost.genKey1();
		K2 = gost.genKey2();
		K3 = gost.genKey3();
		K4 = gost.genKey4();
		
		message = H.toString(16);
		while (message.length() < 64)
			message = 0 + message;
	
		String S4 = message.substring(0, 16);
		String S3 = message.substring(16, 32);
		String S2 = message.substring(32, 48);
		String S1 = message.substring(48, 64);
		
		Cryptography crypt = new Cryptography();
		String cryptoText = crypt.encrypt(K4, S4) + crypt.encrypt(K3, S3) + crypt.encrypt(K2, S2) + crypt.encrypt(K1, S1);
		
		
			for (int i = 0; i < m.length - 2; i++)
			{
				if (m[i] != null)
					Sum = Sum.add(m[i]).mod(MODUL);
			}
			
			m[++indexForM] = L;
			m[++indexForM] = Sum;
			
			for (int j = 0; j < m.length; j++)
			{			
				if (m[j] != null)
				{
					H = new BigInteger(mixFi((H.xor(new BigInteger(mixFi((m[j].xor(new BigInteger(mixFi(cryptoText, 12), 16))).toString(16), 1), 16))).toString(16), 61), 16);
					message = H.toString(16);
					while (message.length() < 64)
						message = 0 + message;
					indexHex = 0;
					hexArray = new int[sizeArrays];
					for (int i = 0; i < message.length(); i += 2)
					{
						h[indexHex++] = Integer.parseInt(message.substring(i, i + 2), 16);
					}
					
				indexHex = 0;
				if ((m.length - j) > 1)
				{
					if (m[j + 1] != null)
					{
						String mStr = m[j + 1].toString(16);
						while (mStr.length() < 64)
							mStr = 0 + mStr;
						for (int i = 0; i < mStr.length(); i += 2)
						{
							hexArray[indexHex++] = Integer.parseInt(mStr.substring(i, i + 2), 16);
						}
					}
				}
				
				gost = new GOST(hexArray, h);
				
				K1 = gost.genKey1();
				K2 = gost.genKey2();
				K3 = gost.genKey3();
				K4 = gost.genKey4();
				
				S4 = message.substring(0, 16);
				S3 = message.substring(16, 32);
				S2 = message.substring(32, 48);
				S1 = message.substring(48, 64);
				
				cryptoText = crypt.encrypt(K4, S4) + crypt.encrypt(K3, S3) + crypt.encrypt(K2, S2) + crypt.encrypt(K1, S1);
				
			}
			}
		
		
		return H;
	}
	
	private String mixFi(String cryptoText, int valueForCicle)
	{
		
		while (cryptoText.length() % 64 != 0)
			cryptoText = 0 + cryptoText;
		int size = cryptoText.length() >> 4;
		int index = 15;
		String resultFi = "";
		BigInteger resultXOR;
		BigInteger[] cryptoTextArr = new BigInteger[16];
		
		for (int i = 0; i < cryptoText.length(); i += size)
		{
			cryptoTextArr[index--] = new BigInteger(cryptoText.substring(i, i + size), 16);
		}
		
		for (int i = 0; i < valueForCicle; i++)
		{
			resultXOR = new BigInteger("0");
			resultFi = "";
			
			for (int j = 0; j < 4; j++)
			{
				resultXOR = resultXOR.xor(cryptoTextArr[j]);	
			}
			resultXOR = resultXOR.xor(cryptoTextArr[12]);	
			resultXOR = resultXOR.xor(cryptoTextArr[15]);
			
			int value = size - resultXOR.toString(16).length();
			
			while (value > 0)
			{
				resultFi += 0;
				value--;
			}
			resultFi += resultXOR.toString(16);
			
			for (int j = 15; j >= 1; j--)
			{
				value = size - cryptoTextArr[j].toString(16).length();
				
				while (value > 0)
				{
					resultFi += 0;
					value--;
				}
				
				resultFi += cryptoTextArr[j].toString(16);
				
					
			}
			index = 15;
			
			for (int j = 0; j < resultFi.length(); j += size)
			{
				cryptoTextArr[index--] = new BigInteger(resultFi.substring(j, j + size), 16);
			}
			
			
		}
		
		return resultFi;
	}
	
}
