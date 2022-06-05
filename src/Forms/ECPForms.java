package Forms;

import java.math.BigInteger;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ECP.ECPManager;

public class ECPForms extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigInteger y;
	private BigInteger a;
	private JTextField message;
	private JTextField hash;
	private JTextField p;
	private JTextField q;
	private JTextField signature;
	private JTextField key;
	private JTextField resultHash;
	private BigInteger[] ecp;
	public ECPForms()
	{
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initComponents();
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initComponents()
	{
		message = new JTextField(64);
		hash = new JTextField(64);
		p = new JTextField(64);
		q = new JTextField(64);
		signature = new JTextField(64);
		key = new JTextField(64);
		resultHash = new JTextField(64);
		
		JButton consider = new JButton("Хэш");
		JButton ecpButton = new JButton("ЭЦП");
		JButton check = new JButton("Проверка");
		
		JLabel messageLabel = new JLabel("Message: ");
		JLabel hashLabel = new JLabel("Hash: ");
		JLabel pLabel = new JLabel("p: ");
		JLabel qLabel = new JLabel("q: ");
		JLabel signLabel = new JLabel("Signature: ");
		JLabel keyLabel = new JLabel("PK");
		JLabel resultLabel = new JLabel("HashR: ");
		
		messageLabel.setPreferredSize(signLabel.getPreferredSize());
		hashLabel.setPreferredSize(signLabel.getPreferredSize());
		pLabel.setPreferredSize(signLabel.getPreferredSize());
		qLabel.setPreferredSize(signLabel.getPreferredSize());
		keyLabel.setPreferredSize(signLabel.getPreferredSize());
		resultLabel.setPreferredSize(signLabel.getPreferredSize());
		
		Box messageBox = Box.createHorizontalBox();
		messageBox.add(messageLabel);
		messageBox.add(Box.createHorizontalStrut(6));
		messageBox.add(message);
		
		Box hashBox = Box.createHorizontalBox();
		hashBox.add(hashLabel);
		hashBox.add(Box.createHorizontalStrut(6));
		hashBox.add(hash);
		
		Box pBox = Box.createHorizontalBox();
		pBox.add(pLabel);
		pBox.add(Box.createHorizontalStrut(6));
		pBox.add(p);
		
		Box qBox = Box.createHorizontalBox();
		qBox.add(qLabel);
		qBox.add(Box.createHorizontalStrut(6));
		qBox.add(q);
		
		Box signBox = Box.createHorizontalBox();
		signBox.add(signLabel);
		signBox.add(Box.createHorizontalStrut(6));
		signBox.add(signature);
		
		Box keyBox = Box.createHorizontalBox();
		keyBox.add(keyLabel);
		keyBox.add(Box.createHorizontalStrut(6));
		keyBox.add(key);
		
		Box resultBox = Box.createHorizontalBox();
		resultBox.add(resultLabel);
		resultBox.add(Box.createHorizontalStrut(6));
		resultBox.add(resultHash);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(consider);
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(ecpButton);
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(check);
		
		Box mainBox = Box.createVerticalBox();
		mainBox.setBorder(new EmptyBorder(12, 12, 12, 12));
		mainBox.add(messageBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(hashBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(pBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(qBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(signBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(keyBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(resultBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(buttonBox);
		
		add(mainBox);
		
		consider.addActionListener((event) -> hashConsider(hash));
		ecpButton.addActionListener((event) -> ecpConsider());
		check.addActionListener((event) -> getResult(ecp[0], ecp[1], ecp[2], ecp[3]));
	}
	
	private void hashConsider(JTextField hField)
	{
		if (!message.getText().isEmpty())
		{
			String messageString = "";
			byte[] text = message.getText().getBytes();
			for (int i = text.length - 1; i >= 0; i--)
			{
				System.out.println(text[i]);
				messageString += Integer.toString((text[i] & 0xff), 16);
			}
			
			String resultHash = new ECPManager().hash(messageString).toString(16);
			while (resultHash.length() < 64)
				resultHash = 0 + resultHash;
			
			hField.setText(resultHash);
		}
	}
	
	private BigInteger[] ecpConsider()
	{
		String hashString = hash.getText();
		String pString = p.getText();
		String qString = q.getText();
		ecp = new BigInteger[4];
		if (!hashString.isEmpty())
		{
			if (!pString.isEmpty() && !qString.isEmpty())
			{
				BigInteger bigP;	
				BigInteger bigQ;
				try
				{
					bigP = new BigInteger(pString);
				}
				catch(Exception ex)
				{
					bigP = new BigInteger(pString, 16);
				}
				try
				{
					bigQ = new BigInteger(qString);
				}
				catch(Exception ex)
				{
					bigQ = new BigInteger(qString, 16);
				}
				
				BigInteger[] sign = genRS(new BigInteger(hashString, 16), bigP, bigQ);
				String r = sign[0].toString(16);
				String s = sign[1].toString(16);
				while (r.length() < 64)
					r = 0 + r;
				while (s.length() < 64)
					s = 0 + s;
				signature.setText(r + s);
				
				ecp[0] = sign[0];
				ecp[1] = sign[1];
				ecp[2] = bigP;
				ecp[3] = bigQ;
				
			}
		}
		
		return ecp;
	}
	
	private void getResult(BigInteger r, BigInteger s, BigInteger m, BigInteger n)
	{
		
		hashConsider(resultHash);
		BigInteger result = genResultECP(new BigInteger(resultHash.getText(), 16), r, s, m, n);
		if (result.compareTo(r) == 0)
			JOptionPane.showMessageDialog(this, "Проверка прошла успешно!");
		else
			JOptionPane.showMessageDialog(this, "Проверка завершилась ошибкой!", "Error" , JOptionPane.ERROR_MESSAGE);
		
	}
	
	
	private BigInteger randomBigInteger(BigInteger big)
	{
		Random rand = new Random();
		int maxBitLength = big.bitLength();
		
		BigInteger aRandomBigInt;
		do
		{
			aRandomBigInt = new BigInteger(maxBitLength, rand);
		}
		while (aRandomBigInt.compareTo(big) > 0);
		
		return aRandomBigInt;
	}
	
	private BigInteger genK(BigInteger n)
	{
		return randomBigInteger(n);
	}
	
	private BigInteger genA(BigInteger m, BigInteger n)
	{
		BigInteger maxD = m.subtract(BigInteger.ONE);
		BigInteger a = BigInteger.ONE;
		
		do
		{
			a = randomBigInteger(maxD).modPow((m.subtract(BigInteger.ONE)).divide(n), m);
		}
		while (a.modPow(n, m).compareTo(BigInteger.ONE) != 0 && a.compareTo(BigInteger.ONE) == 0);
		
		return a;
	}
	
	private BigInteger genR(BigInteger a, BigInteger k,  BigInteger m, BigInteger n)
	{
		BigInteger r = BigInteger.ZERO;
		BigInteger r1 = BigInteger.ZERO;
	
		r = a.modPow(k, m);
		r1 = r.mod(n);
		
		return r1;
	}
	
	private BigInteger genX(BigInteger n)
	{
		return randomBigInteger(n);
	}
	
	private BigInteger[] genRS(BigInteger h, BigInteger m, BigInteger n)
	{
		BigInteger s = BigInteger.ZERO;
		BigInteger r = BigInteger.ZERO;
		a = BigInteger.ZERO;
		BigInteger[] sign = new BigInteger[2];
		do
		{
			BigInteger k;
			do 
			{
				a = genA(m, n);
				k = genK(n);
				r = genR(a, k, m, n);
			}
			while (r.compareTo(BigInteger.ZERO) == 0);
			BigInteger x = genX(n);
			key.setText(x.toString(16));
			y = a.modPow(x, m);
			s = (x.multiply(r).add(k.multiply(h))).mod(n);
			
		}
		while (s.compareTo(BigInteger.ZERO) == 0);
		sign[0] = r;
		sign[1] = s;
		return sign;
	}
	
	//функции проверки
		

	private BigInteger genResultECP(BigInteger h, BigInteger r, BigInteger s, BigInteger m, BigInteger n)
	{
			
		BigInteger w = h.modInverse(n);
		BigInteger u1 = (w.multiply(s)).mod(n);
		BigInteger u2 = ((n.subtract(r)).multiply(w)).mod(n);
		
		BigInteger v = (a.modPow(u1, m).multiply((y.modPow(u2, m)))).mod(m).mod(n);
		return v;
	}

}

