package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import com.amigoscode.s3.S3Buckets;
import com.amigoscode.s3.S3Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public CustomerService(
            @Qualifier("jdbc") CustomerDao customerDao,
            CustomerDTOMapper customerDTOMapper,
            PasswordEncoder passwordEncoder,
            S3Service s3Service,
            S3Buckets s3Buckets
    ) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest req) {
        String email = req.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("email already taken");
        }

        Customer customer = new Customer(
                req.name(),
                req.email(),
                passwordEncoder.encode(req.password()),
                req.age(),
                req.gender()
        );

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {
        if (!customerDao.existsCustomerById(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }
        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {
        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        boolean changes = false;

        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }


    public void uploadCustomerImage(Integer customerId, MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);

        if (file == null || file.isEmpty()) {
            throw new RequestValidationException("file is empty");
        }

        String key = "profile-images/%s/%s".formatted(customerId, file.getOriginalFilename());

        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    key,
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to upload file to S3", e);
        }
        //TODO: Store profileImageId to postgres SQL
    }

    public byte[] getuploadCustomerProfileImage(Integer customerId, String filename) {
        checkIfCustomerExistsOrThrow(customerId);

        String key = "profile-images/%s/%s".formatted(customerId, filename);
        return s3Service.getObject(s3Buckets.getCustomer(), key);
    }

    private void checkIfCustomerExistsOrThrow(Integer customerId) {
        if (!customerDao.existsCustomerById(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }
    }
}