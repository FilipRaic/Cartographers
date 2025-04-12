package hr.tvz.cartographers.shared.jndi;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Map;

public class InitialDirContextCloseable extends InitialDirContext implements AutoCloseable {
    public InitialDirContextCloseable(Map<String, String> environment) throws NamingException {
        super(new Hashtable<>(environment));
    }
}
