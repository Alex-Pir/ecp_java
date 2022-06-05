package ECP;

public class GOST {

	private static final String C = "ff00ffff000000ffff0000ff00ffff0000ff00ff00ff00ffff00ff00ff00ff00";
	private final int[] arrC;
	private int[] U;
	private int[] V;
	private int[] W;
	private int sizeArrays;
	
	public GOST(int[] message, int[] H)
	{
		int index = 0;
		sizeArrays = C.length() >> 1;
		U = new int[sizeArrays];
		V = new int[sizeArrays];
		W = new int[sizeArrays];
		arrC = new int[sizeArrays];
		U = H.clone();
		V = message.clone();

		for (int i = 0; i < C.length(); i += 2)
		{
			arrC[index++] = Integer.parseInt(C.substring(i, i + 2), 16);
		}
	}
	
	public int[] genKey1()
	{
		for (int i = 0; i < sizeArrays; i++)
			W[i] = U[i] ^ V[i];
		return mix(W);
	}
	
	public int[] genKey2()
	{
		U = divideIntoQuarters(U);
		
		V = divideIntoQuarters(V);
		V = divideIntoQuarters(V);
		for (int i = 0; i < sizeArrays; i++)
			W[i] = U[i] ^ V[i];
		return mix(W);
	}
	
	public int[] genKey3()
	{
		U = divideIntoQuarters(U);
		for (int i = 0; i < sizeArrays; i++)
			U[i] = U[i] ^ arrC[i];
		
		V = divideIntoQuarters(V);
		V = divideIntoQuarters(V);
		
		for (int i = 0; i < sizeArrays; i++)
			W[i] = U[i] ^ V[i];
		
		return mix(W);
	}
	
	public int[] genKey4()
	{
		U = divideIntoQuarters(U);
		
		V = divideIntoQuarters(V);
		V = divideIntoQuarters(V);
		
		for (int i = 0; i < sizeArrays; i++)
			W[i] = U[i] ^ V[i];
		
		return mix(W);
	}
	
	private int[] mix(int[] key)
	{
		int index = 0;
		int size = key.length;
		int[] resultKey = new int[size];
		for (int i = 0; index < size; i++)
		{
			for (int j = i; j < size; j += 8)
			{
				resultKey[index++] = key[j];
			}
		}
		return resultKey;
	}
	
	private int[] doubleXOR(int[] arr1, int[] arr2)
	{
		int size = arr1.length;
		int[] resultArr = new int[size * 4];
		
		for (int i = 0; i < size; i++)
		{
			resultArr[i] = arr1[i] ^ arr2[i];
		}
		
		return resultArr;
	}	
	
	private int[] divideIntoQuarters(int[] arr)
	{
		int sizeArrays = arr.length;
		int index = 0;
		int sizeFour = sizeArrays >> 2;
		int[] x1 = new int[sizeFour];
		int[] x2 = new int[sizeFour];
		int[] x3 = new int[sizeFour];
		int[] x4 = new int[sizeFour];
		
		index = 0;
		for (int i = 0; i < sizeArrays; i++)
		{
			x1[index] = arr[i];
			x2[index] = arr[i + 8];
			x3[index] = arr[i + 16];
			x4[index] = arr[i + 24];
			index++;
			if (index == 8)
				break;
		}
		
		arr = doubleXOR(x3, x4);
		
		index = 0;
		for (int i = sizeFour; i < sizeArrays; i++)
		{
			if (i < 16)
				arr[i] = x1[index++];
			else if (i >= 16 && i < 24)
				arr[i] = x2[index++];
			else if (i >= 24 && i < 32)
				arr[i] = x3[index++];
			
			if (index % 8 == 0)
				index = 0;
		}
	
		
		return arr;
	}
	
	
}
