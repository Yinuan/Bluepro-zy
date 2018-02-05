package com.klcxkj.zqxy.utils;

import java.security.Provider;

/**
 * autor:OFFICE-ADMIN
 * time:2017/11/9
 * email:yinjuan@klcxkj.com
 * description:
 */

public final class CryptoProvider extends Provider {

    protected CryptoProvider() {
        super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
        put("SecureRandom.SHA1PRNG",
                "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
        put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
    }
}
