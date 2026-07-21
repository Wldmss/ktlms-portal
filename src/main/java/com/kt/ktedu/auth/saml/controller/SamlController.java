package com.kt.ktedu.auth.saml.controller;

import com.kt.ktedu.common.util.core.StringUtil;
import com.kt.ktedu.auth.jwt.JwtProvider;
import com.kt.ktedu.auth.jwt.dto.JwtDTO;
import com.kt.ktedu.auth.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObjectContentReference;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * SAML 2.0 IdP 컨트롤러 (genius {@code OpenSAMLController} 이관, OpenSAML 2.x → 4.3.2).
 *
 * <p>KT(ktedu)가 IdP 역할을 하고, SP(Ping Identity {@code PingConnect} → Udemy)로 서명된
 * {@code SAMLResponse} 를 auto-submit 한다. genius 는 로그인 세션(memberInfo)으로 사용자를 식별했지만,
 * portal 은 <b>JWT access token</b> 으로 로그인 사용자를 확인한다.</p>
 *
 * <p>URL: {@code /sso/{channel}/login|saml/{email}|metadata} — {@code channel} 은 자유형(현재 udemy 만 사용).</p>
 *
 * <pre>
 * [엔드포인트]   ssoLogin, samlLoginApi(TEST), getMetadata, downloadMetadata
 * [로그인 처리]  handleSsoLogin → samlLogin
 * [응답 생성]    parseSAMLRequest, createSamlResponse
 * [서명/인증서]  addSignature, createKeyInfo, getCredential
 * [세션/사용자]  resolveUser, resolveSamlEmail, storeSamlSession 등
 * </pre>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sso/{channel}")
public class SamlController {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final String SAML_REQUEST = "SAMLRequest";
    private static final String RELAY_STATE = "RelayState";
    private static final String SAML_CHANNEL = "SAMLChannel";

    private final JwtProvider jwtProvider;
    private final LoginService loginService;

    @Value("classpath:saml/private-kt.key")
    private Resource privateKey;

    @Value("classpath:saml/certificate-kt.crt")
    private Resource certPath;

    @Value("${saml.idp.entityid:ktedu.kt.com}")
    private String idpEntityId;

    @Value("${saml.idp.sso.url:}")
    private String idpSsoUrl;

    @Value("${saml.sp.entityid:PingConnect}")
    private String spEntityId;

    @Value("${saml.sp.acs.url:}")
    private String spAcsUrl;

    /**
     * OpenSAML 초기화
     */
    private void initOpenSaml() throws Exception {
        if (INITIALIZED.compareAndSet(false, true)) {
            InitializationService.initialize();
        }
    }

    /**
     * udemy 에서 ktedu.com 으로 SSO 로그인 요청
     * /sso/udemy/login 로 접속
     * >> ktedu 에 로그인이 되어 있다면 -> 해당 로직 실행
     * >> ktedu 에 로그인이 안되어 있다면 -> 로그인 이후 해당 로직 실행
     */
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String ssoLogin(@PathVariable String channel,
                           @RequestParam Map<String, String> input,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        // udemy saml 데이터
        String samlRequest = StringUtil.firstNonBlank(input.get(SAML_REQUEST), sessionValue(request, SAML_REQUEST));
        String relayState = StringUtil.firstNonBlank(input.get(RELAY_STATE), sessionValue(request, RELAY_STATE));

        // saml session 저장
        HttpSession session = request.getSession(true);
        if (!StringUtil.isBlankParam(samlRequest)) {
            session.setAttribute(SAML_REQUEST, samlRequest);
        }
        if (!StringUtil.isBlankParam(relayState)) {
            session.setAttribute(RELAY_STATE, relayState);
        }
        session.setAttribute(SAML_CHANNEL, channel);

        // 로그인 check
        JwtDTO user = resolveUser(request);
        if (user == null) {
            // 로그인 안된 경우 로그인 페이지로 이동
            return "redirect:/login";
        }

        return samlLogin(channel, resolveSamlEmail(user), samlRequest, relayState, request, response);
    }

