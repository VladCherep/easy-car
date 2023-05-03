package by.easycar.service;

import by.easycar.exceptions.WrongOperationNameException;
import by.easycar.model.Payment;
import by.easycar.model.requests.PaymentRequest;
import by.easycar.model.user.UserPrivate;
import by.easycar.repository.PaymentRepository;
import by.easycar.service.mappers.PaymentMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class PaymentService {

    private final UserService userService;

    private final PaymentRepository paymentRepository;

    private final String SIGNING_KEY = "Vm0xNFlWbFdXWGhVV0doVVlURndUMVp0ZUdGV1ZsbDNZVVZPVjAxV2NEQlVWbHBQVlRBeFYyTkdiR0ZXVm5CUVZqQmtSMDVzV25KWGJHUnBVbXR3VEZaVldrWlBWa0pTVUZRd1BRPT1WbTE0WVZsV1dYaFVXR2hVWVRGd1QxWnRlR0ZXVmxsM1lVVk9WMDFXY0RCVVZscFBWVEF4VjJOR2JHRldWbkJRVmpCa1IwNXNXbkpYYkdScFVtdHdURlpWV2taUFZrSlNVRlF3UFE9PVZtMTRZVmxXV1hoVVdHaFVZVEZ3VDFadGVHRldWbGwzWVVWT1YwMVdjREJVVmxwUFZUQXhWMk5HYkdGV1ZuQlFWakJrUjA1c1duSlhiR1JwVW10d1RGWlZXa1pQVmtKU1VGUXdQUT09Vm0xNFlWbFdXWGhVV0doVVlURndUMVp0ZUdGV1ZsbDNZVVZPVjAxV2NEQlVWbHBQVlRBeFYyTkdiR0ZXVm5CUVZqQmtSMDVzV25KWGJHUnBVbXR3VEZaVldrWlBWa0pTVUZRd1BRPT1WbTE0WVZsV1dYaFVXR2hVWVRGd1QxWnRlR0ZXVmxsM1lVVk9WMDFXY0RCVVZscFBWVEF4VjJOR2JHRldWbkJRVmpCa1IwNXNXbkpYYkdScFVtdHdURlpWV2taUFZrSlNVRlF3UFE9PQ";

    private final int MINUTES_OF_EXPIRE = 10;

    private final Map<String, Integer> MAP_OF_OPERATIONS = new HashMap<>();

    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentService(UserService userService, PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        MAP_OF_OPERATIONS.put("10_ups", 10);
        MAP_OF_OPERATIONS.put("20_ups", 20);
        MAP_OF_OPERATIONS.put("30_ups", 30);

    }

    @Transactional
    public void doPay(String jwt) {
        PaymentRequest paymentRequest;
        try {
            paymentRequest = getPaymentRequestFromJwtToken(jwt);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | DecodingException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
        Integer ups = MAP_OF_OPERATIONS.entrySet().stream()
                .filter((x) -> x.getKey().equals(paymentRequest.getOperationName()))
                .findFirst()
                .orElseThrow(() -> new WrongOperationNameException("Wrong operation name"))
                .getValue();
        UserPrivate user = userService.getById(paymentRequest.getUserId());
        Payment payment = paymentMapper.getPaymentFromPaymentRequest(paymentRequest, user);
        Integer currentCountOfUps = user.getUps();
        user.setUps(currentCountOfUps + ups);
        paymentRepository.save(payment);
    }

    public String getToken(Map<String, String> paymentRequest) {
        Date currentDate = new Date();
        return Jwts.builder().setClaims(paymentRequest)
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + (MINUTES.toMillis(MINUTES_OF_EXPIRE))))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY).compact();
    }

    public PaymentRequest getPaymentRequestFromJwtToken(String token) {
        Claims map = Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token).getBody();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBankName((String) map.get("bankName"));
        paymentRequest.setUserId(Long.parseLong((String) map.get("userId")));
        paymentRequest.setOperationName((String) map.get("operationName"));
        paymentRequest.setTransactionNumber((String) map.get("transactionNumber"));
        return paymentRequest;
    }

    public List<Payment> getPaymentsOfUser(Long id) {
        UserPrivate userPrivate = new UserPrivate();
        userPrivate.setId(id);
        return paymentRepository.findByUser(userPrivate);
    }
}

