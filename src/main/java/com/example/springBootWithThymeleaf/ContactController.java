package com.example.springBootWithThymeleaf;

import antlr.StringUtils;
import com.example.springBootWithThymeleaf.exception.BadResourceException;
import com.example.springBootWithThymeleaf.exception.ResourceAlreadyExistsException;
import com.example.springBootWithThymeleaf.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.debugger.Address;

@RestController
@RequestMapping("/api")
public class ContactController {

    private final int ROW_PER_PAGE = 5;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ContactService contactService;

    //ResponseEntity represents the whole HTTP response: status code, headers, and body.
    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Contact>> findAll(
            @RequestParam(value = "page", defaultValue = "1") int pageNumber,
            @RequestParam(required = false) String name
    ) {
        if (name == null) {
            return ResponseEntity.ok(contactService.findAll(pageNumber, ROW_PER_PAGE));
        } else {
            return ResponseEntity.ok(contactService.findAllByName(name, pageNumber, ROW_PER_PAGE));
        }
    }

    //difference between RequestParam and  PathVariable
    //Request param url: http://localhost:8080/foos?id=abc
    //PathVariable param url: http://localhost:8080/foos/abc
    @GetMapping(value = "/contacts/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> findContactById(@PathVariable long contactId) {
        try{
            Contact book = contactService.findById(contactId);
            return ResponseEntity.ok(book);
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(value = "/contacts")
    public ResponseEntity<Contact> addContact(@Validated @RequestBody Contact contact) throws URISyntaxException {
        try{
            Contact newContact = contactService.save(contact);
            return ResponseEntity.created(new URI("/api/contacts"+newContact.getId())).body(contact);
        }
        catch (ResourceAlreadyExistsException ex) {
            // log exception first, then return Conflict (409)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/contacts/{contactId}")
    public ResponseEntity<Contact> updateContact(@Validated @RequestBody Contact contact,@PathVariable long contactId){
        try {
            contact.setId(contactId);
            contactService.update(contact);
            return ResponseEntity.ok().build();
        }
        catch (ResourceNotFoundException ex) {
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//    @PatchMapping("/contacts/{contactId}")
//    public ResponseEntity<Void> updateAddress(@PathVariable long contactId,
//                                              @RequestBody Address address) {
//        try {
//            contactService.updateAddress(contactId, address);
//            return ResponseEntity.ok().build();
//        } catch (ResourceNotFoundException ex) {
//            // log exception first, then return Not Found (404)
//            logger.error(ex.getMessage());
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping(path = "/contacts/{contactId}")
    public ResponseEntity<Void> deleteContactById(@PathVariable long contactId){

        try {
            contactService.deleteById(contactId);
            return ResponseEntity.ok().build();
        }
        catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }

    }


}
