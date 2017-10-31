package coop.magnesium.utils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by rsperoni on 05/05/17.
 */
public class SimpleKeyGenerator implements KeyGenerator {

    @Override
    public Key generateKey() {
        String keyString = "magnesium";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
        return key;
    }

}
