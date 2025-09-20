package com.appops.verifier;

import java.util.UUID;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

public class DomainVerifier {
  
    private static final String TXT_RECORD_PREFIX = "_appops-verification";

    /**
     * Verifies if a user's domain has the specific TXT record.
     * @param domain The user's domain to check (e.g., "customer-domain.com").
     * @param expectedToken The unique token we expect to find.
     * @return true if the token is found, false otherwise.
     */
    public static boolean verifyTxtRecord(String domain, String expectedToken) {
        String queryName = TXT_RECORD_PREFIX + "." + domain;
        System.out.println("Querying for TXT record at: " + queryName);

        try {
            Lookup lookup = new Lookup(queryName, Type.TXT);
            // Using Cloudflare's 1.1.1.1 resolver is often faster for fresh results
            Resolver resolver = new SimpleResolver("1.1.1.1"); 
            lookup.setResolver(resolver);
            lookup.setCache(null); // Disable local caching to get the absolute latest record

            Record[] records = lookup.run();
            if (lookup.getResult() != Lookup.SUCCESSFUL || records == null) {
                System.out.println("   -> No TXT record found or lookup failed.");
                return false;
            }

            for (Record rec : records) {
                TXTRecord txt = (TXTRecord) rec;
                for (String s : txt.getStrings()) {
                    System.out.println("   -> Found TXT value: \"" + s + "\"");
                    if (s.contains(expectedToken)) {
                        System.out.println("   -> ✅ Match found!");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during TXT lookup: " + e.getMessage());
        }

        System.out.println("   -> ❌ No matching token found.");
        return false;
    }

    /**
     * Generates a unique, secure token for each verification attempt.
     * @return A unique token string.
     */
    public static String generateVerificationToken() {
        return "vfy-" + UUID.randomUUID().toString();
    }
}