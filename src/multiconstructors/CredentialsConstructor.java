package multiconstructors;

import iofiles.Credentials;

public final class CredentialsConstructor {
    /**
     * Method creates a copy of a Credentials object identical to a given Credentials object
     * @param credentials credentials to copy
     * @return copyCredentials
     */
    public static Credentials createCred(final Credentials credentials) {
        Credentials copyCredentials = new Credentials();
        copyCredentials.setName(credentials.getName());
        copyCredentials.setPassword(credentials.getPassword());
        copyCredentials.setAccountType(credentials.getAccountType());
        copyCredentials.setCountry(credentials.getCountry());
        copyCredentials.setCountry(credentials.getCountry());
        copyCredentials.setBalance(credentials.getBalance());
        return copyCredentials;
    }

    private CredentialsConstructor() {

    }
}
