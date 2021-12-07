package com.rajat.javaloadbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

@Service
public class GetAccount {

    @Value("${KEYFILE}")
	private String keyFile;

    @Value("${RPC_SERVER}")
	private String RPC_SERVER;

	@Value("${MNEMONIC}")
	private String MNEMONIC;

    @Value("${PRIVATE_KEY}")
	private String PRIVATE_KEY;
    
    public Credentials getAccount() {
        try {
            String password = "Rajat123";
            String mnemonic = MNEMONIC;

            // //Derivation path wanted: // m/44'/60'/0'/0
            // int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0,0};

            // // Generate a BIP32 master keypair from the mnemonic phrase
            // Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

            // // Derived the key using the derivation path
            // Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);

            // // Load the wallet for the derived key
            // Credentials credentials = Credentials.create(derivedKeyPair);
            String walletPassword = "Rajat123";
            String walletPath = keyFile;
            // Decrypt and open the wallet into a Credential object
            // Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletPath);
            // Credentials credentials = WalletUtils.loadBip39Credentials(walletPassword, "truck gallery select material claim elephant pear dog knock kitchen runway juice");
            String pk = PRIVATE_KEY;
            Credentials credentials = Credentials.create(pk);
            System.out.println("Account: "+credentials.getAddress());
            
            Web3j web3 = Web3j.build(new HttpService(RPC_SERVER));//RPC SERVER
            EthGetBalance ethGetBalance = web3
					.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
					.sendAsync().get();

			System.out.println("Balance of Account "+credentials.getAddress()+": "
			+ ethGetBalance.getBalance());
			
			System.out.println("Balance in Ether format: "
			+Convert.fromWei(web3.ethGetBalance(credentials.getAddress(),
			DefaultBlockParameterName.LATEST).send().getBalance().toString(),Unit.ETHER));
            return credentials;

        } catch (Exception e) {
            System.out.println("Error in getAccount(): "+e);
            return null;
        }
    }
    
}
