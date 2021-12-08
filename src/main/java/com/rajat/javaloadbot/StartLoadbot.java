package com.rajat.javaloadbot;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

@Service
public class StartLoadbot {

	@Value("${KEYFILE}")
	private String keyFile;

	@Value("${RPC_SERVER}")
	private String RPC_SERVER;

	@Value("${MNEMONIC}")
	private String MNEMONIC;

	@Value("${PRIVATE_KEY}")
	private String PRIVATE_KEY;

	@Autowired
	SendTransaction sendTransaction;

	static Web3j web3 = Web3j.build(new HttpService("http://3.94.19.25:9545"));//RPC SERVER

	@Async
    public void startLoadbot() {
        try {
			// ExecutorService service = Executors.newFixedThreadPool(8);
            Credentials credentials = getSenderAccount();

			EthGetTransactionCount ethGetTransactionCount = web3
					.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			int j = nonce.intValue();
            
			// SendTransaction sendTransaction = new SendTransaction();
			for (int i = j; i < j+1; i++) {
				System.out.println("i="+i);
				// service.execute(new SendTransaction(i, credentials));
				sendTransaction.sendTransactionFunc(i, credentials);
			}

        } catch (Exception e) {
            System.out.println("Error in startLoadbot(): "+e);
        }
    }

	// Fetches the main sender accoutn that is pre-funded in geth
	public Credentials getSenderAccount() {
		try {
			String walletPassword = "Rajat123";
            String walletPath = keyFile;
            // Decrypt and open the wallet into a Credential object
            // Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletPath);
			// Credentials credentials = WalletUtils.loadBip39Credentials(walletPassword, "truck gallery select material claim elephant pear dog knock kitchen runway juice");
			String pk = PRIVATE_KEY;
            Credentials credentials = Credentials.create(pk);
			System.out.println("Sender Account address: " + credentials.getAddress());
			System.out.println("Sender Balance: "
					+ Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
							.send().getBalance().toString(), Unit.ETHER));
			return credentials;
		} catch (Exception e) {
			System.out.println("Error in getSenderAccount(): "+e);
			return null;
		}
	}

	public void preFundAccounts() {
        try {
			Credentials mainSenderCredentials = getSenderAccount();
			for (int i = 0; i < 1000; i++) {
				Credentials senderCredentials = getNewAccount(i);
				fundNewAccount(senderCredentials.getAddress(), i, mainSenderCredentials);
			}
        } catch (Exception e) {
            System.out.println("Error in startMultipleTransaction(): "+e);
        }
    }

    public void startMultipleTransaction() {
        try {
			Credentials[] senderCredentials = getMultipleSenders(1000);
			int nonce[] = new int[1000];
			for (int i = 0; i < nonce.length; i++) {
				nonce[i] = 0;
			}

			for (int i = 0; i < 10000; i++) {
				sendTransaction.sendMultipleTransaction(senderCredentials, i);
			}

        } catch (Exception e) {
            System.out.println("Error in startMultipleTransaction(): "+e);
        }
    }

	// creates multiple sender accounts and pre-funds them
	public Credentials[] getMultipleSenders(int n) {
		try {
			Credentials[] senderCredentials = new Credentials[n];
            // Credentials mainSenderCredentials = getSenderAccount();

			for (int i = 0; i < senderCredentials.length; i++) {
				senderCredentials[i] = getNewAccount(i);
				// fundNewAccount(senderCredentials[i].getAddress(), i, mainSenderCredentials);
			}
			return senderCredentials;
		} catch (Exception e) {
			System.out.println("Error in getSenderAccount(): "+e);
			return null;
		}
	}

	public Credentials getNewAccount(int i) {
		try {
			String password = "Rajat123";
            String mnemonic = "envelope direct allow creek endless detect mountain squeeze mass welcome virtual sample";

            //Derivation path wanted: // m/44'/60'/0'/i
            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, i};

            // Generate a BIP32 master keypair from the mnemonic phrase
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

            // Derived the key using the derivation path
            Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

            // Load the wallet for the derived key
            Credentials credentials = Credentials.create(derivedKeyPair);
			System.out.println("New Sender "+i+" : "+credentials.getAddress());
			return credentials;

		} catch (Exception e) {
			System.out.println("Error in getNewAccount(): "+e);
			return null;
		}
	}

	public void fundNewAccount(String recipientAddress, int i, Credentials mainCredentials) {
		try {
			// System.out.println("Thread: "+Thread.currentThread().getName());
			// Get the latest nonce of current account
			// EthGetTransactionCount ethGetTransactionCount = web3
			// 		.ethGetTransactionCount(mainCredentials.getAddress(), DefaultBlockParameterName.LATEST).send();
			// BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			BigInteger nonce = BigInteger.valueOf(i);
			System.out.println("Nonce: "+nonce.toString());
			
			// Value to transfer (in wei)
			String amountToBeSent="10000000000000000000000000000000000000000000000000000000000000";
			BigInteger value = new BigInteger(amountToBeSent);

			// Gas Parameter
			BigInteger gasLimit = BigInteger.valueOf(21000);
			BigInteger gasPrice = Convert.toWei("1", Unit.GWEI).toBigInteger();

			// Prepare the rawTransaction
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit,
					recipientAddress, value);

			long chainId = 1212;
			// Sign the transaction
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, mainCredentials);
			String hexValue = Numeric.toHexString(signedMessage);

			// Send transaction
			EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
			String transactionHash = ethSendTransaction.getTransactionHash();
			System.out.println("transactionHash "+i+" : "+ transactionHash);

		} catch (Exception e) {
			System.out.println("Error in fundNewAccount(): "+e);
		}
	}

	@Async
	public void test1() throws Exception {
		// ExecutorService service = Executors.newFixedThreadPool(8);
		for (int i = 0; i < 100; i++) {
			System.out.println("i= "+i+"\tThread: Thread: "+Thread.currentThread().getName());
			sendTransaction.test2(i);
			// service.execute(new SendTransaction());
		}
	}

}
