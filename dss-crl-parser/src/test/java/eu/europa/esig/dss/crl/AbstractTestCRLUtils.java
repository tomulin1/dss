/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.crl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.junit.Test;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;

public abstract class AbstractTestCRLUtils extends AbstractCRLParserTestUtils {

	private static final CertificateFactory certificateFactory;

	static {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			throw new DSSException("Platform does not support X509 certificate", e);
		}
	}

	@Test
	public void isValidCRL() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/belgium2.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/belgiumrs2.crt")) {
			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertNotNull(validCRL.getIssuerToken());
			assertNotNull(validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertTrue(validCRL.isIssuerX509PrincipalMatches());
			assertTrue(validCRL.isSignatureIntact());
			assertTrue(validCRL.isValid());
			assertTrue(validCRL.isCrlSignKeyUsage());
			assertFalse(validCRL.isUnknownCriticalExtension());
			assertEquals(certificateToken, validCRL.getIssuerToken());
			assertNull(validCRL.getSignatureInvalidityReason());
			assertNull(validCRL.getUrl());
		}
	}
	
	@Test
	public void testUA() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/CA-5358AA45-Full.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/CA-Justice-ECDSA-261217.cer")) {
			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertNotNull(validCRL.getIssuerToken());
			assertEquals(SignatureAlgorithm.ECDSA_SHA256,validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertTrue(validCRL.isIssuerX509PrincipalMatches());
			assertTrue(validCRL.isSignatureIntact());
			assertTrue(validCRL.isValid());
			assertTrue(validCRL.isCrlSignKeyUsage());
			assertFalse(validCRL.isUnknownCriticalExtension());
			assertEquals(certificateToken, validCRL.getIssuerToken());
			assertNull(validCRL.getSignatureInvalidityReason());
			assertNull(validCRL.getUrl());
		}
	}

	@Test
	public void isValidPEMCRL() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/belgium2.pem.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/belgiumrs2.crt")) {
			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertNotNull(validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertTrue(validCRL.isIssuerX509PrincipalMatches());
			assertTrue(validCRL.isSignatureIntact());
			assertNotNull(validCRL.getIssuerToken());
			assertTrue(validCRL.isValid());
			assertTrue(validCRL.isCrlSignKeyUsage());
			assertFalse(validCRL.isUnknownCriticalExtension());
			assertEquals(certificateToken, validCRL.getIssuerToken());
			assertNull(validCRL.getSignatureInvalidityReason());
			assertNull(validCRL.getUrl());
		}
	}

	@Test
	public void isValidCRLWrongCertificate() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/belgium2.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {
			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertNull(validCRL.getIssuerToken());
			assertNotNull(validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertFalse(validCRL.isIssuerX509PrincipalMatches());
			assertFalse(validCRL.isSignatureIntact());
			assertFalse(validCRL.isValid());
			assertNotNull(validCRL.getSignatureInvalidityReason());
		}
	}

	@Test
	public void testLTGRCA() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/LTGRCA.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertEquals(SignatureAlgorithm.RSA_SHA1, validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertNull(validCRL.getExpiredCertsOnCRL());

			assertFalse(validCRL.isIssuerX509PrincipalMatches());
			assertFalse(validCRL.isSignatureIntact());
			assertFalse(validCRL.isValid());
		}
	}

	@Test
	public void testGetExpiredCertsOnCRL() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/crl_with_expiredCertsOnCRL_extension.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertEquals(SignatureAlgorithm.RSA_SHA256, validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertNotNull(validCRL.getExpiredCertsOnCRL());
			assertNotNull(validCRL.getUrl());

			assertFalse(validCRL.isIssuerX509PrincipalMatches());
			assertFalse(validCRL.isSignatureIntact());
			assertFalse(validCRL.isValid());
		}
	}

	@Test
	public void testGetExpiredCertsOnCRLUTCTime() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/crl-expiredCertsOnCRL-UTCTime.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertEquals(SignatureAlgorithm.RSA_SHA256, validCRL.getSignatureAlgorithm());
			assertNotNull(validCRL.getThisUpdate());
			assertNotNull(validCRL.getNextUpdate());
			assertNull(validCRL.getExpiredCertsOnCRL()); // Ignored
			assertNull(validCRL.getUrl());

			assertFalse(validCRL.isIssuerX509PrincipalMatches());
			assertFalse(validCRL.isSignatureIntact());
			assertFalse(validCRL.isValid());
		}
	}

	@Test
	public void retrieveRevocation() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/http___crl.globalsign.com_gs_gspersonalsign2sha2g2.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validity = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			BigInteger serialNumber = new BigInteger("288350169419475868349393253038503091234");
			X509CRLEntry entry = CRLUtils.getRevocationInfo(validity, serialNumber);
			assertNotNull(entry);
			assertNotNull(entry.getRevocationDate());
			assertNull(entry.getRevocationReason());
			assertNotNull(entry.getSerialNumber());
			assertEquals(serialNumber, entry.getSerialNumber());

			serialNumber = new BigInteger("288350169419475868349393264025423631520");
			entry = CRLUtils.getRevocationInfo(validity, serialNumber);
			assertNotNull(entry);
			assertNotNull(entry.getRevocationDate());
			assertNull(entry.getRevocationReason());
			assertNotNull(entry.getSerialNumber());
			assertEquals(serialNumber, entry.getSerialNumber());

			serialNumber = new BigInteger("111111111111111111111111111");
			entry = CRLUtils.getRevocationInfo(validity, serialNumber);
			assertNull(entry);
		}
	}

	@Test
	public void testARLFile() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/notaires2020.arl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validity = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validity);
			assertNotNull(validity.getThisUpdate());
			assertNotNull(validity.getNextUpdate());
			assertNotNull(validity.getSignatureAlgorithm());
		}
	}

	@Test(expected = Exception.class)
	public void notACRL() throws Exception {
		try (InputStream is = new ByteArrayInputStream(new byte[] { 1, 2, 3 });
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLUtils.buildCRLValidity(crlBinary, certificateToken);
		}
	}

	// @Ignore
	@Test
	public void testHugeCRL() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/esteid2011.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/ESTEID-SK_2011.der.crt")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validity = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertEquals(SignatureAlgorithm.RSA_SHA256, validity.getSignatureAlgorithm());
			assertNotNull(validity.getThisUpdate());
			assertNotNull(validity.getNextUpdate());
			assertNull(validity.getExpiredCertsOnCRL());
			assertNotNull(validity.getIssuerToken());
			assertTrue(validity.isValid());

			BigInteger serialNumber = new BigInteger("1111111111111111111");
			assertNull(CRLUtils.getRevocationInfo(validity, serialNumber));
		}
	}

	@Test
	public void testRealNot() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/realts2019.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/realts2019.crt")) {
			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity wrongIssuerCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertNotNull(wrongIssuerCRL);
			assertNull(wrongIssuerCRL.getIssuerToken());
			assertNotNull(wrongIssuerCRL.getThisUpdate());
			assertNotNull(wrongIssuerCRL.getNextUpdate());
			assertTrue(wrongIssuerCRL.isIssuerX509PrincipalMatches());
			assertNotNull(wrongIssuerCRL.getSignatureInvalidityReason());
			assertFalse(wrongIssuerCRL.isValid());
			assertEquals(SignatureAlgorithm.RSA_SHA256, wrongIssuerCRL.getSignatureAlgorithm());
		}
	}

	@Test
	public void testECDSA() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/EE-GovCA2018.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/EE-GovCA2018.pem.crt")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validity = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertEquals(SignatureAlgorithm.ECDSA_SHA512, validity.getSignatureAlgorithm());
			assertNotNull(validity.getThisUpdate());
			assertNotNull(validity.getNextUpdate());
			assertNull(validity.getExpiredCertsOnCRL());
			assertNotNull(validity.getIssuerToken());
			assertTrue(validity.isValid());
		}
	}

	@Test
	public void testECDSAwithRSACert() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/EE-GovCA2018.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/citizen_ca.cer")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLValidity validity = CRLUtils.buildCRLValidity(crlBinary, certificateToken);

			assertEquals(SignatureAlgorithm.ECDSA_SHA512, validity.getSignatureAlgorithm());
			assertNotNull(validity.getThisUpdate());
			assertNotNull(validity.getNextUpdate());
			assertNull(validity.getExpiredCertsOnCRL());
			assertNull(validity.getIssuerToken());
			assertFalse(validity.isValid());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPSSwithoutBouncyCastle() throws Exception {
		try (InputStream is = AbstractTestCRLUtils.class.getResourceAsStream("/d-trust_root_ca_1_2017.crl");
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/D-TRUST_Root_CA_1_2017.crt")) {

			CertificateToken certificateToken = loadCert(isCer);

			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLUtils.buildCRLValidity(crlBinary, certificateToken);
		}
	}

	@Test(expected = DSSException.class)
	public void incompleteCRL() throws Exception {
		try (InputStream is = new ByteArrayInputStream(new byte[] { 1, 2, 3 });
				InputStream isCer = AbstractTestCRLUtils.class.getResourceAsStream("/belgiumrs2.crt")) {
			CertificateToken certificateToken = loadCert(isCer);
			CRLBinary crlBinary = new CRLBinary(toByteArray(is));
			CRLUtils.buildCRLValidity(crlBinary, certificateToken);
		}
	}

	@Test
	public void rsaSHA1() throws Exception {
		String crlB64 = "MIIBbTBXMA0GCSqGSIb3DQEBBQUAMCgxCzAJBgNVBAYTAkJFMRkwFwYDVQQDExBCZWxnaXVtIFJvb3QgQ0EyFw0xNDA3MDExMTAwMDBaFw0xNTAxMzExMTAwMDBaMA0GCSqGSIb3DQEBBQUAA4IBAQClCqf+EHb/ZafCIrRXdEmIOrHV0fFYfIbLEWUhMLIDBdNgcDeKjUOB6dc3WnxfyuE4RzndBbZA1dlDv7wEX8sxaGzAdER166uDS/CF7wwVz8voDq+ju5xopN01Vy7FNcCA43IpnZal9HPIQfb2EyrfNu5hQal7WiKE7q8PSch1vBlB9h8NbyIfnyPiHZ7A0B6MPJBqSCFwgGm+YZB/4DQssOVui0+kBT19uUBjTG0QEe7dLxZTBEgBowq5axv93QBXe0j+xOXZ97tlU2iJ51bsLY3E134ziMV6hKPsBw6ARMq/BF64P6axLIUOqdCRaYoMu2ekfYSoFuaM3l2o79aw";
		String certB64 = "MIIDjjCCAnagAwIBAgIIKv++n6Lw6YcwDQYJKoZIhvcNAQEFBQAwKDELMAkGA1UEBhMCQkUxGTAXBgNVBAMTEEJlbGdpdW0gUm9vdCBDQTIwHhcNMDcxMDA0MTAwMDAwWhcNMjExMjE1MDgwMDAwWjAoMQswCQYDVQQGEwJCRTEZMBcGA1UEAxMQQmVsZ2l1bSBSb290IENBMjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMZzQh6S/3UPi790hqc/7bIYLS2X+an7mEoj39WN4IzGMhwWLQdC1i22bi+n9fzGhYJdld61IgDMqFNAn68KNaJ6x+HK92AQZw6nUHMXU5WfIp8MXW+2QbyM69odRr2nlL/zGsvU+40OHjPIltfsjFPekx40HopQcSZYtF3CiInaYNKJIT/e1wEYNm7hLHADBGXvmAYrXR5i3FVr/mZkIV/4L+HXmymvb82fqgxG0YjFnaKVn6w/Fa7yYd/vw2uaItgscf1YHewApDgglVrH1Tdjuk+bqv5WRi5j2Qsj1Yr6tSPwiRuhFA0m2kHwOI8w7QUmecFLTqG4flVSOmlGhHUCAwEAAaOBuzCBuDAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zBCBgNVHSAEOzA5MDcGBWA4CQEBMC4wLAYIKwYBBQUHAgEWIGh0dHA6Ly9yZXBvc2l0b3J5LmVpZC5iZWxnaXVtLmJlMB0GA1UdDgQWBBSFiuv0xbu+DlkDlN7WgAEV4xCcOTARBglghkgBhvhCAQEEBAMCAAcwHwYDVR0jBBgwFoAUhYrr9MW7vg5ZA5Te1oABFeMQnDkwDQYJKoZIhvcNAQEFBQADggEBAFHYhd27V2/MoGy1oyCcUwnzSgEMdL8rs5qauhjyC4isHLMzr87lEwEnkoRYmhC598wUkmt0FoqW6FHvv/pKJaeJtmMrXZRY0c8RcrYeuTlBFk0pvDVTC9rejg7NqZV3JcqUWumyaa7YwBO+mPyWnIR/VRPmPIfjvCCkpDZoa01gZhz5v6yAlGYuuUGK02XThIAC71AdXkbc98m6tTR8KvPG2F9fVJ3bTc0R5/0UAoNmXsimABKgX77OFP67H6dh96tK8QYUn8pJQsKpvO2FsauBQeYNxUJpU4c5nUwfAA4+Bw11V0SoU7Q2dmSZ3G7rPUZuFF1eR1ONeE3gJ7uOhXY=";

		try (InputStream crlIS = new ByteArrayInputStream(Base64.getDecoder().decode(crlB64));
				InputStream certIS = new ByteArrayInputStream(Base64.getDecoder().decode(certB64))) {
			CertificateToken certificateToken = loadCert(certIS);
			CRLBinary crlBinary = new CRLBinary(toByteArray(crlIS));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertTrue(validCRL.isSignatureIntact());
			assertTrue(validCRL.isValid());
			assertEquals(SignatureAlgorithm.RSA_SHA1, validCRL.getSignatureAlgorithm());
		}
	}

	@Test
	public void rsaSHA1bis() throws Exception {
		String crlB64 = "MIICbTBXMA0GCSqGSIb3DQEBBQUAMCgxCzAJBgNVBAYTAkJFMRkwFwYDVQQDExBCZWxnaXVtIFJvb3QgQ0EzFw0xNDA3MDExMTAwMDBaFw0xNTAxMzExMTAwMDBaMA0GCSqGSIb3DQEBBQUAA4ICAQBgfKMetb6Qy9Jb06wIKEvHAOChVVm9rONBFUyaV2YEgtJFON+RosigKDhgTrS/Q2Ja6ZeQQl65PxxU63HzkgHtaZaS4MmFs6uIr58gJFyer4SDT/6YizwajlNFtuGvzauuxjA4XpfPd42xpPg4rrh6+7chEJ05kxJoPR5VWxmdMHQ3ne9AAtczV8DuVZsFD3eMdaxKBV0iFe9bBqy5jGaaZVJVbMKEB7kzAhNXOxuoYnXrsYP0w3D5IXj5EUeuWyg1GAzQ9lf0lqzax+2VtR89O/EwVzgrTggqLt/G3jNz13Z9LxSLGg0CaSAcjEGscqVpEgiZO9ZR4hOt/d1Fb+h09uYhFp+og2oQBT43CJlt7q1WC/L4BhIYg10YJPC69hgP3mqwbJr4R7/6FaJhBwsgw6NxrmgoefNb9G8bT2w4/vV8W4kkFEVa38YkWSmuyTgJU40IYeKGEIamXnbmWki6zXPYRApo873qRd/FN2iTU9781bSgZ/QOi2Z98ZJAEwcYRgGtfpTGsCvA8Fg4DaVReJfEGog1p22XQefO2cLJCcXCdibFReQOB+TpFfBltdoF8MFGZ1qc1McSfpr1C3U80IyYmc+kP/EZIQlZKEoGg3B81j9X2Y+6uBWlkTa3Ki4VLsItHNc4FRVsqQ+X2swA8iPc6pEURDBcBbA3pKaQSA==";
		String certB64 = "MIIFjjCCA3agAwIBAgIIOyEC3pZbHakwDQYJKoZIhvcNAQEFBQAwKDELMAkGA1UEBhMCQkUxGTAXBgNVBAMTEEJlbGdpdW0gUm9vdCBDQTMwHhcNMTMwNjI2MTIwMDAwWhcNMjgwMTI4MTIwMDAwWjAoMQswCQYDVQQGEwJCRTEZMBcGA1UEAxMQQmVsZ2l1bSBSb290IENBMzCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAKjyAZ2Lg8kHoIX7JLc3BeZ1Tzy9MEv7Bnr59xcJezc/xJJdO4V3bwMltKFfNvqsQ5H/GQADFJ0GmTLLPDI5AoeUjBubRZ9hwruUuQ11+vhtoVhuEuZUxofEIU2yJtiSOONwpo/GIb9C4YZ5h+7ltDpC3MvsFyyordpzgwqSHvFwTCmls5SpU05UbF7ZVPcfVf24A5IgHLpZTgQfAvnzPlm++eJY+sNoNzTBoe6iZphmPbxuPNcJ6slV8qMQQk50/g+KmoPpHX4AvoTr4/7TMTvuK8jS1dEn+fdVKdx9qo9ZZRHFW/TXEn5SrNUu99xhzlE/WBurrVwFoKCWCjmO0CnekJlw0NTr3HBTG5D4AiDjNFUYaIcGJk/ha9rzHzY+WpGdoFZxhbP83ZGeoqkgBr8UzfOFCY8cyUN2db6hpIaK6Nuoho6QWnn+TSNh5Hjui5miqpGxS73gYlT2Qww16h8gFTJQ49fiS+QHlwRw5cqFuqfFLE3nFFF9KIamS4TSe7T4dNGY2VbHzpaGVT4wy+fl7gWsfaUkvhM4b00DzgDiJ9BHiKytNLmzoa3Sneij/CKur0dJ5OdMiAqUpSd0Oe8pdIbmQm1oP5cjckiQjxx7+vSxWtacpGowWK8+7oEsYc+7fLt3GD6q/O5Xi440Pd/sFJmfqRf3C1PPMdBqXcwjAgMBAAGjgbswgbgwDgYDVR0PAQH/BAQDAgEGMA8GA1UdEwEB/wQFMAMBAf8wQgYDVR0gBDswOTA3BgVgOAoBATAuMCwGCCsGAQUFBwIBFiBodHRwOi8vcmVwb3NpdG9yeS5laWQuYmVsZ2l1bS5iZTAdBgNVHQ4EFgQUuLxsAI9bGYWdJQGc8BncQI7QOCswEQYJYIZIAYb4QgEBBAQDAgAHMB8GA1UdIwQYMBaAFLi8bACPWxmFnSUBnPAZ3ECO0DgrMA0GCSqGSIb3DQEBBQUAA4ICAQBFYjv/mKX+VcyxEacckgx4L8XvFkIFPXzjEnDnAtCCkROU/k5n1jjVK+ODOn+Q4kJg6Nd7K47+zTXcrSe1tB2gVMsyaCN9scy4phLX1qT48sThCjUtooxfIoRycpdlf14HcUPCYlASTCapZU0MnAbzfpzxm49Ik/A2JWxAhxXVRHwOu3TMGiQ4W/VyVawxjwQMO8TneBDombmkXsI9bI0OxWUh2A5dKlqu0sYvE0dz8xDxr9ZkmZqYcPIKizCZlaP1ZsSlCi5S31gn3EUP+fd21q6ZXgU+50/qgoh/0UUaHRpedPQBES/FYc2IQZ2XjhmeTwM+9Lk7tnzHeHp3dgCoOfceyPUaVkWiXMWcNAvvkDVELvXfJpRxwcRfS5Ks5oafOfj81RzGUbmpwl2usOeCRwdWE8gPvbfWNQQC8MJquDl5HdeuzUesTXUqXeEkyAOo6YnF3g0qGcLI9NXusji1egRUZ7B4XCvG52lTB7Wgd/wVFzS3f4mAmYTGJXH+N/lrBBGKuTJ5XncJaliFUKxGP6VmNyaaLUF5IlTqC9CGHPLSXOgDokt2G9pNwFm2t7AcpwAmegkMNpgcgTd+qk2yljEaT8wf953jUAFedbpN3tX/3i+uvHOOmWjQOxJg2lVKkC+bkWa2FrTBDdrlEWVaLrY+M+xeIctrC0WnP7u4xg==";

		try (InputStream crlIS = new ByteArrayInputStream(Base64.getDecoder().decode(crlB64));
				InputStream certIS = new ByteArrayInputStream(Base64.getDecoder().decode(certB64))) {
			CertificateToken certificateToken = loadCert(certIS);
			CRLBinary crlBinary = new CRLBinary(toByteArray(crlIS));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertTrue(validCRL.isSignatureIntact());
			assertTrue(validCRL.isValid());
			assertEquals(SignatureAlgorithm.RSA_SHA1, validCRL.getSignatureAlgorithm());
		}
	}

	@Test
	public void rsaSHA256() throws Exception {
		String crlB64 = "MIIDbjCCAlYCAQEwDQYJKoZIhvcNAQELBQAwKDELMAkGA1UEBhMCQkUxGTAXBgNVBAMTEEJlbGdpdW0gUm9vdCBDQTIXDTE5MDEwMTExMDAwMFoXDTE5MDczMTExMDAwMFowggHHMCECEDW3OvEPBB82u9j/4UlnHXcXDTE2MDIxNTExMDAwMFowIQIQL1fHogsVxmfMBka5q4uzaRcNMTYwMjE1MTEwMDAwWjAhAhBEWWaaO6WPpV7s98GLjiefFw0xNjAyMTUxMTAwMDBaMCECEC4s3llxVj+aWTyP3S3Pj1UXDTE2MDIxNTExMDAwMFowIQIQTUVQV0lyPD0vIT44fF4uZBcNMTYwMjE1MTEwMDAwWjAhAhBUFJ/4idyxMHdFlLgoUIk9Fw0xNjAyMTUxMTAwMDBaMCECEFVLWoTsL75iRcJb5hg2T+cXDTE2MDIxNTExMDAwMFowIQIQeLumDUO40KwnecZLJxFM2BcNMTYwMjE1MTEwMDAwWjAhAhBl4waBnJUE6bod+GIj6MTPFw0xNjAyMTUxMTAwMDBaMCECEGpqd0JtRXkuLFZHNlEicWUXDTE2MDIxNTExMDAwMFowIQIQRH7WhshwXRK6f0VfOfjXgRcNMTYwMjE1MTEwMDAwWjAhAhB+uPmNO6oGdDh+WM+9VTcoFw0xNjAyMTUxMTAwMDBaMCECEEiVlv1B8vdvwaYrBfEM2YQXDTE2MDIxNTExMDAwMFqgLzAtMAoGA1UdFAQDAgEEMB8GA1UdIwQYMBaAFIWK6/TFu74OWQOU3taAARXjEJw5MA0GCSqGSIb3DQEBCwUAA4IBAQB+GEV0MQQB+iPKDO9hGAGMjIEILK6m3bTdDNDBchQPoCSFTpNbKglq0c4NnAh/nUYTW8i0vENnPzVuL90xV7K36lCjaZDQOjSPSwNDLEgCK4ONK7ReW0tbFPTkTiKYd59uGIm11x5/KtHxLCX44uctoN1ZkQNnmRP2HPs6Djhtt3LTAD1oZ/YC0tQdsZUGfaFDcdgCor5hyrOdgHh+quMapZ7Wm1J1eSzJQrxADaFWq3FPsNKLlqtFZQP9t6nrdVHyUktHv9Dyf+Xjm68nDya7rGW7n/LvCLJMYzcdGd1t8podI+VcPolUKdUuyfS5S98XunmyhFtUHZbtqQgQmuh/";
		String certB64 = "MIIDjjCCAnagAwIBAgIIKv++n6Lw6YcwDQYJKoZIhvcNAQEFBQAwKDELMAkGA1UEBhMCQkUxGTAXBgNVBAMTEEJlbGdpdW0gUm9vdCBDQTIwHhcNMDcxMDA0MTAwMDAwWhcNMjExMjE1MDgwMDAwWjAoMQswCQYDVQQGEwJCRTEZMBcGA1UEAxMQQmVsZ2l1bSBSb290IENBMjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMZzQh6S/3UPi790hqc/7bIYLS2X+an7mEoj39WN4IzGMhwWLQdC1i22bi+n9fzGhYJdld61IgDMqFNAn68KNaJ6x+HK92AQZw6nUHMXU5WfIp8MXW+2QbyM69odRr2nlL/zGsvU+40OHjPIltfsjFPekx40HopQcSZYtF3CiInaYNKJIT/e1wEYNm7hLHADBGXvmAYrXR5i3FVr/mZkIV/4L+HXmymvb82fqgxG0YjFnaKVn6w/Fa7yYd/vw2uaItgscf1YHewApDgglVrH1Tdjuk+bqv5WRi5j2Qsj1Yr6tSPwiRuhFA0m2kHwOI8w7QUmecFLTqG4flVSOmlGhHUCAwEAAaOBuzCBuDAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zBCBgNVHSAEOzA5MDcGBWA4CQEBMC4wLAYIKwYBBQUHAgEWIGh0dHA6Ly9yZXBvc2l0b3J5LmVpZC5iZWxnaXVtLmJlMB0GA1UdDgQWBBSFiuv0xbu+DlkDlN7WgAEV4xCcOTARBglghkgBhvhCAQEEBAMCAAcwHwYDVR0jBBgwFoAUhYrr9MW7vg5ZA5Te1oABFeMQnDkwDQYJKoZIhvcNAQEFBQADggEBAFHYhd27V2/MoGy1oyCcUwnzSgEMdL8rs5qauhjyC4isHLMzr87lEwEnkoRYmhC598wUkmt0FoqW6FHvv/pKJaeJtmMrXZRY0c8RcrYeuTlBFk0pvDVTC9rejg7NqZV3JcqUWumyaa7YwBO+mPyWnIR/VRPmPIfjvCCkpDZoa01gZhz5v6yAlGYuuUGK02XThIAC71AdXkbc98m6tTR8KvPG2F9fVJ3bTc0R5/0UAoNmXsimABKgX77OFP67H6dh96tK8QYUn8pJQsKpvO2FsauBQeYNxUJpU4c5nUwfAA4+Bw11V0SoU7Q2dmSZ3G7rPUZuFF1eR1ONeE3gJ7uOhXY=";

		try (InputStream crlIS = new ByteArrayInputStream(Base64.getDecoder().decode(crlB64));
				InputStream certIS = new ByteArrayInputStream(Base64.getDecoder().decode(certB64))) {
			CertificateToken certificateToken = loadCert(certIS);
			CRLBinary crlBinary = new CRLBinary(toByteArray(crlIS));
			CRLValidity validCRL = CRLUtils.buildCRLValidity(crlBinary, certificateToken);
			assertNotNull(validCRL);
			assertTrue(validCRL.isSignatureIntact());
			assertTrue(validCRL.isValid());
			assertEquals(SignatureAlgorithm.RSA_SHA256, validCRL.getSignatureAlgorithm());
		}
	}

	protected CertificateToken loadCert(InputStream is) throws CertificateException {
		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(is);
		return new CertificateToken(certificate);
	}

}