    /**
     * email 을 직접 지정하는 SAML 로그인 (TEST 용)
     * 로그인 세션 없이 {@code {email}} 을 NameID 로 바로 SAMLResponse 를 만든다.
     */
    @RequestMapping(value = "/saml/{email}", method = {RequestMethod.GET, RequestMethod.POST})
    public String samlLoginApi(@PathVariable String channel,
                               @PathVariable String email,
                               @RequestParam Map<String, String> input,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        return samlLogin(channel, email, input.get(SAML_REQUEST), input.get(RELAY_STATE), request, response);
    }

    /**
     * IdP 메타데이터(EntityDescriptor XML) 생성 — SP 에 등록할 IdP 정보(서명 인증서, SSO URL)
     */
    @GetMapping("/metadata")
    public void getMetadata(@PathVariable String channel,
                            @RequestParam(value = "downloadFlag", required = false) Boolean downloadFlag,
                            HttpServletResponse response) throws Exception {
        try {
            initOpenSaml();

            // EntityDescriptor (IdP entityId)
            EntityDescriptor entityDescriptor = build(EntityDescriptor.DEFAULT_ELEMENT_NAME);
            entityDescriptor.setEntityID(idpEntityId);

            // IDPSSODescriptor: SAML2 프로토콜, AuthnRequest 서명 요구
            IDPSSODescriptor idpDescriptor = build(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
            idpDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
            idpDescriptor.setWantAuthnRequestsSigned(true);

            // KeyDescriptor: 서명용 X.509 공개키(KeyInfo)
            KeyDescriptor keyDescriptor = build(KeyDescriptor.DEFAULT_ELEMENT_NAME);
            keyDescriptor.setUse(UsageType.SIGNING);
            keyDescriptor.setKeyInfo(createKeyInfo(getCredential()));
            idpDescriptor.getKeyDescriptors().add(keyDescriptor);

            // SingleSignOnService: POST 바인딩, SSO 진입 URL
            SingleSignOnService ssoService = build(SingleSignOnService.DEFAULT_ELEMENT_NAME);
            ssoService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
            ssoService.setLocation(StringUtil.defaultIfBlank(idpSsoUrl, "/sso/" + channel + "/login"));
            idpDescriptor.getSingleSignOnServices().add(ssoService);

            entityDescriptor.getRoleDescriptors().add(idpDescriptor);
            String metadata = SerializeSupport.nodeToString(XMLObjectSupport.marshall(entityDescriptor));

            response.setContentType("application/xml;charset=UTF-8");
            if (Boolean.TRUE.equals(downloadFlag)) {
                // 파일 다운로드: kt_idp_metadata_<yyyyMMddHHmmss>.xml
                String ts = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"kt_idp_metadata_" + ts + ".xml\"");
            }
            response.getWriter().write(metadata);
        } catch (Exception e) {
            log.error("SAML metadata generation failed. channel={}", channel, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SAML metadata generation failed");
        }
    }

    /**
     * IdP 메타데이터 파일 다운로드 (downloadFlag=true 로 위임)
     */
    @GetMapping("/metadata/download")
    public void downloadMetadata(@PathVariable String channel, HttpServletResponse response) throws Exception {
        getMetadata(channel, true, response);
    }

    /*======================= 로그인 처리 =======================*/

    /**
     * SAML 로그인 본 처리: {@code SAMLRequest} 파싱 → 서명된 {@code SAMLResponse} 생성 →
     * SP(ACS)로 POST 하는 auto-submit HTML 폼 응답. (genius: {@code samlLogin} — "udemy saml login")
     */
    private String samlLogin(String channel,
                             String email,
                             String samlRequest,
                             String relayState,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        // 필수값 검증 (없으면 SP 에러 리다이렉트)
        if (StringUtil.isBlankParam(samlRequest)) {
            return redirectSp(request, channel, "SAMLRequest is empty");
        }
        if (StringUtil.isBlankParam(email)) {
            return redirectSp(request, channel, "email is empty");
        }

        if (channel == null || channel.equals("")) channel = "udemy";

        try {
            initOpenSaml();
            AuthnRequest authnRequest = parseSAMLRequest(samlRequest);              // SAMLRequest → AuthnRequest
            String samlResponseXml = createSamlResponse(authnRequest, email);       // 서명된 SAMLResponse XML
            String encodedResponse = Base64.getEncoder()                            // Base64 인코딩
                    .encodeToString(samlResponseXml.getBytes(StandardCharsets.UTF_8));
            String escapedRelayState = HtmlUtils.htmlEscape(StringUtil.defaultIfBlank(relayState, ""));
            String acsUrl = resolveAcsUrl(authnRequest);                            // SP ACS(응답 수신) URL

            // SP ACS 로 SAMLResponse 를 POST 하는 auto-submit HTML 폼
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("""
                    <html><head><link rel="stylesheet" href="/resources/legacy/newPortal/css/new23.css"></head><body>
                    <form method="POST" action="%s">
                        <input type="hidden" name="SAMLResponse" value="%s"/>
                        <input type="hidden" name="RelayState" value="%s"/>
                    </form>
                    <div class="loading-block" id="loading-block"><p></p></div>
                    <script>document.forms[0].submit();</script>
                    </body></html>
                    """.formatted(HtmlUtils.htmlEscape(acsUrl), encodedResponse, escapedRelayState));
            removeSamlSession(request);   // 사용 끝난 SAML 세션 속성 정리
            return null;
        } catch (Exception e) {
            log.error("SAML login failed. channel={}, email={}", channel, email, e);
            return redirectSp(request, channel, e.getMessage());
        }
    }

    /*======================= SAMLResponse 생성 =======================*/

    /**
     * {@code SAMLRequest}(SP 발) 를 {@link AuthnRequest} 로 변환.
     * Base64 디코딩 → (DEFLATE 압축이면) 해제 → XML 파싱/unmarshall. (수신 요청 서명검증은 하지 않음)
     */
    private AuthnRequest parseSAMLRequest(String samlRequest) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(samlRequest);   // Base64 디코딩
        byte[] xmlBytes = inflateOrRaw(decoded);                    // DEFLATE 압축 해제(redirect binding)
        XMLObject xmlObject;
        try (InputStream is = new ByteArrayInputStream(xmlBytes)) { // XML 파싱 + unmarshall
            xmlObject = XMLObjectSupport.unmarshallFromInputStream(Objects.requireNonNull(XMLObjectProviderRegistrySupport.getParserPool()), is);
        }
        if (xmlObject instanceof AuthnRequest authnRequest) {
            return authnRequest;
        }
        throw new IllegalArgumentException("SAMLRequest is not AuthnRequest");
    }

