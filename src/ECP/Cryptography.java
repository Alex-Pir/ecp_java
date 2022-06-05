package ECP;

import java.math.BigInteger;

public class Cryptography {
	
	public static final BigInteger MODUL = new BigInteger("2").pow(32);
	

	public String encrypt(int[] K, String text)
	{
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();
		StringBuffer sb4 = new StringBuffer();
		StringBuffer sb5 = new StringBuffer();
		StringBuffer sb6 = new StringBuffer();
		StringBuffer sb7 = new StringBuffer();
		StringBuffer sb8 = new StringBuffer();
		
		String strForB = text.substring(0, text.length() / 2);
		String strForA = text.substring(text.length() / 2);
	
		for (int i = 28; i < 32; i++)
		{
			sb1.append(Integer.toString(K[i], 16));
			if (sb1.length() % 2 != 0)
				sb1.insert(sb1.length() - 1, '0');
		
		}
		
		for (int i = 24; i < 28; i++)
		{
			sb2.append(Integer.toString(K[i], 16));
			if (sb2.length() % 2 != 0)
				sb2.insert(sb2.length() - 1, '0');
		}
			
		for (int i = 20; i < 24; i++)
		{
			sb3.append(Integer.toString(K[i], 16));
			if (sb3.length() % 2 != 0)
				sb3.insert(sb3.length() - 1, '0');
		}
			 
		for (int i = 16; i < 20; i++)
		{
			sb4.append(Integer.toString(K[i], 16));
			if (sb4.length() % 2 != 0)
				sb4.insert(sb4.length() - 1, '0');
		}
			
		for (int i = 12; i < 16; i++)
		{
			sb5.append(Integer.toString(K[i], 16));
			if (sb5.length() % 2 != 0)
				sb5.insert(sb5.length() - 1, '0');
		}
			
		for (int i = 8; i < 12; i++)
		{
			sb6.append(Integer.toString(K[i], 16));
			if (sb6.length() % 2 != 0)
				sb6.insert(sb6.length() - 1, '0');
		}
			
		for (int i = 4; i < 8; i++)
		{
			sb7.append(Integer.toString(K[i], 16));
			if (sb7.length() % 2 != 0)
				sb7.insert(sb7.length() - 1, '0');
		}
		
		for (int i = 0; i < 4; i++)
		{
			sb8.append(Integer.toString(K[i], 16));
			if (sb8.length() % 2 != 0)
				sb8.insert(sb8.length() - 1, '0');
		}
		
		
		BigInteger[] key = new BigInteger[8];
		
		key[0] = new BigInteger(sb1.toString(), 16);
		key[1] = new BigInteger(sb2.toString(), 16);
		key[2] = new BigInteger(sb3.toString(), 16);
		key[3] = new BigInteger(sb4.toString(), 16);
		key[4] = new BigInteger(sb5.toString(), 16);
		key[5] = new BigInteger(sb6.toString(), 16);
		key[6] = new BigInteger(sb7.toString(), 16);
		key[7] = new BigInteger(sb8.toString(), 16);
		
		return encrypt(key, strForA, strForB);
	}
	
	private String encrypt(BigInteger[] key, String strForA, String strForB)
	{
		int index = 0;
		BigInteger A = new BigInteger(strForA, 16);
		BigInteger B = new BigInteger(strForB, 16);
		BigInteger interA = BigInteger.ZERO;
		
		
		for (int i = 0; i < 32; i++)
		{
			if (i < 24)
				index = i % 8;
			else
				index = 7 - (i % 8);
			System.out.println("ЦИКЛ " + i);
			System.out.println("A = " + A.toString(16));
			System.out.println("B = " + B.toString(16));
			interA = multiply(A, key[index]);
			interA = B.xor(interA);
			
			B = A;
			A = interA;
		
		}
		
		String resultB = B.toString(16);
		String resultA = A.toString(16);
		
		while (resultB.length() % 8 != 0)
			resultB = 0 + resultB;
		while (resultA.length() % 8 != 0)
			resultA = 0 + resultA;
		
		return resultA + resultB;
	}
	
	private BigInteger multiply(BigInteger a, BigInteger b)
	{
	
		BigInteger sum = a.add(b).mod(MODUL);
		System.out.println("SUM = " + sum.toString(16));
		String sumString = sum.toString(16);
		String newSumString = "";
		
		
		while (sumString.length() < 8)
			sumString = 0 + sumString;
		
		int index = 0;
		for (int i = sumString.length() - 1; i >= 0; i--)
		{
			newSumString = Integer.toString(ReplacementTable.table[Integer.parseInt(sumString.substring(i, i + 1), 16)][index++], 16) + newSumString;

		}
		System.out.println("BS = " + newSumString);
		BigInteger result = new BigInteger(newSumString, 16);
		
		return shift(result, 11);
	}
	
	private BigInteger shift(BigInteger number, int count)
	{
		StringBuilder sb;
		String befShift = number.toString(16);
		
		sb = new StringBuilder(new BigInteger(befShift, 16).toString(2));
		
		while (sb.toString().length() < 32)
			sb.insert(0, '0');
		for (int i = 0; i < count; i++)
		{
			sb.append(sb.charAt(0));
			sb.deleteCharAt(0);
		}
		return new BigInteger(sb.toString(), 2);
	}
	
}