    /**
     * DEFLATE(raw) 압축 해제. 압축이 아니면(POST 바인딩 등) 원본 바이트 그대로 반환
     */
    private byte[] inflateOrRaw(byte[] decoded) {
        try (InflaterInputStream inflaterInputStream =
                     new InflaterInputStream(new ByteArrayInputStream(decoded), new Inflater(true));
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            inflaterInputStream.transferTo(buffer);
            return buffer.toByteArray();
        } catch (IOException e) {
            return decoded;
        }
    }

    /**
     * 서명된 {@code SAMLResponse} XML 생성.
     * Assertion(Conditions/Subject/AuthnStatement/AttributeStatement) 구성 후 Assertion·Response 를 각각 서명한다.
     * NameID·email 속성 = 사용자 이메일, 유효기간 ±10분, bearer 확인, PASSWORD 인증컨텍스트.
     */
    private String createSamlResponse(AuthnRequest authnRequest, String email) throws Exception {
        Instant now = Instant.now();
        String requestId = authnRequest.getID();                    // 원 AuthnRequest ID (InResponseTo 로 되돌려줌)
        String assertionId = "KT_" + UUID.randomUUID();
        String responseId = "KT_" + UUID.randomUUID();
        String sessionIdx = "KT_" + UUID.randomUUID();
        String acsUrl = resolveAcsUrl(authnRequest);                // SP ACS(응답 수신) URL
        String targetSpEntityId = StringUtil.defaultIfBlank(spEntityId, authnRequest.getIssuer() == null ? null : authnRequest.getIssuer().getValue());

        BasicX509Credential credential = getCredential();           // 서명용 인증서 + 개인키

        // Assertion Issuer (IdP)
        Issuer assertionIssuer = build(Issuer.DEFAULT_ELEMENT_NAME);
        assertionIssuer.setValue(idpEntityId);

        // Assertion
        Assertion assertion = build(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID(assertionId);
        assertion.setIssueInstant(now);
        assertion.setVersion(SAMLVersion.VERSION_20);
        assertion.setIssuer(assertionIssuer);

        // Assertion 유효성 조건 (NotBefore/NotOnOrAfter ±10분, Audience = SP)
        Conditions conditions = build(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore(now.minusSeconds(600));
        conditions.setNotOnOrAfter(now.plusSeconds(600));

        Audience audience = build(Audience.DEFAULT_ELEMENT_NAME);
        audience.setURI(targetSpEntityId);

        AudienceRestriction audienceRestriction = build(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        assertion.setConditions(conditions);

        // Subject: 사용자 식별 (NameID = email) + bearer SubjectConfirmation
        NameID nameID = build(NameID.DEFAULT_ELEMENT_NAME);
        nameID.setFormat(NameIDType.UNSPECIFIED);
        nameID.setSPNameQualifier(targetSpEntityId);
        nameID.setValue(email);

        SubjectConfirmation subjectConfirmation = build(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

        SubjectConfirmationData confirmationData = build(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        confirmationData.setRecipient(acsUrl);
        confirmationData.setInResponseTo(requestId);
        confirmationData.setNotOnOrAfter(now.plusSeconds(600));
        subjectConfirmation.setSubjectConfirmationData(confirmationData);

        Subject subject = build(Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID(nameID);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        assertion.setSubject(subject);

        // AuthnStatement: 인증 정보 (Password 컨텍스트)
        AuthnStatement authn = build(AuthnStatement.DEFAULT_ELEMENT_NAME);
        authn.setAuthnInstant(now);
        authn.setSessionNotOnOrAfter(now.plusSeconds(600));
        authn.setSessionIndex(sessionIdx);

        AuthnContext authnContext = build(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef classRef = build(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        classRef.setURI(AuthnContext.PASSWORD_AUTHN_CTX);
        authnContext.setAuthnContextClassRef(classRef);
        authn.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authn);

        // AttributeStatement: 추가 속성 (email)
        AttributeStatement attributeStatement = build(AttributeStatement.DEFAULT_ELEMENT_NAME);
        Attribute attribute = build(Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setName("email");
        attribute.setNameFormat(Attribute.UNSPECIFIED);

        XSString attributeValue = (XSString) XMLObjectSupport.buildXMLObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attributeValue.setValue(email);
        attribute.getAttributeValues().add(attributeValue);
        attributeStatement.getAttributes().add(attribute);
        assertion.getAttributeStatements().add(attributeStatement);

        // Assertion 서명
        addSignature(assertion, credential);

        // Issuer (Response)
        Issuer responseIssuer = build(Issuer.DEFAULT_ELEMENT_NAME);
        responseIssuer.setValue(idpEntityId);

        // Response (서명된 Assertion 포함)
        Response samlResponse = build(Response.DEFAULT_ELEMENT_NAME);
        samlResponse.setID(responseId);
        samlResponse.setIssueInstant(now);
        samlResponse.setVersion(SAMLVersion.VERSION_20);
        samlResponse.setDestination(acsUrl);
        samlResponse.setIssuer(responseIssuer);
        samlResponse.setInResponseTo(requestId);
        samlResponse.getAssertions().add(assertion);

        // Status (SUCCESS)
        Status status = build(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = build(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);
        samlResponse.setStatus(status);

        // Response 서명 후 XML 문자열 반환
        return SerializeSupport.nodeToString(addSignature(samlResponse, credential));
    }

    // 서명 추가
    private Element addSignature(SignableSAMLObject object, BasicX509Credential credential) throws Exception {
        Signature signature = build(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(credential);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setKeyInfo(createKeyInfo(credential));
        object.setSignature(signature);

        if (signature.getContentReferences().isEmpty()) {
            SAMLObjectContentReference ref = new SAMLObjectContentReference(object);
            ref.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
            signature.getContentReferences().add(ref);
        } else if (signature.getContentReferences().getFirst() instanceof SAMLObjectContentReference ref) {
            ref.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        }

        Element element = XMLObjectSupport.marshall(object);
        Signer.signObject(signature);
        return element;
    }

    // KeyInfo 생성
    private KeyInfo createKeyInfo(BasicX509Credential credential) throws Exception {
        X509KeyInfoGeneratorFactory keyFactory = new X509KeyInfoGeneratorFactory();
        keyFactory.setEmitEntityCertificate(true);
        return keyFactory.newInstance().generate(credential);
    }

    // X.509 인증서
    private BasicX509Credential getCredential() throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate;
        try (InputStream certInput = certPath.getInputStream()) {
            certificate = (X509Certificate) certificateFactory.generateCertificate(certInput);
        }

        PrivateKey signingKey;
        try (PEMParser pemParser = new PEMParser(new InputStreamReader(privateKey.getInputStream(), StandardCharsets.UTF_8))) {
            Object obj = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            if (obj instanceof PEMKeyPair pemKeyPair) {
                PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();
                signingKey = converter.getPrivateKey(privateKeyInfo);
            } else if (obj instanceof PrivateKeyInfo privateKeyInfo) {
                signingKey = converter.getPrivateKey(privateKeyInfo);
            } else {
                throw new IllegalStateException("Unsupported SAML private key format");
            }
        }

        return new BasicX509Credential(certificate, signingKey);
    }

    // saml build 공통
    @SuppressWarnings("unchecked")
    private <T extends XMLObject> T build(javax.xml.namespace.QName qName) {
        return (T) XMLObjectProviderRegistrySupport.getBuilderFactory()
                .getBuilderOrThrow(qName)
                .buildObject(qName);
    }

    // 로그인 user check
    private JwtDTO resolveUser(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveAccessToken(request);
        if (StringUtil.isBlankParam(accessToken)) {
            return null;
        }
        try {
            jwtProvider.validateAccessToken(accessToken);
            return jwtProvider.getUserInfoFromAccessToken(accessToken);
        } catch (Exception e) {
            log.debug("SAML login requested without valid JWT: {}", e.getMessage());
            return null;
        }
    }

    // email -> userId check
    private String resolveSamlEmail(JwtDTO user) {
        String email = loginService.getUserEmail(user.getUserId());
        return StringUtil.firstNonBlank(email, user.getUserId(), user.getUserNm());
    }

    // acs url
    private String resolveAcsUrl(AuthnRequest authnRequest) {
        return StringUtil.defaultIfBlank(spAcsUrl, authnRequest.getAssertionConsumerServiceURL());
    }

    // saml session clear
    private void removeSamlSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SAML_REQUEST);
            session.removeAttribute(RELAY_STATE);
            session.removeAttribute(SAML_CHANNEL);
        }
    }

    // session value 조회
    private String sessionValue(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute(name);
        return value == null ? null : String.valueOf(value);
    }

    // saml 오류시 리턴
    private String redirectSp(HttpServletRequest request, String channel, String message) {
        log.error("[ERROR] samlLogin ({}) : {}", channel, message);
        removeSamlSession(request);
        return "redirect:/login?sso=saml&message=" + java.net.URLEncoder.encode("잘못된 요청입니다. 관리자에게 문의해주세요.", StandardCharsets.UTF_8);
    }
}
